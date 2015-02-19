package models.transit;

import java.io.Serializable;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import models.Model;
import models.transit.RouteType;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.MultiLineString;

import play.Logger;

public class Route extends Model implements Serializable {
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
    
    public Boolean publiclyVisible;

    public Boolean weekday;
    public Boolean saturday;
    public Boolean sunday;

    public String agencyId;

    //public GisRoute gisRoute;

    //public GisUpload gisUpload;
    
    public AttributeAvailabilityType wheelchairBoarding;

    /*@JsonCreator
    public static Route factory(long id) {
      return Route.findById(id);
    }

    @JsonCreator
    public static Route factory(String id) {
      return Route.findById(Long.parseLong(id));
    }*/

    public Route(com.conveyal.gtfs.model.Route route,  Agency agency) {	
        this.gtfsRouteId = route.route_id;
        this.routeShortName = route.route_short_name;
        this.routeLongName = route.route_long_name;
        this.routeDesc = route.route_desc;
        
        //this.routeTypeId = mapGtfsRouteType(route.route_type);
        
        
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

    // slightly tricky as we can't match 1:1 against GTFS and TDM route types -- we'll pick or create a type.
    public static RouteType mapGtfsRouteType(Integer gtfsRouteType)
    {
    	GtfsRouteType type;
    	
    	switch(gtfsRouteType)
    	{
    		case 0:
    			type = GtfsRouteType.TRAM;
    			break;
    		case 1:
    			type = GtfsRouteType.SUBWAY;
    			break;
    		case 2:
    			type = GtfsRouteType.RAIL;
    			break;
    		case 3:
    			type = GtfsRouteType.BUS;
    			break;
    		case 4:
    			type = GtfsRouteType.FERRY;
    			break;
    		case 5:
    			type = GtfsRouteType.CABLECAR;
    			break;
    		case 6:
    			type = GtfsRouteType.GONDOLA;
    			break;
    		case 7:
    			type = GtfsRouteType.FUNICULAR;
    			break;
    		default:
    			type = null;
    			break;
    		
    	}
    	
    	if(type == null)
			return null;
		
    	RouteType routeType = null;//RouteType.find("gtfsRouteType = ?", type).first();
    	
    	if(routeType != null)
    		return routeType;
    	
    	else {
    		
    		routeType = new RouteType();
    		routeType.gtfsRouteType = type;
    		routeType.description = type.name();
    		//routeType.save();
    		
    		return routeType;
    	}    
    }

    public static Integer mapGtfsRouteType(RouteType routeType)
    {
    	switch(routeType.gtfsRouteType)
    	{
    		case TRAM:
    			return 0;
    		case SUBWAY:
    			return 1;
    		case RAIL:
    			return 2;
    		case BUS:
    			return 3;
    		case FERRY:
    			return 4;
    		case CABLECAR:
    			return 5;
    		case GONDOLA:
    			return 6;
    		case FUNICULAR:
    			return 7;
    		default:
    			return null;

    	}
    }
	public com.conveyal.gtfs.model.Route toGtfs(com.conveyal.gtfs.model.Agency a) {
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

}
