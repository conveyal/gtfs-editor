package models.transit;


import java.math.BigInteger;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import com.conveyal.gtfs.model.Frequency;
import com.conveyal.gtfs.model.Service;

import play.Logger;
import play.db.jpa.Model;
import models.gtfs.GtfsSnapshot;

@JsonIgnoreProperties({"entityId", "persistent"})
@Entity
public class Trip extends Model {

    public String gtfsTripId;
    public String tripHeadsign;
    public String tripShortName;

    public String tripDescription;
    
    @Enumerated(EnumType.STRING)
    public TripDirection tripDirection;
    
    public String blockId;
    
    @ManyToOne
    public Route route;
    
    @JsonIgnore
    @ManyToOne
    public TripShape shape;
    
    @ManyToOne
    public TripPattern pattern;
 
    @ManyToOne
    public ServiceCalendar serviceCalendar;
    
    @ManyToOne
    public ServiceCalendarDate serviceCalendarDate;
    
    @Enumerated(EnumType.STRING)
    public AttributeAvailabilityType wheelchairBoarding;
    
    public Boolean useFrequency;
    
    public Integer startTime;
    public Integer endTime;
    
    public Integer headway;
    public Boolean invalid;
  
    
    public static BigInteger nativeInsert(EntityManager em, org.onebusaway.gtfs.model.Trip gtfsTrip, BigInteger routeId, BigInteger shapeId, BigInteger serviceCalendarId, BigInteger serviceCalendarDateId)
    
    {
		Query idQuery = em.createNativeQuery("SELECT NEXTVAL('hibernate_sequence');");
		BigInteger nextId = (BigInteger)idQuery.getSingleResult();
				
		TripDirection dir;
		
		// annoying... OBA gtfs reader should return an int!
		try {
			if(Integer.parseInt(gtfsTrip.getDirectionId()) == 1 )
				dir = TripDirection.B;
			else
				dir = TripDirection.A;
		}
		catch (Exception e) {
			dir = TripDirection.A;
		}
		
		if(serviceCalendarId != null)
		{
			
			Query q;
			
			if(shapeId != null)
				q = em.createNativeQuery("INSERT INTO trip (id, gtfstripid, tripheadsign, tripshortname, tripdirection, blockid, route_id, servicecalendar_id, useFrequency, shape_id)" +
		    	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
		    else
		    	q = em.createNativeQuery("INSERT INTO trip (id, gtfstripid, tripheadsign, tripshortname, tripdirection, blockid, route_id, servicecalendar_id, useFrequency)" +
				    	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);");
		    	
		      q.setParameter(1,  nextId)
		      .setParameter(2,  gtfsTrip.getId().toString())
		      .setParameter(3,  gtfsTrip.getTripHeadsign())
		      .setParameter(4,  gtfsTrip.getRouteShortName())
		      .setParameter(5,  dir.name())
		      .setParameter(6,  gtfsTrip.getBlockId())
		      .setParameter(7,  routeId)
		      .setParameter(8,  serviceCalendarId)
		      .setParameter(9,  false);
		      
			if(shapeId != null)
		      q.setParameter(10,  shapeId);
			
		      
		      q.executeUpdate();
		}
		else if(serviceCalendarDateId != null)
		{
Query q;
			
			if(shapeId != null)
				q = em.createNativeQuery("INSERT INTO trip (id, gtfstripid, tripheadsign, tripshortname, tripdirection, blockid, route_id, servicecalendar_id, shape_id)" +
		    	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);");
		    else
		    	q = em.createNativeQuery("INSERT INTO trip (id, gtfstripid, tripheadsign, tripshortname, tripdirection, blockid, route_id, servicecalendar_id)" +
				    	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
		    	
		      q.setParameter(1,  nextId)
		      .setParameter(2,  gtfsTrip.getId().toString())
		      .setParameter(3,  gtfsTrip.getTripHeadsign())
		      .setParameter(4,  gtfsTrip.getRouteShortName())
		      .setParameter(5,  dir)
		      .setParameter(6,  gtfsTrip.getBlockId())
		      .setParameter(7,  routeId)
		      .setParameter(8,  serviceCalendarDateId);
		      
		      if(shapeId != null)
		        q.setParameter(9,  shapeId);
		      	      
		      q.executeUpdate();
		}
		else
		{
			Logger.error("Missing vaild serivce id for trip " + gtfsTrip.getId().toString());
		}
			
	    
		return nextId;
    }
  
    public ArrayList<StopTime> getStopTimes()
    {
    	ArrayList<StopTime> stopTimes = new ArrayList(StopTime.find("trip = ? ORDER BY stopSequence", this).fetch());
    	
    	return stopTimes;
    }

    public String getGtfsId () {
		if (gtfsTripId != null && !gtfsTripId.isEmpty())
			return gtfsTripId;
		else
			return id.toString();
    }
    
	public com.conveyal.gtfs.model.Trip toGtfs(com.conveyal.gtfs.model.Route route, Service service) {
		com.conveyal.gtfs.model.Trip ret = new com.conveyal.gtfs.model.Trip();
		
		ret.block_id = blockId;
		ret.route = route;
		ret.trip_id = getGtfsId();
		ret.service = service;
		ret.shape_id = shape.getGtfsId();
		ret.trip_headsign = tripHeadsign;
		ret.trip_short_name = tripShortName;
		ret.direction_id = tripDirection == tripDirection.A ? 0 : 1;
		ret.block_id = blockId;
		
		
		if (wheelchairBoarding != null) {
			if (wheelchairBoarding.equals(AttributeAvailabilityType.AVAILABLE))
				ret.wheelchair_accessible = 1;
			
			else if (wheelchairBoarding.equals(AttributeAvailabilityType.UNAVAILABLE))
				ret.wheelchair_accessible = 2;
			
			else
				ret.wheelchair_accessible = 0;
			
		}
		else if (pattern.route.wheelchairBoarding != null) {
			if(pattern.route.wheelchairBoarding.equals(AttributeAvailabilityType.AVAILABLE))
				ret.wheelchair_accessible = 1;
			
			else if (pattern.route.wheelchairBoarding.equals(AttributeAvailabilityType.UNAVAILABLE))
				ret.wheelchair_accessible = 2;
			
			else
				ret.wheelchair_accessible = 0;
			
		}
		
		return ret;
	}

	/** get the frequencies.txt entry for this trip, or null if this trip should not be in frequencies.txt */
	public Frequency getFrequency(com.conveyal.gtfs.model.Trip trip) {
		if (useFrequency == null || !useFrequency || headway <= 0 || trip.trip_id != getGtfsId())
			return null;
		
		Frequency ret = new Frequency();
		ret.start_time = startTime;
		ret.end_time = endTime;
		ret.headway_secs = headway;
		ret.trip = trip;
		
		return ret;
	}
    
}   
