package jobs;

import com.conveyal.gtfs.GTFSFeed;
import com.conveyal.gtfs.model.CalendarDate;
import com.conveyal.gtfs.model.Entity;
import com.conveyal.gtfs.model.Service;
import com.conveyal.gtfs.model.Shape;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.vividsolutions.jts.geom.*;
import datastore.AgencyTx;
import datastore.GlobalTx;
import datastore.VersionedDataStore;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import models.transit.*;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.mapdb.DBMaker;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;
import play.Logger;
import play.i18n.Messages;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;


public class ProcessGtfsSnapshotMerge implements Runnable {
	/** map from GTFS agency IDs to Agencies */
	private Map<String, Agency> agencyIdMap = new HashMap<String, Agency>();
	private Map<String, Route> routeIdMap = new HashMap<String, Route>();
	/** map from (gtfs stop ID, database agency ID) -> stop */
	private Map<Tuple2<String, String>, Stop> stopIdMap = Maps.newHashMap();
	private TIntObjectMap<String> routeTypeIdMap = new TIntObjectHashMap<String>();
	private Map<String, LineString> shapes = DBMaker.newTempHashMap();
	
	private GTFSFeed input;	
	private File gtfsFile;

	/** once the merge runs this will have the ID of the created agency */
	public String agencyId;

	public String sourceId;

	public ProcessGtfsSnapshotMerge (File gtfsFile) {
		this(gtfsFile, null);
	}

	public ProcessGtfsSnapshotMerge (File gtfsFile, String sourceId) {
		this.gtfsFile = gtfsFile;
		this.sourceId = sourceId;
		System.out.println(">> Merge w/ sourceID " + sourceId);
	}

	public void run () {
    	long agencyCount = 0;
    	long routeCount = 0;
    	long stopCount = 0;
    	long stopTimeCount = 0;
    	long tripCount = 0;
    	long shapePointCount = 0;
    	long serviceCalendarCount = 0;
    	long shapeCount = 0;
    	
    	GlobalTx gtx = VersionedDataStore.getGlobalTx();
        // map from (non-gtfs) agency IDs to transactions.
        Map<String, AgencyTx> agencyTxs = Maps.newHashMap();
        
        try {
       		input = GTFSFeed.fromFile(gtfsFile.getAbsolutePath());
    		
        	Logger.info("GtfsImporter: importing agencies...");        
        	// store agencies
        	for (com.conveyal.gtfs.model.Agency gtfsAgency : input.agency.values()) {
   	       		Agency agency = new Agency(gtfsAgency);
				agencyId = agency.id;
				agency.sourceId = sourceId;
        		// don't save the agency until we've come up with the stop centroid, below.
        		agencyCount++;
        		// we do want to use the modified agency ID here, because everything that refers to it has a reference
        		// to the agency object we updated.
        		agencyIdMap.put(gtfsAgency.agency_id, agency);
        	}
        	
	        // agency-specific stuff: start transactions for all relevant agencies	        
	        for (Agency a : agencyIdMap.values()) {
	        	agencyTxs.put(a.id, VersionedDataStore.getAgencyTx(a.id));
	        }
	    	
	    	Logger.info("Agencies loaded: " + agencyCount);
	    	
	        Logger.info("GtfsImporter: importing stops...");
	        
	        // infer agency ownership of stops, if there are multiple agencies
	        SortedSet<Tuple2<String, String>> stopsByAgency = inferAgencyStopOwnership();
	    	
	    	// build agency centroids as we go
	    	// note that these are not actually centroids, but the center of the extent of the stops . . .
	    	Map<String, Envelope> stopEnvelopes = Maps.newHashMap();
	    	
	    	for (Agency agency : agencyIdMap.values()) {
	    		stopEnvelopes.put(agency.id, new Envelope());
	    	}
	    	
	    	GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
	        for (com.conveyal.gtfs.model.Stop gtfsStop : input.stops.values()) {
	        	// duplicate the stop for all of the agencies by which it is used
	        	Collection<Agency> agencies = Collections2.transform(
	        			stopsByAgency.subSet(new Tuple2(gtfsStop.stop_id, null), new Tuple2(gtfsStop.stop_id, Fun.HI)),
	        			new Function<Tuple2<String, String>, Agency> () {

							@Override
							public Agency apply(Tuple2<String, String> input) {
								// TODO Auto-generated method stub
								return agencyIdMap.get(input.b);
							}
	        			});

	        	// impossible to tell to whom unused stops belong, so give them to everyone
	        	if (agencies.size() == 0)
	        		agencies = agencyIdMap.values();
	        	
	        	for (Agency agency : agencies) {
	        		Stop stop = new Stop(gtfsStop, geometryFactory, agency);
	        		agencyTxs.get(agency.id).stops.put(stop.id, stop);
	        		stopIdMap.put(new Tuple2(gtfsStop.stop_id, agency.id), stop);
	        		stopEnvelopes.get(agency.id).expandToInclude(gtfsStop.stop_lon, gtfsStop.stop_lat);
	        	}
	        }
	        
	        // set the agency default zoom locations and save the agencies
	        for (Agency a : agencyIdMap.values()) {
	        	Envelope stopEnvelope = stopEnvelopes.get(a.id	);
	        	a.defaultLat = stopEnvelope.centre().y;
	        	a.defaultLon = stopEnvelope.centre().x;
	        	gtx.agencies.put(a.id, a);
	        }
	        	        
	        Logger.info("Stops loaded: " + stopCount);
	    	
	    	Logger.info("GtfsImporter: importing routes...");
	    	
	    	
	    	for (com.conveyal.gtfs.model.Route gtfsRoute : input.routes.values()) {
	    		Agency agency = agencyIdMap.get(gtfsRoute.agency.agency_id);
	    		
	    		if (!routeTypeIdMap.containsKey(gtfsRoute.route_type)) {
	    			RouteType rt = new RouteType();
	    			rt.gtfsRouteType = GtfsRouteType.fromGtfs(gtfsRoute.route_type);
	    			rt.hvtRouteType = rt.gtfsRouteType.toHvt();
	    			rt.description = agencyIdMap.values().iterator().next().name + " " + rt.gtfsRouteType.toString();
	    			gtx.routeTypes.put(rt.id, rt);
	    			routeTypeIdMap.put(gtfsRoute.route_type, rt.id);
	    		}
	    		
	    		Route route = new Route(gtfsRoute, agency, routeTypeIdMap.get(gtfsRoute.route_type));
	    		
	    		agencyTxs.get(agency.id).routes.put(route.id, route);
	    		routeIdMap.put(gtfsRoute.route_id, route);
	    		routeCount++;
	    	}
	        	        
	        Logger.info("Routes loaded:" + routeCount); 

	        Logger.info("GtfsImporter: importing Shapes...");
	        
	        // shapes are a part of trippatterns, so we don't actually import them into the model, we just make a map
	        // shape id -> linestring which we use when building trip patterns
	        // we put this map in mapdb because it can be big
	        
	        // import points
	        
	        for (String shapeId : input.shapes.keySet()) {
	        	Collection<Shape> points = input.shapePoints.subMap(new Tuple2(shapeId, 0), new Tuple2(shapeId, Fun.HI)).values();
	        		
	        	if (points.size() < 2) {
	        		Logger.warn("Shape " + shapeId + " has fewer than two points. Using stop-to-stop geometries instead.");
	        		continue;
	        	}
	        		
	        	Coordinate[] coords = new Coordinate[points.size()];	
	        	
	        	int lastSeq = Integer.MIN_VALUE;
	        	
	        	int i = 0;
	        	for (Shape shape : points) {
	        		if (shape.shape_pt_sequence <= lastSeq) {
	        			Logger.warn("Shape %s has out-of-sequence points. This implies a bug in the GTFS importer. Using stop-to-stop geometries.");
	        			continue;
	        		}
	        		lastSeq = shape.shape_pt_sequence;
	        		
	        		coords[i++] = new Coordinate(shape.shape_pt_lon, shape.shape_pt_lat);
	        	}
	        	
	        	shapes.put(shapeId, geometryFactory.createLineString(coords));
	        	shapePointCount += points.size();
	        	shapeCount++;
	        }
	        
	        Logger.info("Shape points loaded: " + shapePointCount);
	        Logger.info("Shapes loaded: " + shapeCount);
	        
	        Logger.info("GtfsImporter: importing Service Calendars...");
	        
	        // we don't put service calendars in the database just yet, because we don't know what agency they're associated with
	        // we copy them into the agency database as needed
	        // GTFS service ID -> ServiceCalendar
	        Map<String, ServiceCalendar> calendars = Maps.newHashMap();
	    	
	        for (Service svc : input.services.values()) {
	        	
	        	ServiceCalendar cal;
	        	
	        	if (svc.calendar != null) {
	        		// easy case: don't have to infer anything!
	        		cal = new ServiceCalendar(svc.calendar);
	        	} else {
	        		// infer a calendar
	        		// number of mondays, etc. that this calendar is active
	        		int monday, tuesday, wednesday, thursday, friday, saturday, sunday;
	        		monday = tuesday = wednesday = thursday = friday = saturday = sunday = 0;
	        		
	        		LocalDate startDate = null;
	        		LocalDate endDate = null;
	        		
	        		for (CalendarDate cd : svc.calendar_dates.values()) {
	        			if (cd.exception_type == 2)
	        				continue;
	        			
	        			if (startDate == null || cd.date.isBefore(startDate))
	        				startDate = cd.date;
	        			
	        			if (endDate == null || cd.date.isAfter(endDate))
	        				endDate = cd.date;
	        			
	        			int dayOfWeek = cd.date.getDayOfWeek();
	        			
	        			switch (dayOfWeek) {
						case DateTimeConstants.MONDAY:
							monday++;
							break;
						case DateTimeConstants.TUESDAY:
							tuesday++;
							break;
						case DateTimeConstants.WEDNESDAY:
							wednesday++;
							break;
						case DateTimeConstants.THURSDAY:
							thursday++;
							break;
						case DateTimeConstants.FRIDAY:
							friday++;
							break;
						case DateTimeConstants.SATURDAY:
							saturday++;
							break;
						case DateTimeConstants.SUNDAY:
							sunday++;
							break;
						}
	        		}
	        		
	        		// infer the calendar. if there is service on more than half as many as the maximum number of
	        		// a particular day that has service, assume that day has service in general.
	        		int maxService = Ints.max(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
	        		
	        		cal = new ServiceCalendar();
	        		
	        		if (startDate == null) {
	        			// no service whatsoever
	        			Logger.warn("Service ID " + svc.service_id + " has no service whatsoever");
	        			startDate = new LocalDate().minusMonths(1);
	        			endDate = startDate.plusYears(1);
	        			cal.monday = cal.tuesday = cal.wednesday = cal.thursday = cal.friday = cal.saturday = cal.sunday = false;
	        		}
	        		else {
	        			// infer parameters
		       
		        		int threshold = (int) Math.round(Math.ceil((double) maxService / 2));
		        		
		        		cal.monday = monday >= threshold;
		        		cal.tuesday = tuesday >= threshold;
		        		cal.wednesday = wednesday >= threshold;
		        		cal.thursday = thursday >= threshold;
		        		cal.friday = friday >= threshold;
		        		cal.saturday = saturday >= threshold;
		        		cal.sunday = sunday >= threshold;
		        		
		        		cal.startDate = startDate;
		        		cal.endDate = endDate;
	        		}
	        		
	        		cal.inferName();
	        		cal.gtfsServiceId = svc.service_id;
	        	}
	   
	        	calendars.put(svc.service_id, cal);
	        	
	        	serviceCalendarCount++;
	        }
	        
	        Logger.info("Service calendars loaded: " + serviceCalendarCount); 
	        	        
	        Logger.info("GtfsImporter: importing trips...");
	        
	        // import trips, stop times and patterns all at once
	        Map<List<String>, List<String>> patterns = input.findPatterns();
	    	
	        for (Entry<List<String>, List<String>> pattern : patterns.entrySet()) {
	        	// it is possible, though unlikely, for two routes to have the same stopping pattern
	        	// we want to ensure they get different trip patterns
	        	Map<String, TripPattern> tripPatternsByRoute = Maps.newHashMap();
	        	
	        	for (String tripId : pattern.getValue()) {
	        		com.conveyal.gtfs.model.Trip gtfsTrip = input.trips.get(tripId);
	        		String agencyId = agencyIdMap.get(gtfsTrip.route.agency.agency_id).id;
	        		AgencyTx tx = agencyTxs.get(agencyId);
	        		
	        		if (!tripPatternsByRoute.containsKey(gtfsTrip.route.route_id)) {
	        			TripPattern pat = createTripPatternFromTrip(gtfsTrip, tx);
	        			tx.tripPatterns.put(pat.id, pat);
	        			tripPatternsByRoute.put(gtfsTrip.route.route_id, pat);
	        		}
	        		
	        		// there is more than one pattern per route, but this map is specific to only this pattern
	        		// generally it will contain exactly one entry, unless there are two routes with identical
	        		// stopping patterns.
	        		// (in DC, suppose there were trips on both the E2/weekday and E3/weekend from Friendship Heights
	        		//  that short-turned at Missouri and 3rd).
	        		TripPattern pat = tripPatternsByRoute.get(gtfsTrip.route.route_id);
	        		
	        		ServiceCalendar cal = calendars.get(gtfsTrip.service.service_id);
	        		// if the service calendar has not yet been imported, import it
	        		if (!tx.calendars.containsKey(cal.id)) {
	        			// no need to clone as they are going into completely separate mapdbs
	        			tx.calendars.put(cal.id, cal);
	        		}
	        		
	        		Trip trip = new Trip(gtfsTrip, routeIdMap.get(gtfsTrip.route.route_id), pat, cal);      		
	        		
	        		Collection<com.conveyal.gtfs.model.StopTime> stopTimes =
	        				input.stop_times.subMap(new Tuple2(gtfsTrip.trip_id, null), new Tuple2(gtfsTrip.trip_id, Fun.HI)).values();
	        		
	        		for (com.conveyal.gtfs.model.StopTime st : stopTimes) {
	        			trip.stopTimes.add(new StopTime(st, stopIdMap.get(new Tuple2<String, String>(st.stop_id, agencyId)).id));
	        		}
	        		
	        		tx.trips.put(trip.id, trip);
	        		
	        		tripCount++;
	        		
	        		if (tripCount % 1000 == 0) {
	        			Logger.info("Loaded %s / %s trips", tripCount, input.trips.size());
	        		}
	        	}
	        }
	        
	        Logger.info("Trips loaded: " + tripCount); 
	        
	        // commit the agency TXs first, so that we have orphaned data rather than inconsistent data on a commit failure
	        for (AgencyTx tx : agencyTxs.values()) {
	        	tx.commit();
	        }
	        gtx.commit();
	        	        	        
	        Logger.info("Imported GTFS file: " + agencyCount + " agencies; " + routeCount + " routes;" + stopCount + " stops; " +  stopTimeCount + " stopTimes; " + tripCount + " trips;" + shapePointCount + " shapePoints");
        }
        finally {
        	for (AgencyTx tx : agencyTxs.values()) {
        		tx.rollbackIfOpen();
        	}
        	gtx.rollbackIfOpen();
        }
	}

	/** infer the ownership of stops based on what stops there
	 * Returns a set of tuples stop ID, agency ID with GTFS IDs */
	private SortedSet<Tuple2<String, String>> inferAgencyStopOwnership() {
		// agency
		SortedSet<Tuple2<String, String>> ret = Sets.newTreeSet();
		
		for (com.conveyal.gtfs.model.StopTime st : input.stop_times.values()) {
			String stopId = st.stop_id;
			String agencyId = input.trips.get(st.trip_id).route.agency.agency_id;
			Tuple2<String, String> key = new Tuple2(stopId, agencyId);
			ret.add(key);
		}
		
		return ret;
	}
	
	/**
	 * Create a trip pattern from the given trip.
	 * Neither the trippattern nor the trippatternstops are saved.
	 */
	public TripPattern createTripPatternFromTrip (com.conveyal.gtfs.model.Trip gtfsTrip, AgencyTx tx) {
		TripPattern patt = new TripPattern();
		patt.routeId = routeIdMap.get(gtfsTrip.route.route_id).id;
		patt.agencyId = agencyIdMap.get(gtfsTrip.route.agency.agency_id).id;
		if (gtfsTrip.shape_id != null) {
			if (!shapes.containsKey(gtfsTrip.shape_id)) {
				Logger.warn("Missing shape for shape ID %s, referenced by trip %s", gtfsTrip.shape_id, gtfsTrip.trip_id);
			}
			else {
				patt.shape = (LineString) shapes.get(gtfsTrip.shape_id).clone();
			}
		}
		
		patt.patternStops = new ArrayList<TripPatternStop>();
				
		com.conveyal.gtfs.model.StopTime[] stopTimes =
				input.stop_times.subMap(new Tuple2(gtfsTrip.trip_id, 0), new Tuple2(gtfsTrip.trip_id, Fun.HI)).values().toArray(new com.conveyal.gtfs.model.StopTime[0]);
		
		if (gtfsTrip.trip_headsign != null && !gtfsTrip.trip_headsign.isEmpty())
			patt.name = gtfsTrip.trip_headsign;
		else if (gtfsTrip.route.route_long_name != null)
			patt.name = Messages.get("gtfs.named-route-pattern", gtfsTrip.route.route_long_name, input.stops.get(stopTimes[stopTimes.length - 1].stop_id).stop_name, stopTimes.length);
		else
			patt.name = Messages.get("gtfs.unnamed-route-pattern", input.stops.get(stopTimes[stopTimes.length - 1].stop_id).stop_name, stopTimes.length);
				
		for (com.conveyal.gtfs.model.StopTime st : stopTimes) {
			TripPatternStop tps = new TripPatternStop();
			Stop stop = stopIdMap.get(new Tuple2(st.stop_id, patt.agencyId));
			tps.stopId = stop.id;
			
			if (st.timepoint != Entity.INT_MISSING)
				tps.timepoint = st.timepoint == 1;
			
			if (st.departure_time != Entity.INT_MISSING && st.arrival_time != Entity.INT_MISSING)
				tps.defaultDwellTime = st.departure_time - st.arrival_time;
			else
				tps.defaultDwellTime = 0;
			
			patt.patternStops.add(tps);
		}
		
		patt.calcShapeDistTraveled(tx);
		
		// infer travel times
		if (stopTimes.length >= 2) {
			int startOfBlock = 0;
			// start at one because the first stop has no travel time
			// but don't put nulls in the data
			patt.patternStops.get(0).defaultTravelTime = 0;
			for (int i = 1; i < stopTimes.length; i++) {
				com.conveyal.gtfs.model.StopTime current = stopTimes[i];
				
				if (current.arrival_time != Entity.INT_MISSING) {
					// interpolate times
					
					int timeSinceLastSpecifiedTime = current.arrival_time - stopTimes[startOfBlock].departure_time;
					
					double blockLength = patt.patternStops.get(i).shapeDistTraveled - patt.patternStops.get(startOfBlock).shapeDistTraveled;
					
					// go back over all of the interpolated stop times and interpolate them
					for (int j = startOfBlock + 1; j <= i; j++) {
						TripPatternStop tps = patt.patternStops.get(j);
						double distFromLastStop = patt.patternStops.get(j).shapeDistTraveled - patt.patternStops.get(j - 1).shapeDistTraveled;
						tps.defaultTravelTime = (int) Math.round(timeSinceLastSpecifiedTime * distFromLastStop / blockLength);
					}
					
					startOfBlock = i;
				}
			}
		}
				
		return patt;
	}
}

