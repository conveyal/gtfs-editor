package models.transit;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import org.mapdb.Fun.Tuple2;

import play.Logger;

/** A stop on a trip pattern. This is not a model, as it is stored in a list within trippattern */
public class TripPatternStop implements Serializable {
	public static final long serialVersionUID = 1;
	
    public TripPattern pattern;

    public Tuple2<String, String> stopId;

	public Double defaultDistance;

	public int defaultTravelTime;
	public int defaultDwellTime;
	
	/** 
	 * Is this stop a timepoint?
	 * 
	 * If null, no timepoint information will be exported for this stop.
	 */
	public Boolean timepoint;

	public TripPatternStop()
	{

	}

	public TripPatternStop(TripPattern pattern, Stop stop, Integer defaultTravelTime)
	{
		this.pattern = pattern;
		this.stopId = stop.id;
		this.defaultTravelTime = defaultTravelTime;

		this.defaultDistance = 0.0;
	}
}


