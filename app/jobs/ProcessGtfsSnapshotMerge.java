package jobs;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.geotools.referencing.GeodeticCalculator;
import org.hibernate.StatelessSession;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.mapdb.Fun;
import org.opentripplanner.routing.core.RouteMatcher;

import com.conveyal.gtfs.GTFSFeed;
import com.conveyal.gtfs.model.CalendarDate;
import com.conveyal.gtfs.model.Entity;
import com.conveyal.gtfs.model.Service;
import com.conveyal.gtfs.model.Shape;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Ints;
import com.mchange.v2.c3p0.impl.DbAuth;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import controllers.Bootstrap;
import models.transit.Agency;
import models.transit.Route;
import models.transit.ServiceCalendar;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import models.transit.TripShape;
import models.transit.Trip;
import models.transit.TripShape.InvalidShapeException;
import play.Logger;
import play.Play;
import play.db.jpa.NoTransaction;
import play.i18n.Messages;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

import org.mapdb.Fun.Tuple2;

public class ProcessGtfsSnapshotMerge extends Job {
/*
	private Long _gtfsSnapshotMergeId;

	private Map<String, Agency> agencyIdMap = new HashMap<String, Agency>();
	private Map<String, Route> routeIdMap = new HashMap<String, Route>();
	private Map<String, Stop> stopIdMap = new HashMap<String, Stop>();
	private Map<Tuple2<String, String>, ServiceCalendar> serviceCalendarIdMap = new HashMap<Tuple2<String, String>, ServiceCalendar>();
	private TObjectLongMap<String> shapeIdMap = new TObjectLongHashMap<String>();
	private GTFSFeed input;	
	
	public ProcessGtfsSnapshotMerge(Long gtfsSnapshotMergeId)
	{
		this._gtfsSnapshotMergeId = gtfsSnapshotMergeId;
	}

	public void doJob() {
			
		GtfsSnapshotMerge snapshotMerge = null;
		while(snapshotMerge == null)
		{
			snapshotMerge = GtfsSnapshotMerge.findById(this._gtfsSnapshotMergeId);
			Logger.warn("Waiting for snapshotMerge to save...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    	long agencyCount = 0;
    	long routeCount = 0;
    	long stopCount = 0;
    	Long stopTimeCount = new Long(0);
    	Long tripCount = new Long(0);
    	Long shapePointCount = new Long(0);
    	Long serviceCalendarCount = new Long(0);
    	Long serviceCalendarDateCount = new Long(0);
    	Long shapeCount = new Long(0);
    	
    	try {
    		
    		File gtfsFile = new File(Play.configuration.getProperty("application.publicDataDirectory"), snapshotMerge.snapshot.getFilename());
    		input = GTFSFeed.fromFile(gtfsFile.getAbsolutePath());
    		
    	    	
        	Logger.info("GtfsImporter: importing agencies...");
        
        	GtfsSnapshotMergeTask agencyTask = new GtfsSnapshotMergeTask(snapshotMerge);
        	agencyTask.startTask();
        
        	// store agencies
        	for (com.conveyal.gtfs.model.Agency gtfsAgency : input.agency.values()) {
        		// deduplicate agency IDs
        		String baseAgencyId = gtfsAgency.agency_id != null ? gtfsAgency.agency_id : gtfsAgency.agency_name;
        		if (Agency.count("gtfsAgencyId = ?", baseAgencyId) > 0) {
        			int number = 0;
        			while (Agency.count("gtfsAgencyId = ?", baseAgencyId + "_" + number) > 0) {
        				number++;
        			}
        			
        			gtfsAgency.agency_id = baseAgencyId + "_" + number;
        		}
        		
        		Agency agency = new Agency(gtfsAgency);
        		// don't save the agency until we've come up with the stop centroid, below.
        		agencyCount++;
        		// we do want to use the modified agency ID here, because everything that refers to it has a reference
        		// to the agency object we updated.
        		agencyIdMap.put(gtfsAgency.agency_id, agency);
        	}
	    	
	    	agencyTask.completeTask("Imported " + agencyCount + " agencies.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	    	
	    	Logger.info("Agencies loaded: " + agencyCount);
	    	
	        Logger.info("GtfsImporter: importing stops...");
	    	
	    	GtfsSnapshotMergeTask stopTask = new GtfsSnapshotMergeTask(snapshotMerge);
	    	stopTask.startTask();
	    	
	    	// build agency centroids as we go
	    	// note that these are not actually centroids, but the center of the extent of the stops . . .
	    	Envelope stopEnvelope = new Envelope();
	    	
	    	GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
	        for (com.conveyal.gtfs.model.Stop gtfsStop : input.stops.values()) {	     
	           Stop stop = new Stop(gtfsStop, geometryFactory);
	           stop.save();
	           stopIdMap.put(gtfsStop.stop_id, stop);
	           stopEnvelope.expandToInclude(gtfsStop.stop_lon, gtfsStop.stop_lat);	           
	           stopCount++;
	        }
	        
	        // set the agency default zoom locations
	        for (Agency a : agencyIdMap.values()) {
	        	a.defaultLat = stopEnvelope.centre().y;
	        	a.defaultLon = stopEnvelope.centre().x;
	        	a.save();
	        }
	        
	        stopTask.completeTask("Imported " + stopCount + " stops.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	        
	        Logger.info("Stops loaded: " + stopCount);
	    	
	    	Logger.info("GtfsImporter: importing routes...");
	    	
	    	GtfsSnapshotMergeTask routeTask = new GtfsSnapshotMergeTask(snapshotMerge);
	    	routeTask.startTask();
	    	
	    	for (com.conveyal.gtfs.model.Route gtfsRoute : input.routes.values()) {
	    		Route route = new Route(gtfsRoute, agencyIdMap.get(gtfsRoute.agency.agency_id));
	    		route.save();
	    		routeIdMap.put(gtfsRoute.route_id, route);
	    		routeCount++;
	    	}
	        
	        routeTask.completeTask("Imported " + routeCount + " routes.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	        
	        Logger.info("Routes loaded:" + routeCount); 

	        
	        Logger.info("GtfsImporter: importing Shapes...");
		    
	        GtfsSnapshotMergeTask tripShapeTask = new GtfsSnapshotMergeTask(snapshotMerge);
	        tripShapeTask.startTask();
	        
	        // import points
	        
	        for (String shapeId : input.shapes.keySet()) {
	        	TripShape shape;
	        	Collection<Shape> shapes;
	        	try {
	        		shapes = input.shapePoints.subMap(new Tuple2(shapeId, 0), new Tuple2(shapeId, Fun.HI)).values();
	        		shape = new TripShape(shapes, shapeId, geometryFactory);
	        	} catch (InvalidShapeException e) {
	        		Logger.warn("Shape " + shapeId + " is not valid. Using stop-to-stop geometries instead.");
	        		continue;
	        	}
	        	shape.save();
	        	
	        	// we track IDs here not the objects themselves, because they have the potential to be huge
	        	shapeIdMap.put(shapeId, shape.id);
	        	
	        	shapePointCount += shapes.size();
	        	shapeCount++;
	        }
	        
	        Logger.info("Shape points loaded: " + shapePointCount.toString());
	        Logger.info("Shapes loaded: " + shapeCount.toString());
	        
	        tripShapeTask.completeTask("Imported " + shapePointCount + " points in " + shapeCount + " shapes.", GtfsSnapshotMergeTaskStatus.SUCCESS);

	        
	        GtfsSnapshotMergeTask serviceCalendarsTask = new GtfsSnapshotMergeTask(snapshotMerge);
	        serviceCalendarsTask.startTask();
	        
	        Logger.info("GtfsImporter: importing Service Calendars...");
	    	
	        for (Service svc : input.services.values()) {
	        	
	        	ServiceCalendar cal;
	        	
	        	if (svc.calendar != null) {
	        		// easy case: don't have to infer anything!
	        		cal = new ServiceCalendar(svc.calendar);
	        		cal.save();
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
		        		
		        		cal.startDate = startDate.toDate();
		        		cal.endDate = endDate.toDate();
	        		}
	        		
	        		cal.inferName();
	        		cal.gtfsServiceId = svc.service_id;
	        	}
	        	
        		for (Agency a : agencyIdMap.values()) {
        			ServiceCalendar aCal = cal.clone();
        			aCal.agency = a;
        			aCal.save();
        			serviceCalendarIdMap.put(new Tuple2(a.gtfsAgencyId, svc.service_id), aCal);
        		}
	        	
	        	serviceCalendarCount++;
	        }
	    
	        
	        Logger.info("Service calendars loaded: " + serviceCalendarCount); 
	        
	        serviceCalendarsTask.completeTask("Imported " + serviceCalendarCount.toString() + " service calendars.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	        
	        Logger.info("GtfsImporter: importing trips...");
	        
	        GtfsSnapshotMergeTask tripsStopTimesTask = new GtfsSnapshotMergeTask(snapshotMerge);
	        tripsStopTimesTask.startTask();
	        
	        // import trips, stop times and patterns all at once
	        Map<List<String>, List<String>> patterns = input.findPatterns();
	    	
	        for (Entry<List<String>, List<String>> pattern : patterns.entrySet()) {
	        	// it is possible, though unlikely, for two routes to have the same stopping pattern
	        	// we want to ensure they get different trip patterns
	        	Map<String, TripPattern> tripPatternsByRoute = Maps.newHashMap();
	        	
	        	for (String tripId : pattern.getValue()) {
	        		com.conveyal.gtfs.model.Trip gtfsTrip = input.trips.get(tripId); 
	        		if (!tripPatternsByRoute.containsKey(gtfsTrip.route.route_id)) {
	        			TripPattern pat = createTripPatternFromTrip(gtfsTrip);
	        			pat.route = routeIdMap.get(gtfsTrip.route.route_id);
	        			pat.save();
	        			
	        			for (TripPatternStop stop : pat.patternStops) {
	        				stop.save();
	        			}
	        			
	        			tripPatternsByRoute.put(gtfsTrip.route.route_id, pat);
	        		}
	        		
	        		// there is more than one pattern per route, but this map is specific to only this pattern
	        		// generally it will contain exactly one entry, unless there are two routes with identical
	        		// stopping patterns.
	        		// (in DC, suppose there were trips on both the E2/weekday and E3/weekend from Friendship Heights
	        		//  that short-turned at Missouri and 3rd).
	        		TripPattern pat = tripPatternsByRoute.get(gtfsTrip.route.route_id);
	        		
	        		TripShape shape = null;
	        		
	        		if (gtfsTrip.shape_id != null && shapeIdMap.containsKey(gtfsTrip.shape_id))
	        			shape = TripShape.findById(shapeIdMap.get(gtfsTrip.shape_id));
	        		
	        		Trip trip = new Trip(gtfsTrip, routeIdMap.get(gtfsTrip.route.route_id), shape, pat,
	        				serviceCalendarIdMap.get(new Tuple2(gtfsTrip.route.agency.agency_id, gtfsTrip.service.service_id)));
	        		trip.save();
	        		
	        		Trip.nativeInsert(snapshotMerge.em(), gtfsTrip, routeIdMap.get(gtfsTrip.route.route_id).id, shape != null ? shape.id : null,
	        				serviceCalendarIdMap.get(new Tuple2(gtfsTrip.route.agency.agency_id, gtfsTrip.service.service_id)).id,
	        				pat.id);
	        		
	        		int stopIdx = 0;
	        		// next step: stop times. make sure to add trippatternstop info to each. 
	        		for (com.conveyal.gtfs.model.StopTime gtfsStopTime : input.stop_times.subMap(new Tuple2(gtfsTrip.trip_id,0), new Tuple2(gtfsTrip.trip_id, Fun.HI)).values()) {
	        			// we ignore the stop sequence information from the GTFS and renumber them starting at 1.
	        			StopTime.nativeInsert(snapshotMerge.em(), gtfsStopTime, trip.id, stopIdMap.get(gtfsStopTime.stop_id).id, pat.patternStops.get(stopIdx).id);
	        			stopIdx++;
	        			stopTimeCount++;
	        		}
	        		
	        		tripCount++;
	        		
	        		if (tripCount % 1000 == 0) {
	        			Logger.info("Loaded %s / %s trips", tripCount, input.trips.size());
	        		}
	        	}
	        }
	        	
	        
	        Logger.info("Trips loaded: " + tripCount); 
	        
	        tripsStopTimesTask.completeTask("Imported " + tripCount.toString() + " trips and " + stopTimeCount.toString() + " stop times.",
	                GtfsSnapshotMergeTaskStatus.SUCCESS);
	        	        
	        String mergeDescription = new String("Imported GTFS file: " + agencyCount + " agencies; " + routeCount + " routes;" + stopCount + " stops; " +  stopTimeCount + " stopTimes; " + tripCount + " trips;" + shapePointCount + " shapePoints");
	        
	        snapshotMerge.complete(mergeDescription);
	       
	        encodeTripShapes();
	    }
        catch (Exception e) {
    		
        	Logger.error(e.toString()); 
        	
        	e.printStackTrace();
        	
        	snapshotMerge.failed(e.toString());
    	}
	}*/
	
	/**
	 * Create a trip pattern from the given trip.
	 * Neither the trippattern nor the trippatternstops are saved.
	 *//*
	public TripPattern createTripPatternFromTrip (com.conveyal.gtfs.model.Trip gtfsTrip) {
		TripPattern patt = new TripPattern();
		patt.route = routeIdMap.get(gtfsTrip.route.route_id);
		if (gtfsTrip.shape_id != null)
			patt.shape = TripShape.findById(shapeIdMap.get(gtfsTrip.shape_id));
		
		List<TripPatternStop> patternStops = new ArrayList<TripPatternStop>();
				
		com.conveyal.gtfs.model.StopTime[] stopTimes =
				input.stop_times.subMap(new Tuple2(gtfsTrip.trip_id, 0), new Tuple2(gtfsTrip.trip_id, Fun.HI)).values().toArray(new com.conveyal.gtfs.model.StopTime[0]);
		
		if (gtfsTrip.trip_headsign != null && !gtfsTrip.trip_headsign.isEmpty())
			patt.name = gtfsTrip.trip_headsign;
		else if (gtfsTrip.route.route_long_name != null)
			patt.name = Messages.get("gtfs.named-route-pattern", gtfsTrip.route.route_long_name, input.stops.get(stopTimes[stopTimes.length - 1].stop_id).stop_name, stopTimes.length);
		else
			patt.name = Messages.get("gtfs.unnamed-route-pattern", input.stops.get(stopTimes[stopTimes.length - 1].stop_id).stop_name, stopTimes.length);

		// stop sequences are one-based
		int stopSequence = 1;
		Stop previous = null;
		
		GeodeticCalculator gc = new GeodeticCalculator();
		
		for (com.conveyal.gtfs.model.StopTime st : stopTimes) {
			TripPatternStop tps = new TripPatternStop();
			tps.stop = stopIdMap.get(st.stop_id);
			
			tps.stopSequence = stopSequence++;
			tps.pattern = patt;
			if (st.timepoint != Entity.INT_MISSING)
				tps.timepoint = st.timepoint == 1;
			
			if (st.departure_time != Entity.INT_MISSING && st.arrival_time != Entity.INT_MISSING)
				tps.defaultDwellTime = st.departure_time - st.arrival_time;
			else
				tps.defaultDwellTime = 0;	
				
			// travel times will be inferred momentarily
			if (previous != null) {
				gc.setStartingGeographicPoint((Double) previous.getLocation().get("lat"), (Double) previous.getLocation().get("lng"));
				gc.setDestinationGeographicPoint((Double) tps.stop.getLocation().get("lat"), (Double) tps.stop.getLocation().get("lng"));
				tps.defaultDistance = gc.getOrthodromicDistance();
			}
			
			patternStops.add(tps);
			previous = tps.stop;
		}
		
		// infer travel times
		if (stopTimes.length >= 2) {
			int startOfBlock = 0;
			double totalDistance = 0;
			// start at one because the first stop has no travel time
			// but don't put nulls in the data
			patternStops.get(0).defaultTravelTime = 0;
			for (int i = 1; i < stopTimes.length; i++) {
				com.conveyal.gtfs.model.StopTime current = stopTimes[i];
				
				totalDistance += patternStops.get(i).defaultDistance;
				
				if (current.arrival_time != Entity.INT_MISSING) {
					// interpolate times
					
					int timeSinceLastSpecifiedTime = current.arrival_time - stopTimes[startOfBlock].departure_time;
					
					// go back over all of the interpolated stop times and interpolate them
					for (int j = startOfBlock + 1; j <= i; j++) {
						TripPatternStop tps = patternStops.get(j);
						tps.defaultTravelTime = (int) Math.round(timeSinceLastSpecifiedTime * tps.defaultDistance / totalDistance);
					}
					
					startOfBlock = i;
					totalDistance = 0;
				}
			}
		}
		
		patt.patternStops = patternStops;
		
		return patt;
	}
	
	 public static void encodeTripShapes() {
	    	
		List<TripPattern> tps = TripPattern.findAll();
		
		for(TripPattern tp : tps) {
			if(tp.shape != null && tp.encodedShape == null) {
				tp.encodedShape = tp.shape.generateEncoded();
				tp.save();
			}
		}
	}*/
}

