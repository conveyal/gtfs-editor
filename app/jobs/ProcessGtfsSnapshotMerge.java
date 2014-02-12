package jobs;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

import com.mchange.v2.c3p0.impl.DbAuth;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import models.gtfs.GtfsSnapshotMerge;
import models.gtfs.GtfsSnapshotMergeTask;
import models.gtfs.GtfsSnapshotMergeTaskStatus;
import models.transit.Agency;
import models.transit.Route;
import models.transit.ServiceCalendar;
import models.transit.ServiceCalendarDate;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import models.transit.TripShape;
import models.transit.Trip;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import utils.StopSequence;


public class ProcessGtfsSnapshotMerge extends Job {

	private Long _gtfsSnapshotMergeId;

	
	private Map<String, BigInteger> agencyIdMap = new HashMap<String, BigInteger>();
	private Map<String, BigInteger> routeIdMap = new HashMap<String, BigInteger>();
	private Map<String, BigInteger> stopIdMap = new HashMap<String, BigInteger>();
	private Map<String, BigInteger> tripShapeIdMap = new HashMap<String, BigInteger>();
	private Map<String, BigInteger> tripIdMap = new HashMap<String, BigInteger>();
	private	Map<String, BigInteger> serviceIdMap = new HashMap<String, BigInteger>();
	private Map<String, BigInteger> serviceDateIdMap = new HashMap<String, BigInteger>();
	

	private Map<BigInteger, ArrayList<StopSequence>> tripStopTimeMap = new HashMap<BigInteger, ArrayList<StopSequence>>();
	
	private Map<BigInteger, ArrayList<StopSequence>> tripPatternStopMap = new HashMap<BigInteger, ArrayList<StopSequence>>();
	private Map<BigInteger,  ArrayList<BigInteger>> tripPatternFirstStopMap = new HashMap<BigInteger, ArrayList<BigInteger>>();

	private Map<String, List<org.onebusaway.gtfs.model.ShapePoint>> shapePointIdMap = new HashMap<String, List<org.onebusaway.gtfs.model.ShapePoint>>();
	
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
		
		GtfsReader reader = new GtfsReader();
    	GtfsDaoImpl store = new GtfsDaoImpl();
    	
    	Long agencyCount = new Long(0);
    	Long routeCount = new Long(0);
    	Long stopCount = new Long(0);
    	Long stopTimeCount = new Long(0);
    	Long tripCount = new Long(0);
    	Long shapePointCount = new Long(0);
    	Long serviceCalendarCount = new Long(0);
    	Long serviceCalendarDateCount = new Long(0);
    	Long shapeCount = new Long(0);
    	
    	try {
    		
    		File gtfsFile = new File(Play.configuration.getProperty("application.publicDataDirectory"), snapshotMerge.snapshot.getFilename());
    		
    		reader.setInputLocation(gtfsFile);
        	reader.setEntityStore(store);
        	reader.run();
    	    	
        	Logger.info("GtfsImporter: importing agencies...");
        
        	GtfsSnapshotMergeTask agencyTask = new GtfsSnapshotMergeTask(snapshotMerge);
        	agencyTask.startTask();
        
        	
        	List<Agency> agencies = Agency.findAll();
        	
        	for (Agency agency : agencies)
        	{
        		agencyIdMap.put(agency.gtfsAgencyId, new BigInteger(agency.id.toString()));
        	}
        	
        	BigInteger primaryAgencyId = null;
        	
        	
        	
	    	for (org.onebusaway.gtfs.model.Agency gtfsAgency : reader.getAgencies()) {
	    		
	    		if(!agencyIdMap.containsKey(gtfsAgency.getId()))
	    		{
		    		Agency agency = new Agency(gtfsAgency);
		    		agency.save();
		    		
		    		agencyIdMap.put(agency.gtfsAgencyId, BigInteger.valueOf(agency.id));
		    		
		    		primaryAgencyId = BigInteger.valueOf(agency.id);
		    		
		    		agencyCount++;  		
	    		}
	    		else
	    			primaryAgencyId = agencyIdMap.get(gtfsAgency.getId());
	    		
	    	}
	    	
	    	Agency primaryAgency = Agency.findById(primaryAgencyId.longValue());
	    	
	    	agencyTask.completeTask("Imported " + agencyCount + " agencies.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	    	
	    	Logger.info("Agencies loaded: " + agencyCount.toString());
	    	Logger.info("GtfsImporter: importing routes...");
	    	
	    	GtfsSnapshotMergeTask routeTask = new GtfsSnapshotMergeTask(snapshotMerge);
	    	routeTask.startTask();
	    	
	        for (org.onebusaway.gtfs.model.Route gtfsRoute : store.getAllRoutes()) {
	        
	        	BigInteger agencyId = agencyIdMap.get(gtfsRoute.getAgency().getId());
	        	BigInteger routeId = Route.nativeInsert(snapshotMerge.em(), gtfsRoute, agencyId);
	            
	            routeIdMap.put(gtfsRoute.getId().toString(), routeId );
	           	          
	        	routeCount++;
	        }
	        
	        if(agencyCount > 1)
	    		primaryAgencyId = null;
	        
	        routeTask.completeTask("Imported " + routeCount + " routes.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	        
	        Logger.info("Routes loaded:" + routeCount.toString()); 
	        Logger.info("GtfsImporter: importing stops...");
	    	
	    	GtfsSnapshotMergeTask stopTask = new GtfsSnapshotMergeTask(snapshotMerge);
	    	stopTask.startTask();
	    	
	        for (org.onebusaway.gtfs.model.Stop gtfsStop : store.getAllStops()) {	     
	           
	        	BigInteger stopId = Stop.nativeInsert(snapshotMerge.em(), gtfsStop, primaryAgencyId);
	            stopIdMap.put(gtfsStop.getId().toString(), stopId );
	           	          
	        	stopCount++;
	        }
	        
	        stopTask.completeTask("Imported " + stopCount + " stops.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	        
	        Logger.info("Stops loaded: " + stopCount);
	        Logger.info("GtfsImporter: importing Shapes...");
	        
	        Logger.info("Calculating agency centroid for stops...");
	        
		    Point centroid = Stop.findCentroid(primaryAgencyId);
		    Logger.info("Center: " + centroid.getCoordinate().y + ", " + centroid.getCoordinate().x);
	        
		    primaryAgency.defaultLat = centroid.getCoordinate().y;
		    primaryAgency.defaultLon = centroid.getCoordinate().x;
		    
		    primaryAgency.save();
		    
	        GtfsSnapshotMergeTask tripShapeTask = new GtfsSnapshotMergeTask(snapshotMerge);
	        tripShapeTask.startTask();
	        
	        // import points
	        
	        for (org.onebusaway.gtfs.model.ShapePoint shapePoint : store.getAllShapePoints()) {
	        
	        	List<org.onebusaway.gtfs.model.ShapePoint> shapePoints  = shapePointIdMap.get(shapePoint.getShapeId().toString());
	        
	        	if(shapePoints  != null)
	        	{
	        		shapePoints.add(shapePoint);
	        	}
	        	else
	        	{
	        		shapePoints = new ArrayList<org.onebusaway.gtfs.model.ShapePoint>();
	        		shapePoints.add(shapePoint);
	        	
	        		shapePointIdMap.put(shapePoint.getShapeId().toString(), shapePoints);
	        	}
	        	
	        	shapePointCount++;

	        }
	        
	        
	        // sort/load points 
	        
	        
	        
	        for(String gtfsShapeId : shapePointIdMap.keySet())
	        {
	        	
	        	List<org.onebusaway.gtfs.model.ShapePoint> shapePoints  = shapePointIdMap.get(gtfsShapeId);
	        	
	        	Collections.sort(shapePoints);
	        	
	        	Double describedDistance = new Double(0);
	        	List<String> points = new ArrayList<String>();
	        	
	        	for(org.onebusaway.gtfs.model.ShapePoint shapePoint : shapePoints)
	        	{
	        		describedDistance += shapePoint.getDistTraveled();
	        		
	        		points.add(new Double(shapePoint.getLon()).toString() + " " + new Double(shapePoint.getLat()).toString());
	        		
	        		
	        		
	        	}
	        	
	        	String linestring = "LINESTRING(" + StringUtils.join(points, ", ") + ")";
	            
	        	BigInteger tripShapeId = TripShape.nativeInsert(snapshotMerge.em(), gtfsShapeId, linestring, describedDistance);
	        	
	            tripShapeIdMap.put(gtfsShapeId, tripShapeId);
	            
	            shapeCount++;
	        
	        }
	        
	        Logger.info("Shape points loaded: " + shapePointCount.toString());
	        Logger.info("Shapes loaded: " + shapeCount.toString());
	        
	        tripShapeTask.completeTask("Imported " + shapePointCount + " points in " + shapeCount + " shapes.", GtfsSnapshotMergeTaskStatus.SUCCESS);

	        
	        GtfsSnapshotMergeTask serviceCalendarsTask = new GtfsSnapshotMergeTask(snapshotMerge);
	        serviceCalendarsTask.startTask();
	        
	        Logger.info("GtfsImporter: importing Service Calendars...");
	    	
	        for (org.onebusaway.gtfs.model.ServiceCalendar gtfsService : store.getAllCalendars()) {
	        	
	        	BigInteger serviceId = ServiceCalendar.nativeInsert(snapshotMerge.em(), gtfsService, primaryAgencyId);
	        	        	
	        	serviceIdMap.put(gtfsService.getServiceId().toString(), serviceId);
	        	
	        	serviceCalendarCount++;
	        	
	        }
	    
	        
	        Logger.info("Service calendars loaded: " + serviceCalendarCount); 
	        
	        serviceCalendarsTask.completeTask("Imported " + serviceCalendarCount.toString() + " Service calendars.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	        
	        
	        Logger.info("GtfsImporter: importing Service Calendar dates...");
	        
	        GtfsSnapshotMergeTask serviceCalendarDatesTask = new GtfsSnapshotMergeTask(snapshotMerge);
	        serviceCalendarDatesTask.startTask();
	    	
	        for (org.onebusaway.gtfs.model.ServiceCalendarDate gtfsServiceDate : store.getAllCalendarDates()) {
	        	
	        	BigInteger serviceDateId = ServiceCalendarDate.nativeInsert(snapshotMerge.em(), gtfsServiceDate);
	        	
	        	serviceDateIdMap.put(gtfsServiceDate.getServiceId().toString(), serviceDateId);
	        	
	        	serviceCalendarDateCount++;
	        	
	        }
	    
	        serviceCalendarDatesTask.completeTask("Imported " + serviceCalendarDateCount.toString() + " Service calendar dates.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	        
	        Logger.info(" loaded: " + serviceCalendarDateCount); 
	        
	        Logger.info("GtfsImporter: importing trips...");
	        
	        GtfsSnapshotMergeTask tripsTask = new GtfsSnapshotMergeTask(snapshotMerge);
	        tripsTask.startTask();
	    	
	        for (org.onebusaway.gtfs.model.Trip gtfsTrip : store.getAllTrips()) {
	        	
	        	BigInteger routeId = routeIdMap.get(gtfsTrip.getRoute().getId().toString());
	        	
	        	BigInteger shapeId = null;
	        	
	        	if(gtfsTrip.getShapeId() != null)
	        		shapeId = tripShapeIdMap.get(gtfsTrip.getShapeId().toString());
	    
	        	BigInteger serviceId = serviceIdMap.containsKey(gtfsTrip.getServiceId().toString()) ? serviceIdMap.get(gtfsTrip.getServiceId().toString()) : null;
	        	BigInteger serviceDateId = serviceDateIdMap.containsKey(gtfsTrip.getServiceId().toString()) ? serviceDateIdMap.get(gtfsTrip.getServiceId().toString()) : null;
	        	
	        	BigInteger tripId = Trip.nativeInsert(snapshotMerge.em(), gtfsTrip, routeId, shapeId, serviceId, serviceDateId);
	        	        	
	        	tripIdMap.put(gtfsTrip.getId().toString(), tripId);
	        	
	        	tripCount++;
	        
	        }
	    
	        
	        Logger.info("Trips loaded: " + tripCount); 
	        
	        tripsTask.completeTask("Imported " + tripCount.toString() + " trips.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	        
	        
	        Logger.info("GtfsImporter: importing stopTimes...");
	    	
	        GtfsSnapshotMergeTask stopTimesTask = new GtfsSnapshotMergeTask(snapshotMerge);
	        stopTimesTask.startTask();
	   
	        for (org.onebusaway.gtfs.model.StopTime gtfsStopTime : store.getAllStopTimes()) {
	        	
	        	BigInteger stopId = stopIdMap.get(gtfsStopTime.getStop().getId().toString());
	        	BigInteger tripId = tripIdMap.get(gtfsStopTime.getTrip().getId().toString());
	        	
	        	BigInteger stopTimeId = StopTime.nativeInsert(snapshotMerge.em(), gtfsStopTime, tripId, stopId);
	        	
	        	StopSequence stopSequence = new StopSequence(stopId, gtfsStopTime.getStopSequence());
	        	
	        	if(!tripStopTimeMap.containsKey(tripId))
	        		tripStopTimeMap.put(tripId, new ArrayList<StopSequence>());
	        	
	        	tripStopTimeMap.get(tripId).add(stopSequence);
	        	
	        	stopTimeCount++;
	        }
	        
	      
	        Logger.info("StopTimes loaded: " + stopTimeCount.toString());
	        
	        stopTimesTask.completeTask("Imported " + stopTimeCount.toString() + " stop times.", GtfsSnapshotMergeTaskStatus.SUCCESS);
	        
	        String mergeDescription = new String("Imported GTFS file: " + agencyCount + " agencies; " + routeCount + " routes;" + stopCount + " stops; " +  stopTimeCount + " stopTimes; " + tripCount + " trips;" + shapePointCount + " shapePoints");
	        
	        snapshotMerge.complete(mergeDescription);
	        
	        inferTripPatterns(snapshotMerge.em());
	       
	        snapshotMerge.em().getTransaction().commit();
	        
    	}
        catch (Exception e) {
    		
        	Logger.error(e.toString()); 
        	
        	snapshotMerge.failed(e.toString());
    	}
	}
	
	private void addTripPatternToLookup(BigInteger patternId, ArrayList<StopSequence> stopTimes)
	{
		if(!tripPatternFirstStopMap.containsKey(stopTimes.get(0).stopId))
			tripPatternFirstStopMap.put(stopTimes.get(0).stopId, new ArrayList<BigInteger>());
		
		tripPatternFirstStopMap.get(stopTimes.get(0).stopId).add(patternId);
		tripPatternStopMap.put(patternId, stopTimes);
	}
			
	private void inferTripPatterns(EntityManager em)
	{
		Set<BigInteger> tripIds = tripStopTimeMap.keySet();
		for(BigInteger tripId : tripIds)
		{
			ArrayList<StopSequence> stopTimes = tripStopTimeMap.get(tripId);
			
			Collections.sort(stopTimes);
			
			BigInteger patternId = findExistingPattern(stopTimes);
			
			if(patternId == null)
			{
				patternId = TripPattern.createFromTrip(em, tripId);
				addTripPatternToLookup(patternId, stopTimes);
			}		
		
			Trip.em().createNativeQuery("UPDATE trip SET pattern_id = ? WHERE id = ?").setParameter(1, patternId).setParameter(2,  tripId).executeUpdate();
		}
	}
	
	private BigInteger findExistingPattern(ArrayList<StopSequence> stopTimes)
	{		
		ArrayList<BigInteger> candidatePatterns = tripPatternFirstStopMap.get(stopTimes.get(0).stopId);
		
		if(candidatePatterns == null)
			return null;
		
		for(BigInteger candidate : candidatePatterns)
		{
			ArrayList<StopSequence> patternStops = tripPatternStopMap.get(candidate);
			
			if(patternStops.size() != stopTimes.size())
				continue;
			
			int index = 0;
			for(StopSequence patternStop : patternStops)
			{
				if(!patternStop.stopId.equals(stopTimes.get(index).stopId));
					continue;
			}
			
			return candidate;
		}
		
		return null;	
	}
	
	
	

}

