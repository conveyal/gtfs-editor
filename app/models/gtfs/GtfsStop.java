package models.gtfs;

import java.util.*;

import org.onebusaway.gtfs.model.Stop;


public class GtfsStop {
 
/*    stop_id	
    Required. The stop_id field contains an ID that uniquely identifies a stop or station. Multiple routes may use the same stop. The stop_id is dataset unique.

    stop_code	
    Optional. The stop_code field contains short text or a number that uniquely identifies the stop for passengers. Stop codes are often used in phone-based transit information systems or printed on stop signage to make it easier for riders to get a stop schedule or real-time arrival information for a particular stop.

    The stop_code field should only be used for stop codes that are displayed to passengers. For internal codes, use stop_id. This field should be left blank for stops without a code.
    
    stop_name	
    Required. The stop_name field contains the name of a stop or station. Please use a name that people will understand in the local and tourist vernacular.

    stop_desc	
    Optional. The stop_desc field contains a description of a stop. Please provide useful, quality information. Do not simply duplicate the name of the stop.

    stop_lat	
    Required. The stop_lat field contains the latitude of a stop or station. The field value must be a valid WGS 84 latitude.

    stop_lon	
    Required. The stop_lon field contains the longitude of a stop or station. The field value must be a valid WGS 84 longitude value from -180 to 180.

    zone_id	
    Optional. The zone_id field defines the fare zone for a stop ID. Zone IDs are required if you want to provide fare information using fare_rules.txt. If this stop ID represents a station, the zone ID is ignored.

    stop_url	
    Optional. The stop_url field contains the URL of a web page about a particular stop. This should be different from the agency_url and the route_url fields. 

    The value must be a fully qualified URL that includes http:// or https://, and any special characters in the URL must be correctly escaped. See http://www.w3.org/Addressing/URL/4_URI_Recommentations.html for a description of how to create fully qualified URL values.

    location_type	
    Optional. The location_type field identifies whether this stop ID represents a stop or station. If no location type is specified, or the location_type is blank, stop IDs are treated as stops. Stations may have different properties from stops when they are represented on a map or used in trip planning.

    The location type field can have the following values:
    0 or blank - Stop. A location where passengers board or disembark from a transit vehicle.
    1 - Station. A physical structure or area that contains one or more stop.
    
    parent_station
    Optional. For stops that are physically located inside stations, the parent_station field identifies the station associated with the stop. To use this field, stops.txt must also contain a row where this stop ID is assigned location type=1.
*/

    public String stopId;
    public String stopCode;
    public String stopName;
    public String stopDesc;
    public String zoneId;
    public String stopUrl;
    public Integer locationType;
    public String parentStation;
    public Double stopLat;
    public Double stopLon;
    public Integer wheelchairBoarding;
 
    
    public GtfsStop(Stop stop, GtfsSnapshot gtfsSnapshot) {
        this.stopId = stop.getId().toString();
        this.stopCode = stop.getCode();
        this.stopName = stop.getName();
        this.stopDesc = stop.getDesc();
        this.zoneId = stop.getZoneId();
        this.stopUrl = stop.getUrl();
        this.locationType = stop.getLocationType();
        this.parentStation = stop.getParentStation();
        this.stopLat = stop.getLat();
        this.stopLon = stop.getLon();
        this.wheelchairBoarding = stop.getWheelchairBoarding();
    } 
    
    
    public GtfsStop(String stopId, String stopCode, String stopName, String stopDesc, String zoneId, String stopUrl, Integer locationType, String parentStation, Double stopLat, Double stopLon, Integer wheelchairBoarding, GtfsSnapshot gtfsSnapshot) {
        this.stopId = stopId;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.stopDesc = stopDesc;
        this.zoneId = zoneId;
        this.stopUrl = stopUrl;
        this.locationType = locationType;
        this.parentStation = parentStation;
        this.stopLat = stopLat;
        this.stopLon = stopLon;
        this.wheelchairBoarding = wheelchairBoarding;
    } 
    

}