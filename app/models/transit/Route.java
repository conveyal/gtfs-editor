package models.transit;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import models.Model;
import models.transit.RouteType;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.MultiLineString;

import datastore.AgencyTx;
import datastore.GlobalTx;
import play.Logger;

public class Route extends Model implements Cloneable, Serializable {
	public static final long serialVersionUID = 1;
	
	public String gtfsRouteId;
    public String routeShortName;
    public String routeLongName;
 
    public String routeDesc;

    public String routeTypeId;

    public String routeUrl;
    public String routeColor;
    public String routeTextColor;

    // Custom Fields
    public String comments;

    public StatusType status;

    public String agencyId;

    //public GisRoute gisRoute;

    //public GisUpload gisUpload;
    
    public AttributeAvailabilityType wheelchairBoarding;
    
    /** on which days does this route have service? Derived from calendars on render */
    public transient Boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday;

    // add getters so Jackson will serialize
    
    @JsonProperty("monday")
    public Boolean jsonGetMonday() {
		return monday;
	}

    @JsonProperty("tuesday")
	public Boolean jsonGetTuesday() {
		return tuesday;
	}

    @JsonProperty("wednesday")
	public Boolean jsonGetWednesday() {
		return wednesday;
	}
    
    @JsonProperty("thursday")
	public Boolean jsonGetThursday() {
		return thursday;
	}

    @JsonProperty("friday")
    public Boolean jsonGetFriday() {
		return friday;
	}

    @JsonProperty("saturday")
    public Boolean jsonGetSaturday() {
		return saturday;
	}

    @JsonProperty("sunday")
	public Boolean jsonGetSunday() {
		return sunday;
	}

	public Route () {}

    public Route(com.conveyal.gtfs.model.Route route,  Agency agency, String routeTypeId) {	
        this.gtfsRouteId = route.route_id;
        this.routeShortName = route.route_short_name;
        this.routeLongName = route.route_long_name;
        this.routeDesc = route.route_desc;
        
        this.routeTypeId = routeTypeId;
        
        this.routeUrl = route.route_url != null ? route.route_url.toString() : null;
        this.routeColor = route.route_color;
        this.routeTextColor = route.route_text_color;

        this.agencyId = agency.id;
    }


    public Route(String routeShortName, String routeLongName, RouteType routeType, String routeDescription,  Agency agency) {
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.routeTypeId = routeType.id;
        this.routeDesc = routeDescription;

        this.agencyId = agency.id;
    }

	public com.conveyal.gtfs.model.Route toGtfs(com.conveyal.gtfs.model.Agency a, GlobalTx tx) {
		com.conveyal.gtfs.model.Route ret = new com.conveyal.gtfs.model.Route();
		ret.agency = a;
		ret.route_color = routeColor;
		ret.route_desc = routeDesc;
		ret.route_id = getGtfsId();
		ret.route_long_name = routeLongName;
		ret.route_short_name = routeShortName;
		ret.route_text_color = routeTextColor;
		// TODO also handle HVT types here
		//ret.route_type = mapGtfsRouteType(routeTypeId);
		try {
			ret.route_url = new URL(routeUrl);
		} catch (MalformedURLException e) {
			Logger.warn("Cannot coerce route URL {} to URL", routeUrl);
			ret.route_url = null;
		}
		
		ret.route_type = tx.routeTypes.get(this.routeTypeId).gtfsRouteType.toGtfs();
		
		return ret;
	}

	@JsonIgnore
	public String getGtfsId() {
		if(gtfsRouteId != null && !gtfsRouteId.isEmpty())
			return gtfsRouteId;
		else
			return id;
	}


	/**
	 * Get a name for this combining the short name and long name as available.
	 * @return
	 */
	@JsonIgnore
	public String getName() {
		if (routeShortName == null && routeLongName == null)
			return id;
		else if (routeShortName == null)
			return routeLongName;
		else if (routeLongName == null)
			return routeShortName;
		else
			return routeShortName + " " + routeLongName;
	
	}

	// Add information about the days of week this route is active
	public void addDerivedInfo(AgencyTx tx) {
		monday = tuesday = wednesday = thursday = friday = saturday = sunday = false;
		
		for (Trip trip : tx.getTripsByRoute(this.id)) {
			ServiceCalendar cal = tx.calendars.get(trip.calendarId);
			
			if (cal.monday)
				monday = true;
			
			if (cal.tuesday)
				tuesday = true;
			
			if (cal.wednesday)
				wednesday = true;
			
			if (cal.thursday)
				thursday = true;
			
			if (cal.friday)
				friday = true;
			
			if (cal.saturday)
				saturday = true;
			
			if (cal.sunday)
				sunday = true;
			
			if (monday && tuesday && wednesday && thursday && friday && saturday && sunday)
				// optimization: no point in continuing
				break;
		}
	}
	
	public Route clone () throws CloneNotSupportedException {
		return (Route) super.clone();
	}
}
