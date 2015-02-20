package models.transit;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;
import org.mapdb.Fun.Tuple2;

import utils.JacksonSerializers;

import com.conveyal.gtfs.model.Entity;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents a stop time. This is not a model, as it is stored directly as a list in Trip.
 * @author mattwigway
 *
 */
public class StopTime implements Serializable {
	public static final long serialVersionUID = 1;
	
    public Integer arrivalTime;
    public Integer departureTime;
    
    public String stopHeadsign;
    
    /* reference to trip pattern stop is implied based on position, no stop sequence needed */
    
    public StopTimePickupDropOffType pickupType;
    
    public StopTimePickupDropOffType dropOffType;
    
    public double shapeDistTraveled;
    
    @JsonSerialize(using=JacksonSerializers.Tuple2Serializer.class)
    @JsonDeserialize(using=JacksonSerializers.Tuple2Deserializer.class)
    public Tuple2<String, String> stopId;
    
    public StopTime()
    {
    	
    }
    
    public StopTime(com.conveyal.gtfs.model.StopTime stopTime, Tuple2<String, String> stopId) {
        
    	this.arrivalTime = stopTime.arrival_time;
    	this.departureTime = stopTime.departure_time;
    	this.stopHeadsign = stopTime.stop_headsign;
    	this.pickupType = mapGtfsPickupDropOffType(stopTime.pickup_type);
    	this.dropOffType = mapGtfsPickupDropOffType(stopTime.drop_off_type);
    	this.shapeDistTraveled = stopTime.shape_dist_traveled;
    	
    	this.stopId = stopId;
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

    // TODO fix
	/*public com.conveyal.gtfs.model.StopTime toGtfs() {
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
	}*/
}
