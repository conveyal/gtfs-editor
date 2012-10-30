package models.transit;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Entity;
import javax.persistence.Query;

import models.gis.GisRoute;
import models.gis.GisUpload;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.MultiLineString;

import play.db.jpa.Model;

@JsonIgnoreProperties({"entityId", "systemMap", "persistent"})
@Entity
public class Route extends Model {
	
	public String gtfsRouteId;
    public String routeShortName;
    public String routeLongName;
    public String routeDesc;
    
    @Enumerated(EnumType.STRING)
    public RouteType routeType;
    
    public String routeUrl;
    public String routeColor;
    public String routeTextColor;
   
    public Boolean weekday;
    public Boolean saturday;
    public Boolean sunday;
    
    @ManyToOne
    public Agency agency;
    
    @ManyToOne
    public GisRoute gisRoute;
    
    @ManyToOne
    public GisUpload gisUpload;
    
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
    
    public static RouteType mapGtfsRouteType(Integer routeType)
    {
    	switch(routeType)
    	{
    		case 0:
    			return RouteType.TRAM;
    		case 1:
    			return RouteType.SUBWAY;
    		case 2:
    			return RouteType.RAIL;
    		case 3:
    			return RouteType.BUS;
    		case 4:
    			return RouteType.FERRY;
    		case 5:
    			return RouteType.CABLECAR;
    		case 6:
    			return RouteType.GONDOLA;
    		case 7:
    			return RouteType.FUNICULAR;
    		default:
    			return null;
    			
    	}
    }
    
    public static Integer mapGtfsRouteType(RouteType routeType)
    {
    	switch(routeType)
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
    	
        em.createNativeQuery("INSERT INTO route (id, routecolor, routedesc, gtfsrouteid, routelongname, routeshortname, routetextcolor, routetype, routeurl, agency_id)" +
        	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
          .setParameter(1,  nextId)
          .setParameter(2,  gtfsRoute.getColor())
          .setParameter(3,  gtfsRoute.getDesc())
          .setParameter(4,  gtfsRoute.getId().toString())
          .setParameter(5,  gtfsRoute.getLongName())
          .setParameter(6,  gtfsRoute.getShortName())
          .setParameter(7,  gtfsRoute.getTextColor())
          .setParameter(8,  mapGtfsRouteType(gtfsRoute.getType()).name())
          .setParameter(9,  gtfsRoute.getUrl())
          .setParameter(10, agencyId)
          .executeUpdate();
        
        return nextId;
    }

}
