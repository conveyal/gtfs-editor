package datastore;

import com.beust.jcommander.internal.Maps;
import com.csvreader.CsvReader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.WKTReader;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import models.Account;
import models.transit.*;
import org.joda.time.LocalDate;
import org.mapdb.DBMaker;
import org.mapdb.Fun;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.opentripplanner.common.LoggingUtil.human;

/**
 * Migrate a Postgres database dump to the MapDB format.
 */
public class MigrateToMapDB {
    private GlobalTx gtx;
    private File fromDirectory;

    private static GeometryFactory gf = new GeometryFactory();

    /** keep track of transactions for all agencies */
    private Map<String, AgencyTx> atxes = Maps.newHashMap();

    /** cache shapes; use a mapdb so it's not huge */
    private Map<String, LineString> shapeCache = DBMaker.newTempHashMap();

    /** cache stop times: Tuple2<Trip ID, Stop Sequence> -> StopTime */
    private NavigableMap<Fun.Tuple2<String, Integer>, StopTime> stopTimeCache = DBMaker.newTempTreeMap();

    /** cache stop times: Tuple2<Trip ID, Stop Sequence> -> TripPatternStop */
    private NavigableMap<Fun.Tuple2<String, Integer>, TripPatternStop> patternStopCache = DBMaker.newTempTreeMap();

    /** cache exception dates, Exception ID -> Date */
    private Multimap<String, LocalDate> exceptionDates = HashMultimap.create();

    /** cache custom calendars, exception ID -> calendar ID*/
    private Multimap<String, String> exceptionCalendars = HashMultimap.create();

    /** route ID -> agency ID, needed because we need the agency ID to get a reference to the route . . . */
    TLongLongMap routeAgencyMap = new TLongLongHashMap();

    /** pattern ID -> agency ID */
    TLongLongMap patternAgencyMap = new TLongLongHashMap();

    /** actually perform the migration */
    public void migrate(File fromDirectory) throws Exception {
        // import global stuff first: easy-peasy lemon squeezee
        gtx = VersionedDataStore.getGlobalTx();
        this.fromDirectory = fromDirectory;

        try {
            readAgencies();
            readAccounts();
            readRouteTypes();

            readStops();

            readRoutes();

            readShapes();
            readPatternStops();
            readTripPatterns();

            readStopTimes();
            readTrips();

            readCalendars();

            readExceptionDates();
            readExceptionCustomCalendars();
            readExceptions();

            gtx.commit();

            for (AgencyTx atx : atxes.values()) {
                atx.commit();
            }
        } finally {
            gtx.rollbackIfOpen();

            for (AgencyTx atx : atxes.values()) {
                atx.rollbackIfOpen();
            }
        }
    }

    private void readAgencies () throws Exception {
        System.out.println("Reading agencies");

        DatabaseCsv reader = getCsvReader("agency.csv");

        reader.readHeaders();

        int count = 0;
        while (reader.readRecord()) {
            Agency a = new Agency();
            a.id = reader.get("id");
            a.color = reader.get("color");
            a.defaultLon = reader.getDouble("defaultlon");
            a.defaultLat = reader.getDouble("defaultlat");
            a.gtfsAgencyId = reader.get("gtfsagencyid");
            a.lang = reader.get("lang");
            a.name = reader.get("name");
            a.phone = reader.get("phone");
            a.timezone = reader.get("timezone");
            a.url = reader.get("url");
            // easy to maintain referential integrity; we're retaining DB IDs.
            a.routeTypeId = reader.get("defaultroutetype_id");

            gtx.agencies.put(a.id, a);
            count++;
        }

        System.out.println("imported " + count + " agencies");
    }

    private void readAccounts () throws Exception {
        System.out.println("Reading accounts");

        DatabaseCsv reader = getCsvReader("account.csv");
        reader.readHeaders();
        int count = 0;

        while (reader.readRecord()) {
            String username = reader.get("username");
            Boolean admin = reader.getBoolean("admin");
            String email = reader.get("email");
            String agencyId = reader.get("agency_id");
            Account a = new Account(username, "password", email, admin, agencyId);
            a.password = reader.get("password");
            a.active = reader.getBoolean("active");
            a.id = a.username;

            gtx.accounts.put(a.id, a);

            count++;
        }

        System.out.println("Imported " + count + " accounts");
    }

    private void readStops () throws Exception {
        System.out.println("reading stops");

        DatabaseCsv reader = getCsvReader("stop.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            Stop s = new Stop();
            s.location = gf.createPoint(new Coordinate(reader.getDouble("lon"), reader.getDouble("lat")));
            s.agencyId = reader.get("agency_id");
            s.bikeParking = reader.getAvail("bikeparking");
            s.carParking = reader.getAvail("carparking");
            s.dropOffType = reader.getPdType("dropofftype");
            s.pickupType = reader.getPdType("pickuptype");
            s.gtfsStopId = reader.get("gtfsstopid");
            s.locationType = reader.getLocationType("locationtype");
            s.majorStop = reader.getBoolean("majorstop");
            s.parentStation = reader.get("parentstation");
            s.stopCode = reader.get("stopcode");
            s.stopIconUrl = reader.get("stopiconurl");
            s.stopDesc = reader.get("stopdesc");
            s.stopName = reader.get("stopname");
            s.stopUrl = reader.get("stopurl");
            s.wheelchairBoarding = reader.getAvail("wheelchairboarding");
            s.zoneId = reader.get("zoneid");
            s.id = reader.get("id");

            getAgencyTx(s.agencyId).stops.put(s.id, s);
            count ++;
        }

        System.out.println("Read " + count + " stops");

    }

    /** Read the routes */
    private void readRoutes () throws Exception {
        System.out.println("Reading routes");
        DatabaseCsv reader = getCsvReader("route.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            Route r = new Route();
            r.id = reader.get("id");
            r.comments = reader.get("comments");
            r.gtfsRouteId = reader.get("gtfsrouteid");
            r.routeColor = reader.get("routecolor");
            r.routeDesc = reader.get("routedesc");
            r.routeLongName = reader.get("routelongname");
            r.routeShortName = reader.get("routeshortname");
            r.routeTextColor = reader.get("routetextcolor");
            r.routeUrl = reader.get("routeurl");
            String status = reader.get("status");
            r.status = status != null ? StatusType.valueOf(status) : null;
            r.wheelchairBoarding = reader.getAvail("wheelchairboarding");
            r.agencyId = reader.get("agency_id");
            r.routeTypeId = reader.get("routetype_id");

            // cache the agency ID
            routeAgencyMap.put(Long.parseLong(r.id), Long.parseLong(r.agencyId));

            getAgencyTx(r.agencyId).routes.put(r.id, r);
            count++;
        }

        System.out.println("Read " + count + " routes");
    }

    /**
     * Read in the trip shapes. We put them in a MapDB keyed by Shape ID, because we don't store them directly;
     * rather, we copy them into their respective trip patterns when we import the patterns.
     */
    private void readShapes () throws Exception {
        System.out.println("Reading shapes");
        DatabaseCsv reader = getCsvReader("shapes.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            shapeCache.put(reader.get("id"), reader.getLineString("shape"));
            count++;
        }

        System.out.println("Read " + count + " shapes");
    }

    /** read and cache the trip pattern stops */
    private void readPatternStops () throws Exception {
        System.out.println("Reading trip pattern stops");
        DatabaseCsv reader = getCsvReader("patternstop.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            TripPatternStop tps = new TripPatternStop();
            Integer dtt = reader.getInteger("defaulttraveltime");
            tps.defaultTravelTime = dtt != null ? dtt : 0;
            Integer ddt = reader.getInteger("defaultdwelltime");
            tps.defaultDwellTime = ddt != null ? ddt : 0;
            tps.timepoint = reader.getBoolean("timepoint");
            tps.stopId = reader.get("stop_id");
            // note: not reading shape_dist_traveled as it was incorrectly computed. We'll recompute at the end.

            Fun.Tuple2<String, Integer> key = new Fun.Tuple2(reader.get("pattern_id"), reader.getInteger("stopsequence"));

            // make sure that we don't have a mess on our hands due to data import issues far in the past.
            if (patternStopCache.containsKey(key)) {
                throw new IllegalStateException("Duplicate pattern stops!");
            }

            patternStopCache.put(key, tps);
            count++;
        }

        System.out.println("Read " + count + " pattern stops");
    }

    /** Read the trip patterns */
    private void readTripPatterns () throws Exception {
        System.out.println("Reading trip patterns");
        DatabaseCsv reader = getCsvReader("trippattern.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            TripPattern p = new TripPattern();
            p.id = reader.get("id");
            p.headsign = reader.get("headsign");
            p.name = reader.get("name");
            p.routeId = reader.get("route_id");
            String shapeId = reader.get("shape_id");
            p.shape = shapeId != null ? shapeCache.get(shapeId) : null;

            // get the pattern stops
            p.patternStops = new ArrayList<TripPatternStop>();
            p.patternStops.addAll(patternStopCache.subMap(new Fun.Tuple2(p.id, null), new Fun.Tuple2(p.id, Fun.HI)).values());

            p.agencyId = routeAgencyMap.get(Long.parseLong(p.routeId)) + "";
            patternAgencyMap.put(Long.parseLong(p.id), Long.parseLong(p.agencyId));

            p.calcShapeDistTraveled(getAgencyTx(p.agencyId));

            getAgencyTx(p.agencyId).tripPatterns.put(p.id, p);
            count++;
        }

        System.out.println("Read " + count + " trip patterns");
    }

    /** Read the stop times and cache them */
    private void readStopTimes () throws Exception {
        System.out.println("Reading stop times (this could take a while) . . .");
        DatabaseCsv reader = getCsvReader("stoptime.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            if (++count % 100000 == 0) {
                System.out.println(human(count) + " stop times read . . .");
            }

            StopTime st = new StopTime();
            st.arrivalTime = reader.getInteger("arrivaltime");
            st.departureTime = reader.getInteger("departuretime");
            // note: not reading shape_dist_traveled as it was incorrectly computed. We'll recompute at the end.

            st.stopHeadsign = reader.get("stopheadsign");
            st.dropOffType = reader.getPdType("dropofftype");
            st.pickupType = reader.getPdType("pickuptype");
            st.stopId = reader.get("stop_id");

            Fun.Tuple2<String, Integer> key = new Fun.Tuple2(reader.get("trip_id"), reader.getInteger("stopsequence"));

            if (stopTimeCache.containsKey(key)) {
                throw new IllegalStateException("Duplicate stop times!");
            }

            stopTimeCache.put(key, st);
        }

        System.out.println("read " + count + " stop times");
    }

    private void readTrips () throws Exception {
        DatabaseCsv reader = getCsvReader("trip.csv");
        reader.readHeaders();
        int count = 0;
        int stCount = 0;

        while (reader.readRecord()) {
            Trip t = new Trip();
            t.id = reader.get("id");
            t.blockId = reader.get("blockid");
            t.endTime = reader.getInteger("endtime");
            t.gtfsTripId = reader.get("gtfstripid");
            t.headway = reader.getInteger("headway");
            t.invalid = reader.getBoolean("invalid");
            t.startTime = reader.getInteger("starttime");
            t.tripDescription = reader.get("tripdescription");
            String dir =  reader.get("tripdirection");
            t.tripDirection = dir != null ? TripDirection.valueOf(dir) : null;
            t.tripHeadsign = reader.get("tripheadsign");
            t.tripShortName = reader.get("tripshortname");
            t.useFrequency = reader.getBoolean("usefrequency");
            t.wheelchairBoarding = reader.getAvail("wheelchairboarding");
            t.patternId = reader.get("pattern_id");
            t.routeId = reader.get("route_id");
            t.calendarId = reader.get("servicecalendar_id");
            t.agencyId = routeAgencyMap.get(Long.parseLong(t.routeId)) + "";

            // get stop times
            // make sure we put nulls in as needed for skipped stops
            t.stopTimes = new ArrayList<StopTime>();

            // loop over the pattern stops and find the stop times that match
            for (Map.Entry<Fun.Tuple2<String, Integer>, TripPatternStop> entry :
                    patternStopCache.subMap(new Fun.Tuple2(t.patternId, null), new Fun.Tuple2(t.patternId, Fun.HI)).entrySet()) {
                // get the appropriate stop time, or null if the stop is skipped
                StopTime st = stopTimeCache.get(new Fun.Tuple2(t.id, entry.getKey().b));
                t.stopTimes.add(st);

                if (st != null)
                    stCount++;
            }

            count++;

            getAgencyTx(t.agencyId).trips.put(t.id, t);
        }

        System.out.println("Read " + count + " trips");
        System.out.println("Associated " + stCount + " stop times with trips");
    }

    private void readRouteTypes () throws Exception {
        System.out.println("Reading route types");

        DatabaseCsv reader = getCsvReader("routetype.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            RouteType rt = new RouteType();
            rt.id = reader.get("id");
            rt.description = reader.get("description");
            String grt = reader.get("gtfsroutetype");
            rt.gtfsRouteType = grt != null ? GtfsRouteType.valueOf(grt) : null;
            String hvt = reader.get("hvtroutetype");
            rt.hvtRouteType = hvt != null ? HvtRouteType.valueOf(hvt) : null;
            rt.localizedVehicleType = reader.get("localizedvehicletype");
            gtx.routeTypes.put(rt.id, rt);
            count++;
        }

        System.out.println("Imported " + count + " route types");
    }

    private void readCalendars () throws Exception {
        System.out.println("Reading calendars");
        DatabaseCsv reader = getCsvReader("servicecalendar.csv");
        reader.readHeaders();
        int count = 0;

        while (reader.readRecord()) {
            ServiceCalendar c = new ServiceCalendar();
            c.id = reader.get("id");
            c.description = reader.get("description");
            c.endDate = reader.getLocalDate("enddate");
            c.startDate = reader.getLocalDate("startdate");
            c.gtfsServiceId = reader.get("gtfsserviceid");
            c.monday = reader.getBoolean("monday");
            c.tuesday = reader.getBoolean("tuesday");
            c.wednesday = reader.getBoolean("wednesday");
            c.thursday = reader.getBoolean("thursday");
            c.friday = reader.getBoolean("friday");
            c.saturday = reader.getBoolean("saturday");
            c.sunday = reader.getBoolean("sunday");
            c.agencyId = reader.get("agency_id");

            getAgencyTx(c.agencyId).calendars.put(c.id, c);
            count++;
        }

        System.out.println("Imported " + count + " calendars");
    }

    private void readExceptionDates () throws Exception {
        System.out.println("Reading exception dates");
        DatabaseCsv reader = getCsvReader("exception_dates.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            exceptionDates.put(reader.get("scheduleexception_id"), reader.getLocalDate("dates"));
            count++;
        }

        System.out.println("Read " + count + " exception dates");
    }

    private void readExceptionCustomCalendars () throws Exception {
        System.out.println("Reading exception calendars");
        DatabaseCsv reader = getCsvReader("exception_calendars.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            exceptionCalendars.put(reader.get("scheduleexception_id"), reader.get("customschedule_id"));
            count++;
        }

        System.out.println("Read " + count + " exception calendars");
    }

    private void readExceptions () throws Exception {
        System.out.println("Reading exceptions");
        DatabaseCsv reader = getCsvReader("exception.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            ScheduleException e = new ScheduleException();
            e.id = reader.get("id");
            e.exemplar = ScheduleException.ExemplarServiceDescriptor.valueOf(reader.get("exemplar"));
            e.name = reader.get("name");
            e.agencyId = reader.get("agency_id");

            e.dates = new ArrayList<LocalDate>(exceptionDates.get(e.id));
            e.customSchedule = new ArrayList<String>(exceptionCalendars.get(e.id));

            getAgencyTx(e.agencyId).exceptions.put(e.id, e);
            count++;
        }

        System.out.println("Read " + count + " exceptions");
    }

    private DatabaseCsv getCsvReader(String file) {
        try {
            InputStream is = new FileInputStream(new File(fromDirectory, file));
            return new DatabaseCsv(new CsvReader(is, ',', Charset.forName("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private AgencyTx getAgencyTx (String agencyId) {
        if (!atxes.containsKey(agencyId))
            atxes.put(agencyId, VersionedDataStore.getAgencyTx(agencyId));

        return atxes.get(agencyId);
    }

    private static class DatabaseCsv {
        private CsvReader reader;

        private static Pattern datePattern = Pattern.compile("^([1-9][0-9]{3})-([0-9]{2})-([0-9]{2})");

        public DatabaseCsv(CsvReader reader) {
            this.reader = reader;
        }

        public boolean readHeaders() throws IOException {
            return reader.readHeaders();
        }

        public boolean readRecord () throws IOException {
            return reader.readRecord();
        }

        public String get (String column) throws IOException {
            String ret = reader.get(column);
            if (ret.isEmpty())
                return null;

            return ret;
        }

        public Double getDouble(String column) {
            try {
                String dbl = reader.get(column);
                return Double.parseDouble(dbl);
            } catch (Exception e) {
                return null;
            }
        }

        public StopTimePickupDropOffType getPdType (String column) throws Exception {
            String val = reader.get(column);

            try {
                return StopTimePickupDropOffType.valueOf(val);
            } catch (Exception e) {
                return null;
            }
        }

        public Boolean getBoolean (String column) throws Exception {
            String val = get(column);

            if (val == null)
                return null;

            switch (val.charAt(0)) {
                case 't':
                    return Boolean.TRUE;
                case 'f':
                    return Boolean.FALSE;
                default:
                    return null;
            }

        }

        public LineString getLineString (String column) throws Exception {
            String val = reader.get(column);

            try {
                return (LineString) new WKTReader().read(val);
            } catch (Exception e) {
                return null;
            }
        }

        public AttributeAvailabilityType getAvail (String column) throws Exception {
            String val = reader.get(column);

            try {
                return AttributeAvailabilityType.valueOf(val);
            } catch (Exception e) {
                return null;
            }
        }

        public Integer getInteger (String column) throws Exception {
            String val = reader.get(column);

            try {
                return Integer.parseInt(val);
            } catch (Exception e) {
                return null;
            }
        }

        public LocationType getLocationType (String column) throws Exception {
            String val = reader.get(column);

            try {
                return LocationType.valueOf(val);
            } catch (Exception e) {
                return null;
            }
        }

        public LocalDate getLocalDate (String column) throws Exception {
            String val = get(column);

            try {
                Matcher m = datePattern.matcher(val);

                if (!m.matches())
                    return null;

                return new LocalDate(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
            } catch (Exception e) {
                return null;
            }
        }
    }
}
