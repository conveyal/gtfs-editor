package models.transit;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import datastore.AgencyTx;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import models.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mysql.jdbc.log.Log;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import play.Logger;
import utils.JacksonSerializers;

public class Stop extends Model implements Serializable {
	public static final long serialVersionUID = 1;
	
	private static GeometryFactory geometryFactory = new GeometryFactory();

    public String gtfsStopId;
    public String stopCode;
    public String stopName;
    public String stopDesc;
    public String zoneId;
    public String stopUrl;

    public String stopIconUrl;

    public String agencyId;

    public LocationType locationType;

    public AttributeAvailabilityType bikeParking;
    
    public AttributeAvailabilityType carParking;

    public AttributeAvailabilityType wheelchairBoarding;
    
    public StopTimePickupDropOffType pickupType;
    
    public StopTimePickupDropOffType dropOffType;

    public String parentStation;
    
    // Major stop is a custom field; it has no corrolary in the GTFS.
    public Boolean majorStop;
    
    @JsonIgnore
    public Point location;

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
        
        this.location  =  geometryFactory.createPoint(new Coordinate(stop.stop_lon,stop.stop_lat));
        
        this.agencyId = agency.id;
    }

    public Stop(Agency agency, String stopName,  String stopCode,  String stopUrl, String stopDesc, Double lat, Double lon) {
        this.agencyId = agency.id;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.stopDesc = stopDesc;
        this.stopUrl = stopUrl;
        this.locationType = LocationType.STOP;
        this.pickupType = StopTimePickupDropOffType.SCHEDULED;
        this.dropOffType = StopTimePickupDropOffType.SCHEDULED;
        
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        this.location = geometryFactory.createPoint(new Coordinate(lon, lat));
    }
    
    /** Create a stop. Note that this does *not* generate an ID, as you have to set the agency first */
    public Stop () {}
    
    public double getLat () {
    	return location.getY();
    }
    
    public double getLon () {
    	return location.getX();
    }
    
    @JsonCreator
    public static Stop fromJson(@JsonProperty("lat") double lat, @JsonProperty("lon") double lon) {
    	Stop ret = new Stop();
    	ret.location = geometryFactory.createPoint(new Coordinate(lon, lat));    	
    	return ret;
    }

	public com.conveyal.gtfs.model.Stop toGtfs() {
		com.conveyal.gtfs.model.Stop ret = new com.conveyal.gtfs.model.Stop();
		ret.stop_id = getGtfsId();
		ret.stop_code = stopCode;
		ret.stop_desc = stopDesc;
		ret.stop_lat = location.getY();
		ret.stop_lon = location.getX();
		
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

    /** Merge the given stops IDs within the given AgencyTx, deleting stops and updating trip patterns and trips */
    public static void merge (List<String> stopIds, AgencyTx tx) {
        Stop target = tx.stops.get(stopIds.get(0));
        for (int i = 1; i < stopIds.size(); i++) {
            Stop source = tx.stops.get(stopIds.get(i));

            // find all the patterns that stop at this stop
            Collection<TripPattern> tps = tx.getTripPatternsByStop(source.id);

            List<TripPattern> tpToSave = new ArrayList<TripPattern>();

            // update them
            for (TripPattern tp : tps) {
                tp = tp.clone();
                for (TripPatternStop ps : tp.patternStops) {
                    if (source.id.equals(ps.stopId)) {
                        ps.stopId = target.id;
                    }
                }

                // batch them for save at the end, as all of the sets we are working with still refer to the db,
                // so changing it midstream is a bad idea
                tpToSave.add(tp);

                // update the trips
                List<Trip> tripsToSave = new ArrayList<Trip>();
                for (Trip trip : tx.getTripsByPattern(tp.id)) {
                    trip = trip.clone();

                    for (StopTime st : trip.stopTimes) {
                        if (source.id.equals(st.stopId)) {
                            // stop times have been cloned, so this is safe
                            st.stopId = target.id;
                        }
                    }

                    tripsToSave.add(trip);
                }

                for (Trip trip : tripsToSave) {
                    tx.trips.put(trip.id, trip);
                }
            }

            for (TripPattern tp : tpToSave) {
                tx.tripPatterns.put(tp.id, tp);
            }

            if (!tx.getTripPatternsByStop(source.id).isEmpty()) {
                throw new IllegalStateException("Tried to move all trip patterns when merging stops but was not successful");
            }

            tx.stops.remove(source.id);
        }
    }

	@JsonIgnore
	public String getGtfsId() {
		if(gtfsStopId != null && !gtfsStopId.isEmpty())
			return gtfsStopId;
		else
			return "STOP_" + id;
	}
}
