package models.transit;



import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import com.mysql.jdbc.log.Log;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import org.codehaus.groovy.tools.shell.util.Logger;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Type;

import play.db.jpa.Model;

@JsonIgnoreProperties({"entityId", "persistent"})
@Entity
public class Stop extends Model {

    public String gtfsStopId;
    public String stopCode;
    public String stopName;
    public String stopDesc;
    public String zoneId;
    public String stopUrl;

    public String stopIconUrl;

    @ManyToOne
    public Agency agency;

    @Enumerated(EnumType.STRING)
    public LocationType locationType;

    @Enumerated(EnumType.STRING)
    public AttributeAvailabilityType bikeParking;

    @Enumerated(EnumType.STRING)
    public AttributeAvailabilityType wheelchairBoarding;

    public String parentStation;
    
    // Major stop is a custom field; it has no corralary in the GTFS.
    public Boolean majorStop;

    @JsonCreator
    public static Stop factory(long id) {
      return Stop.findById(id);
    }

    @JsonCreator
    public static Stop factory(String id) {
      return Stop.findById(Long.parseLong(id));
    }
    
    @JsonIgnore
    @Type(type = "org.hibernatespatial.GeometryUserType")
    public Point location;

    @JsonProperty("location")
    public Hashtable getLocation() {
        Hashtable loc = new Hashtable();
        loc.put("lat", this.location.getY());
        loc.put("lng", this.location.getX());
        return loc;
    }
    
    public void merge(Stop mergedStop) {
   
    	// find replace references to mergedStop 
    	StopTime.replaceStop(this, mergedStop);
    	TripPatternStop.replaceStop(this, mergedStop);
    	
    	mergedStop.delete();
    	
    }

    public void setLocation(Hashtable<String, Double> loc) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        this.location = geometryFactory.createPoint(new Coordinate(loc.get("lng"), loc.get("lat")));;
    }
       
    public Point locationPoint() {
   
    	Hashtable<String, Double> loc = this.getLocation();
    	GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(loc.get("lat"), loc.get("lng")));
    }

    public Stop(org.onebusaway.gtfs.model.Stop stop, GeometryFactory geometryFactory) {

        this.gtfsStopId = stop.getId().toString();
        this.stopCode = stop.getCode();
        this.stopName = stop.getName();
        this.stopDesc = stop.getDesc();
        this.zoneId = stop.getZoneId();
        this.stopUrl = stop.getUrl();
        this.locationType = stop.getLocationType() == 1 ? LocationType.STATION : LocationType.STOP;
        this.parentStation = stop.getParentStation();

        this.location  =  geometryFactory.createPoint(new Coordinate(stop.getLat(),stop.getLon()));
    }

    public Stop(Agency agency,String stopName,  String stopCode,  String stopUrl, String stopDesc, Double lat, Double lon) {
        this.agency = agency;
        this.stopCode = stopCode;
        this.stopName = stopName;
        this.stopDesc = stopDesc;
        this.stopUrl = stopUrl;
        this.locationType = LocationType.STOP;

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

        this.location = geometryFactory.createPoint(new Coordinate(lon, lat));;
    }


    public static BigInteger nativeInsert(EntityManager em, org.onebusaway.gtfs.model.Stop gtfsStop, BigInteger agencyId)
    {
        Query idQuery = em.createNativeQuery("SELECT NEXTVAL('hibernate_sequence');");
        BigInteger nextId = (BigInteger)idQuery.getSingleResult();

        em.createNativeQuery("INSERT INTO stop (id, locationtype, parentstation, stopcode, stopdesc, gtfsstopid, stopname, stopurl, zoneid, location, agency_id)" +
            "  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ST_GeomFromText( ? , 4326), ?);")
          .setParameter(1,  nextId)
          .setParameter(2,  gtfsStop.getLocationType() == 1 ? LocationType.STATION.name() : LocationType.STOP.name())
          .setParameter(3,  gtfsStop.getParentStation())
          .setParameter(4,  gtfsStop.getCode())
          .setParameter(5,  gtfsStop.getDesc())
          .setParameter(6,  gtfsStop.getId().toString())
          .setParameter(7,  gtfsStop.getName())
          .setParameter(8,  gtfsStop.getUrl())
          .setParameter(9,  gtfsStop.getZoneId())
          .setParameter(10,  "POINT(" + gtfsStop.getLon() + " " + gtfsStop.getLat() + ")")
          .setParameter(11, agencyId)
          .executeUpdate();

        return nextId;
    }
    
    public static List<List<Stop>> findDuplicateStops(BigInteger agencyId) {
    	
    	// !!! need to autodetect proper SRID for UTM Zone
    	Query q = Stop.em().createNativeQuery("SELECT s1_id, s2_id, dist FROM " + 
    			"(SELECT s1.id as s1_id, s2.id as s2_id, st_distance(transform(s1.location, 32651), transform(s2.location, 32651)) as dist " + 
    			"FROM stop as s1, stop as s2 WHERE s1.agency_id = s2.agency_id and s1.agency_id = ? and s1.id != s2.id) AS calcdist WHERE dist < 15;");
    	
    	q.setParameter(1, agencyId);
    	
    	List<Object[]> pairsResults = q.getResultList();
    	
    	ArrayList<List<Stop>> stopPairs = new ArrayList<List<Stop>>();
    	
    	for(Object[] cols : pairsResults) {
    		Stop s1 =  Stop.findById(((BigInteger)cols[0]).longValue());
    		Stop s2 =  Stop.findById(((BigInteger)cols[1]).longValue());
    		
    		if(s1 != null && s2 != null) {
    			List<Stop> pair = new ArrayList<Stop>();
    			pair.add(s1);
    			pair.add(s2);
    			stopPairs.add(pair);
    		}
    	}
    	
    	return stopPairs;
    }

    public Set<Route> routesServed()
    {
        List<TripPatternStop> stops = TripPatternStop.find("stop = ?", this).fetch();
        HashSet<Route> routes = new HashSet<Route>();

        for(TripPatternStop patternStop : stops)
        {
            if(patternStop.pattern == null)
                continue;

            if(patternStop.pattern.longest != null && patternStop.pattern.longest == true)
                routes.add(patternStop.pattern.route);
        }

        return routes;
    }

    public Set<TripPattern> tripPatternsServed(Boolean weekday, Boolean saturday, Boolean sunday)
    {
        List<TripPatternStop> stops = TripPatternStop.find("stop = ?", this).fetch();
        HashSet<TripPattern> patterns = new HashSet<TripPattern>();

        for(TripPatternStop patternStop : stops)
        {
            if(patternStop.pattern == null)
                continue;

            if(patternStop.pattern.longest == true)
            {
                if((weekday && patternStop.pattern.weekday) || (saturday && patternStop.pattern.saturday) || (sunday && patternStop.pattern.sunday))
                    patterns.add(patternStop.pattern);
            }
        }

        return patterns;
    }
}
