package models.transit;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public String stopId;
    
    public StopTime()
    {
    	
    }
    
    public StopTime(com.conveyal.gtfs.model.StopTime stopTime, String stopId) {
        
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

	public StopTime clone () {
		StopTime ret = new StopTime();
		ret.arrivalTime = arrivalTime;
		ret.departureTime = departureTime;
		ret.pickupType = pickupType;
		ret.dropOffType = dropOffType;
		ret.stopHeadsign = stopHeadsign;
		ret.shapeDistTraveled = shapeDistTraveled;
		ret.stopId = stopId;
		return ret;
	}
}
