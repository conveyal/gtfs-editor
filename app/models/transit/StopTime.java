package models.transit;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import play.db.jpa.Model;
import models.gtfs.GtfsSnapshot;

@Entity
@JsonIgnoreProperties({ "persistent", "entityId" })
public class StopTime extends Model {
	
    public Integer arrivalTime;
    public Integer departureTime;
 
    public Integer stopSequence;
    public String stopHeadsign;
    
    @ManyToOne
    public TripPatternStop patternStop;
    
    @Enumerated(EnumType.STRING)
    public StopTimePickupDropOffType pickupType;
    
    @Enumerated(EnumType.STRING)
    public StopTimePickupDropOffType dropOffType;
    
    public Double shapeDistTraveled;
    
    @JsonBackReference
    @ManyToOne
    public Trip trip;
    
    @ManyToOne
    public Stop stop;
    
    public StopTime()
    {
    	
    }
    
    public StopTime(org.onebusaway.gtfs.model.StopTime stopTime, Trip trip, Stop stop) {
        
    	this.arrivalTime = stopTime.getArrivalTime();
    	this.departureTime = stopTime.getDepartureTime();
    	this.stopSequence = stopTime.getStopSequence();
    	this.stopHeadsign = stopTime.getStopHeadsign();
    	this.pickupType = mapGtfsPickupDropOffType(stopTime.getPickupType());
    	this.dropOffType = mapGtfsPickupDropOffType(stopTime.getDropOffType());
    	this.shapeDistTraveled = stopTime.getShapeDistTraveled();
    	
    	this.trip = trip;
    	this.stop = stop;
    } 
    
    public static void replaceStop(Stop newStop, Stop oldStop) {
    	
    	 StopTime.em().createNativeQuery("UPDATE stoptime SET stop_id = ? WHERE stop_id = ?;")
    	          .setParameter(1, newStop.id)
    	          .setParameter(2, oldStop.id)
    	          .executeUpdate();
    }
    
    public static StopTimePickupDropOffType mapGtfsPickupDropOffType(Integer pickupDropOffType)
    {
    	switch(pickupDropOffType)
    	{
    	case 0:
			return StopTimePickupDropOffType.SCHEDULED;
		case 1:
			return StopTimePickupDropOffType.NONE;
		case 2:
			return StopTimePickupDropOffType.AGENCY;
		case 3:
			return StopTimePickupDropOffType.DRIVER;
		default:
			return null;
    	}
    }
    
    public static BigInteger nativeInsert(EntityManager em, org.onebusaway.gtfs.model.StopTime gtfsStopTime, BigInteger trip_id, BigInteger stop_id)
    {
    	Query idQuery = em.createNativeQuery("SELECT NEXTVAL('hibernate_sequence');");
    	BigInteger nextId = (BigInteger)idQuery.getSingleResult();
    	
        em.createNativeQuery("INSERT INTO stoptime (id, arrivaltime, departuretime, stopsequence, stopheadsign, pickuptype, dropofftype, shapedisttraveled, trip_id, stop_id)" +
        	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
          .setParameter(1, nextId)
          .setParameter(2, gtfsStopTime.getArrivalTime())
          .setParameter(3, gtfsStopTime.getDepartureTime())
          .setParameter(4, gtfsStopTime.getStopSequence())
          .setParameter(5, gtfsStopTime.getStopHeadsign())
          .setParameter(6, mapGtfsPickupDropOffType(gtfsStopTime.getPickupType()).name())
          .setParameter(7, mapGtfsPickupDropOffType(gtfsStopTime.getDropOffType()).name())
          .setParameter(8, gtfsStopTime.getShapeDistTraveled())
          .setParameter(9, trip_id)
          .setParameter(10, stop_id)
          .executeUpdate();
        
        return nextId;
    }

	public com.conveyal.gtfs.model.StopTime toGtfs() {
	    com.conveyal.gtfs.model.StopTime st = new com.conveyal.gtfs.model.StopTime();
	    st.trip_id = trip.getGtfsId();
	    st.stop_id = stop.getGtfsId();
	    st.arrival_time = arrivalTime != null ? arrivalTime : st.INT_MISSING;
	    st.departure_time = departureTime != null ? departureTime : st.INT_MISSING;
	    st.pickup_type = pickupType != null ? pickupType.toGtfsValue() : 0;
	    st.drop_off_type = dropOffType != null ? dropOffType.toGtfsValue() : 0;
	    st.shape_dist_traveled = shapeDistTraveled == null ? Double.NaN : shapeDistTraveled;
	    st.stop_sequence = stopSequence;
	    
	    if (patternStop != null && patternStop.timepoint != null)
	    	st.timepoint = patternStop.timepoint ? 1 : 0;
	    
	    return st;
	}

    /*public String getDepartureTimeString()
    {
    	long dateMs = departureTime * 1000;
    	Date departure = new Date(dateMs);
    	SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss a");
    	return df.format(departure);
    	
    	
    }
    
    public String getSimpleDepartureTimeString()
    {
    	long dateMs = departureTime * 1000;
    	Date departure = new Date(dateMs);
    	SimpleDateFormat df = new SimpleDateFormat("h:mma");
    	return df.format(departure);
    	
    	
    }*/
}
