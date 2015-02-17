package models.transit;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.mapdb.Fun.Tuple2;

import models.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mysql.jdbc.log.Log;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import play.Logger;

/** does not extend model because has tuple key */
public class Stop implements Serializable {
	public static final long serialVersionUID = 1;

    public String gtfsStopId;
    public String stopCode;
    public String stopName;
    public String stopDesc;
    public String zoneId;
    public String stopUrl;
    
    /** Agency ID, Stop ID */
    public Tuple2<String, String> id;

    public String stopIconUrl;

    public Agency agency;

    public LocationType locationType;

    public AttributeAvailabilityType bikeParking;
    
    public AttributeAvailabilityType carParking;

    public AttributeAvailabilityType wheelchairBoarding;
    
    public StopTimePickupDropOffType pickupType;
    
    public StopTimePickupDropOffType dropOffType;

    public String parentStation;
    
    // Major stop is a custom field; it has no corralary in the GTFS.
    public Boolean majorStop;
    
    @JsonIgnore
    public Point location;

    @JsonProperty("location")
    public Hashtable getLocation() {
        Hashtable loc = new Hashtable();
        loc.put("lat", this.location.getY());
        loc.put("lng", this.location.getX());
        return loc;
    }

    public void setLocation(Hashtable<String, Double> loc) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        this.location = geometryFactory.createPoint(new Coordinate(loc.get("lng"), loc.get("lat")));;
    }
       
    public Point locationPoint() {
   
    	Hashtable<String, Double> loc = this.getLocation();
    	GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(loc.get("lng"), loc.get("lat")));
    }

    public Stop(com.conveyal.gtfs.model.Stop stop, GeometryFactory geometryFactory, Agency agency) {

        this.gtfsStopId = stop.stop_id;
        this.stopCode = stop.stop_code;
        this.stopName = stop.stop_name;
        this.stopDesc = stop.stop_desc;
        this.zoneId = stop.zone_id;
        this.stopUrl = stop.stop_url != null ? stop.stop_url.toString() : null;
        this.locationType = stop.location_type == 1 ? LocationType.STATION : LocationType.STOP;
        this.parentStation = stop.parent_station;
        this.pickupType = StopTimePickupDropOffType.SCHEDULED;
        this.dropOffType = StopTimePickupDropOffType.SCHEDULED;

        this.id = new Tuple2(agency.id, stop.stop_id);
        
        this.location  =  geometryFactory.createPoint(new Coordinate(stop.stop_lat,stop.stop_lon));
    }

    public Stop(Agency agency, String stopName,  String stopCode,  String stopUrl, String stopDesc, Double lat, Double lon) {
        this.agency = agency;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.stopDesc = stopDesc;
        this.stopUrl = stopUrl;
        this.locationType = LocationType.STOP;
        this.pickupType = StopTimePickupDropOffType.SCHEDULED;
        this.dropOffType = StopTimePickupDropOffType.SCHEDULED;
        this.id = new Tuple2(agency.id, UUID.randomUUID().toString());

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        this.location = geometryFactory.createPoint(new Coordinate(lon, lat));;
    }

	public com.conveyal.gtfs.model.Stop toGtfs() {
		com.conveyal.gtfs.model.Stop ret = new com.conveyal.gtfs.model.Stop();
		ret.stop_id = getGtfsId();
		ret.stop_code = stopCode;
		ret.stop_desc = stopDesc;
		// we can't use this.location directly, because Hibernate masks that field with the getter...
		Hashtable<String, Double> loc = getLocation();
		ret.stop_lat = loc.get("lat");
		ret.stop_lon = loc.get("lng");
		
		if (stopName != null && !stopName.isEmpty())
			ret.stop_name = stopName;
		else
			ret.stop_name = id.toString();
		
		try {
			ret.stop_url = new URL(stopUrl);
		} catch (MalformedURLException e) {
			Logger.warn("Unable to coerce stop URL {} to URL", stopUrl);
			ret.stop_url = null;
		}
		
		return ret;
	}

	@JsonIgnore
	public String getGtfsId() {
		if(gtfsStopId != null && !gtfsStopId.isEmpty())
			return gtfsStopId;
		else
			return "STOP_" + id;
	}
}
