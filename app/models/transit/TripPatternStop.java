package models.transit;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import org.mapdb.Fun.Tuple2;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import play.Logger;
import utils.JacksonSerializers;

/** A stop on a trip pattern. This is not a model, as it is stored in a list within trippattern */
public class TripPatternStop implements Cloneable, Serializable {
	public static final long serialVersionUID = 1;

    public String stopId;

	public int defaultTravelTime;
	public int defaultDwellTime;
	
	/** 
	 * Is this stop a timepoint?
	 * 
	 * If null, no timepoint information will be exported for this stop.
	 */
	public Boolean timepoint;

	public Double shapeDistTraveled;

	public TripPatternStop()
	{

	}

	public TripPatternStop(Stop stop, Integer defaultTravelTime)
	{
		this.stopId = stop.id;
		this.defaultTravelTime = defaultTravelTime;
	}
	
	public TripPatternStop clone () throws CloneNotSupportedException {
		return (TripPatternStop) super.clone();
	}
}


