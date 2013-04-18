package models.transit;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonManagedReference;
import org.hibernate.annotations.Type;

import play.Logger;
import play.db.jpa.Model;

import models.gtfs.GtfsSnapshot;

@JsonIgnoreProperties({"entityId", "persistent"})
@Entity
public class TripPattern extends Model {

    public String name;
    public String headsign;

    
    @ManyToOne
    public TripShape shape;

    @ManyToOne
    public Route route;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL)
    public List<TripPatternStop> patternStops;

    public Boolean longest;

    public Boolean weekday;
    public Boolean saturday;
    public Boolean sunday;

    public Boolean useFrequency;

    public Integer startTime;
    public Integer endTime;

    public Integer headway;

    @JsonCreator
    public static TripPattern factory(long id) {
      return TripPattern.findById(id);
    }

    @JsonCreator
    public static TripPattern factory(String id) {
      return TripPattern.findById(Long.parseLong(id));
    }

    public TripPattern()
    {

    }

    public TripPattern(String name, String headsign, TripShape shape, Route route)
    {
    	this.name = name;
    	this.headsign = headsign;
    	this.shape = shape;
    	this.route = route;
    }

    public static BigInteger createFromTrip(EntityManager em, BigInteger tripId)
    {
    	Trip trip = Trip.findById(tripId.longValue());

    	Query idQuery = em.createNativeQuery("SELECT NEXTVAL('hibernate_sequence');");
		BigInteger tripPatternId = (BigInteger)idQuery.getSingleResult();

		Query q;

		if(trip.shape != null)
			q = em.createNativeQuery("INSERT INTO trippattern (id, name, route_id, headsign, shape_id)" +
	    	"  VALUES(?, ?, ?, ?, ?);");
		else
			q = em.createNativeQuery("INSERT INTO trippattern (id, name, route_id, headsign)" +
			    	"  VALUES(?, ?, ?, ?);");

	      q.setParameter(1,  tripPatternId.longValue())
	      .setParameter(2,  trip.route.routeShortName + " (" + trip.tripHeadsign + ")")
	      .setParameter(3,  trip.route.id)
	      .setParameter(4,  trip.tripHeadsign);

	      if(trip.shape != null)
	       q.setParameter(5,  trip.shape.id);

	      q.executeUpdate();


    	ArrayList<StopTime> stopTimes = trip.getStopTimes();

    	Integer previousDepartureTime = 0;
    	Double previousDistance = new Double(0);

    	Boolean firstStop = true;

    	for(StopTime stopTime : stopTimes)
    	{
    		BigInteger tripPatternStopId = (BigInteger)idQuery.getSingleResult();

    		q = em.createNativeQuery("INSERT INTO trippatternstop (id, pattern_id, stop_id, stopsequence, defaultdwelltime, defaultdistance, defaulttraveltime)" +
	    	"  VALUES(?, ?, ?, ?, ?, ?, ?);");

    		q.setParameter(1,  tripPatternStopId.longValue());
    		q.setParameter(2,  tripPatternId.longValue());
    		q.setParameter(3,  stopTime.stop.id);
    		q.setParameter(4,  stopTime.stopSequence);
    		q.setParameter(5,  stopTime.departureTime - stopTime.arrivalTime);

    		if(firstStop)
    		{
    			previousDepartureTime = stopTime.departureTime;

    			q.setParameter(6,  new Double(0));
    			q.setParameter(7,  0);

    			firstStop = false;
    		}
    		else
    		{
    			q.setParameter(6,  stopTime.shapeDistTraveled - previousDistance);
    			q.setParameter(7,  stopTime.arrivalTime - previousDepartureTime);

    			previousDepartureTime = stopTime.departureTime;
    			previousDistance = stopTime.shapeDistTraveled;
    		}

    		q.executeUpdate();
    	}

    	Logger.info("Adding trip pattern: " + trip.route.routeShortName + " (" + trip.tripHeadsign + ")");

    	return tripPatternId;
    }
}
