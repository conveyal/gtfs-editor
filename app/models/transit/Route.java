package models.transit;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.persistence.Query;
import javax.persistence.Column;

import models.gis.GisRoute;
import models.gis.GisUpload;
import models.transit.RouteType;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.MultiLineString;

import play.db.jpa.Model;

@JsonIgnoreProperties({"entityId", "persistent"})
@Entity
public class Route extends Model {

	public String gtfsRouteId;
    public String routeShortName;
    public String routeLongName;
 

    @Column(length = 8000,columnDefinition="TEXT")
    public String routeDesc;

    @ManyToOne
    public RouteType routeType;

    public String routeUrl;
    public String routeColor;
    public String routeTextColor;

    // Custom Fields
    public String comments;

    @Enumerated(EnumType.STRING)
    public StatusType status;
    
    public Boolean publiclyVisible;

    public Boolean weekday;
    public Boolean saturday;
    public Boolean sunday;

    @ManyToOne
    public Agency agency;

    @ManyToOne
    public GisRoute gisRoute;

    @ManyToOne
    public GisUpload gisUpload;
    
    @Enumerated(EnumType.STRING)
    public AttributeAvailabilityType wheelchairBoarding;

    @JsonCreator
    public static Route factory(long id) {
      return Route.findById(id);
    }

    @JsonCreator
    public static Route factory(String id) {
      return Route.findById(Long.parseLong(id));
    }

    public Route(org.onebusaway.gtfs.model.Route route,  Agency agency) {	
        this.gtfsRouteId = route.getId().toString();
        this.routeShortName = route.getShortName();
        this.routeLongName = route.getLongName();
        this.routeDesc = route.getDesc();
        
        this.routeType = mapGtfsRouteType(route.getType());
        
        
        this.routeUrl = route.getUrl();
        this.routeColor = route.getColor();

        this.agency = agency;
    }


    public Route(String routeShortName, String routeLongName, RouteType routeType, String routeDescription,  Agency agency) {
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.routeType = routeType;
        this.routeDesc = routeDescription;

        this.agency = agency;
    }
    
    public Route delete() {
    	
    	 List<TripPattern> patterns = TripPattern.find("route = ?", this).fetch();
         for(TripPattern pattern : patterns)
         {
        	 pattern.delete();
         }
    	
    	return super.delete();
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
		
    	RouteType routeType = RouteType.find("gtfsRouteType = ?", type).first();
    	
    	if(routeType != null)
    		return routeType;
    	
    	else {
    		
    		routeType = new RouteType();
    		routeType.gtfsRouteType = type;
    		routeType.description = type.name();
    		routeType.save();
    		
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

    public static BigInteger nativeInsert(EntityManager em, org.onebusaway.gtfs.model.Route gtfsRoute, BigInteger agencyId)
    {
    	Query idQuery = em().createNativeQuery("SELECT NEXTVAL('hibernate_sequence');");
    	BigInteger nextId = (BigInteger)idQuery.getSingleResult();

        em.createNativeQuery("INSERT INTO route (id, routecolor, routedesc, gtfsrouteid, routelongname, routeshortname, routetextcolor, routetype_id, routeurl, agency_id)" +
        	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
          .setParameter(1,  nextId)
          .setParameter(2,  gtfsRoute.getColor())
          .setParameter(3,  gtfsRoute.getDesc())
          .setParameter(4,  gtfsRoute.getId().toString())
          .setParameter(5,  gtfsRoute.getLongName())
          .setParameter(6,  gtfsRoute.getShortName())
          .setParameter(7,  gtfsRoute.getTextColor())
          .setParameter(8,  mapGtfsRouteType(gtfsRoute.getType()).id)
          .setParameter(9,  gtfsRoute.getUrl())
          .setParameter(10, agencyId)
          .executeUpdate();

        return nextId;
    }

}
