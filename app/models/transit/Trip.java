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
    
    public Boolean useFrequency;
    
    public Integer startTime;
    public Integer endTime;
    
    public Integer headway;
  
    
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
				q = em.createNativeQuery("INSERT INTO trip (id, gtfstripid, tripheadsign, tripshortname, tripdirection, blockid, route_id, servicecalendar_id, shape_id)" +
		    	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);");
		    else
		    	q = em.createNativeQuery("INSERT INTO trip (id, gtfstripid, tripheadsign, tripshortname, tripdirection, blockid, route_id, servicecalendar_id)" +
				    	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?);");
		    	
		      q.setParameter(1,  nextId)
		      .setParameter(2,  gtfsTrip.getId().toString())
		      .setParameter(3,  gtfsTrip.getTripHeadsign())
		      .setParameter(4,  gtfsTrip.getRouteShortName())
		      .setParameter(5,  dir.name())
		      .setParameter(6,  gtfsTrip.getBlockId())
		      .setParameter(7,  routeId)
		      .setParameter(8,  serviceCalendarId);
		      
			if(shapeId != null)
		      q.setParameter(9,  shapeId);
			
		      
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
    
}   
