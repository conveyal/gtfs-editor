package models.transit;


import java.math.BigInteger;
import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Transient;

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
    
    public Trip () {}
  
    
    public Trip(com.conveyal.gtfs.model.Trip trip, Route route, TripShape shape, TripPattern pattern, ServiceCalendar serviceCalendar) {
    	gtfsTripId = trip.trip_id;
    	tripHeadsign = trip.trip_headsign;
    	tripShortName = trip.trip_short_name;
    	tripDirection = trip.direction_id == 0 ? TripDirection.A : TripDirection.B;
    	blockId = trip.block_id;
    	this.route = route;
    	this.shape = shape;
    	this.pattern = pattern;
    	this.serviceCalendar = serviceCalendar;
    	
    	if (trip.wheelchair_accessible == 1)
    		this.wheelchairBoarding = AttributeAvailabilityType.AVAILABLE;
    	else if (trip.wheelchair_accessible == 2)
    		this.wheelchairBoarding = AttributeAvailabilityType.UNAVAILABLE;
    	else
    		this.wheelchairBoarding = AttributeAvailabilityType.UNKNOWN;
    	
    	useFrequency = false;
	}

	public static BigInteger nativeInsert(EntityManager em, com.conveyal.gtfs.model.Trip gtfsTrip, long routeId, Long shapeId, long serviceCalendarId, long pattId)
    
    {
		Query idQuery = em.createNativeQuery("SELECT NEXTVAL('hibernate_sequence');");
		BigInteger nextId = (BigInteger)idQuery.getSingleResult();
				
		TripDirection dir = gtfsTrip.direction_id == 1 ? TripDirection.A : TripDirection.B;
			
		Query q;
		
		if(shapeId != null)
			q = em.createNativeQuery("INSERT INTO trip (id, gtfstripid, tripheadsign, tripshortname, tripdirection, blockid, route_id, servicecalendar_id, useFrequency, pattern_id, shape_id)" +
	    	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
	    else
	    	q = em.createNativeQuery("INSERT INTO trip (id, gtfstripid, tripheadsign, tripshortname, tripdirection, blockid, route_id, servicecalendar_id, useFrequency, pattern_id)" +
			    	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
	    	
	      q.setParameter(1,  nextId)
	      .setParameter(2,  gtfsTrip.trip_id)
	      .setParameter(3,  gtfsTrip.trip_headsign)
	      .setParameter(4,  gtfsTrip.trip_short_name)
	      .setParameter(5,  dir.name())
	      .setParameter(6,  gtfsTrip.block_id)
	      .setParameter(7,  routeId)
	      .setParameter(8,  serviceCalendarId)
	      .setParameter(9,  false)
	      .setParameter(10, pattId);
	      
		if(shapeId != null)
	      q.setParameter(11,  shapeId);
		
	      
	      q.executeUpdate();
			
	    
		return nextId;
    }
  
    public ArrayList<StopTime> getStopTimes()
    {
    	ArrayList<StopTime> stopTimes = new ArrayList(StopTime.find("trip = ? ORDER BY stopSequence", this).fetch());
    	
    	return stopTimes;
    }

    @Transient
    @JsonIgnore
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
		ret.shape_id = shape == null ? null : shape.getGtfsId();
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
