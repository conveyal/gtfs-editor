package models.transit;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import models.Model;

import org.hibernate.annotations.Type;

import com.conveyal.gtfs.GTFSFeed;
import com.conveyal.gtfs.model.Frequency;
import com.conveyal.gtfs.model.Service;
import com.fasterxml.jackson.annotation.JsonIgnore;

import datastore.AgencyTx;
import play.Logger;


public class Trip extends Model implements Serializable {
	public static final long serialVersionUID = 1;

    public String gtfsTripId;
    public String tripHeadsign;
    public String tripShortName;

    public String tripDescription;
    
    public TripDirection tripDirection;
    
    public String blockId;
    
    public String routeId;
        
    public String patternId;
 
    public String calendarId;
    
    public AttributeAvailabilityType wheelchairBoarding;
    
    public Boolean useFrequency;
    
    public Integer startTime;
    public Integer endTime;
    
    public Integer headway;
    public Boolean invalid;
    
    public List<StopTime> stopTimes;

	public String agencyId;
    
    public Trip () {}
  
    /** Create a trips entry from a GTFS trip. Does not import stop times. */
    public Trip(com.conveyal.gtfs.model.Trip trip, Route route, TripPattern pattern, ServiceCalendar serviceCalendar) {
    	gtfsTripId = trip.trip_id;
    	tripHeadsign = trip.trip_headsign;
    	tripShortName = trip.trip_short_name;
    	tripDirection = trip.direction_id == 0 ? TripDirection.A : TripDirection.B;
    	blockId = trip.block_id;
    	this.routeId = route.id;
    	this.patternId = pattern.id;
    	this.calendarId = serviceCalendar.id;
    	this.agencyId = route.agencyId;
    	this.stopTimes = new ArrayList<StopTime>();
    	
    	if (trip.wheelchair_accessible == 1)
    		this.wheelchairBoarding = AttributeAvailabilityType.AVAILABLE;
    	else if (trip.wheelchair_accessible == 2)
    		this.wheelchairBoarding = AttributeAvailabilityType.UNAVAILABLE;
    	else
    		this.wheelchairBoarding = AttributeAvailabilityType.UNKNOWN;
    	
    	useFrequency = false;
	}

    @JsonIgnore
    public String getGtfsId () {
		if (gtfsTripId != null && !gtfsTripId.isEmpty())
			return gtfsTripId;
		else
			return id.toString();
    }
    
	/*public com.conveyal.gtfs.model.Trip toGtfs(com.conveyal.gtfs.model.Route route, Service service) {
		com.conveyal.gtfs.model.Trip ret = new com.conveyal.gtfs.model.Trip();
		
		ret.block_id = blockId;
		ret.route = route;
		ret.trip_id = getGtfsId();
		ret.service = service;
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
	}*/

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

	public Trip clone () {
		Trip ret = new Trip();
		ret.id = this.id;
		ret.agencyId = this.agencyId;
		ret.blockId = this.blockId;
		ret.calendarId = this.calendarId;
		ret.routeId = this.routeId;
		ret.endTime = this.endTime;
		ret.startTime = this.startTime;
		ret.headway = this.headway;
		ret.invalid = this.invalid;
		ret.patternId = this.patternId;
		ret.tripDescription = this.tripDescription;
		ret.tripDirection = this.tripDirection;
		ret.gtfsTripId = this.gtfsTripId;
		ret.tripHeadsign = this.tripHeadsign;
		ret.tripShortName = this.tripShortName;
		ret.wheelchairBoarding = this.wheelchairBoarding;
		ret.useFrequency = this.useFrequency;

		// duplicate the stop times
		ret.stopTimes = new ArrayList<StopTime>();

		for (StopTime st : stopTimes) {
			ret.stopTimes.add(st == null ? null : st.clone());
		}

		return ret;
	}
}   
