package datastore;

import com.beust.jcommander.internal.Maps;
import com.csvreader.CsvReader;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import models.Account;
import models.transit.*;
import sun.misc.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Migrate a Postgres database dump to the MapDB format.
 */
public class MigrateToMapDB {
    GlobalTx gtx;
    File fromDirectory;

    private static GeometryFactory gf = new GeometryFactory();

    private Map<String, AgencyTx> atxes = Maps.newHashMap();

    /** actually perform the migration */
    public void migrate(File fromDirectory) throws Exception {
        // import global stuff first: easy-peasy lemon squeezee
        gtx = VersionedDataStore.getGlobalTx();
        this.fromDirectory = fromDirectory;

        readAgencies();
        readAccounts();
        readRouteTypes();

        readStops();

        gtx.commit();

        for (AgencyTx atx : atxes.values()) {
            atx.commit();
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
            String agencyId = reader.get("agencyId");
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
            s.pickupType = reader.getPdType("pickupType");
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

    private void readRouteTypes () throws Exception {
        System.out.println("Reading route types");

        DatabaseCsv reader = getCsvReader("routetype.csv");
        reader.readHeaders();

        int count = 0;

        while (reader.readRecord()) {
            RouteType rt = new RouteType();
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

        public AttributeAvailabilityType getAvail (String column) throws Exception {
            String val = reader.get(column);

            try {
                return AttributeAvailabilityType.valueOf(val);
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
    }
}
