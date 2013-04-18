package models.transit;


import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import play.db.jpa.Model;

import models.gtfs.GtfsSnapshot;

@JsonIgnoreProperties({"entityId", "persistent"})
@Entity
public class RouteType extends Model {
	
    public String localizedVehicleType;
	public String description;
	
    @Enumerated(EnumType.STRING)
    public GtfsRouteType gtfsRouteType;
    
    @Enumerated(EnumType.STRING)
    public HvtRouteType hvtRouteType;
    
    @JsonCreator
    public static RouteType factory(long id) {
      return RouteType.findById(id);
    }

    @JsonCreator
    public static RouteType factory(String id) {
      return RouteType.findById(Long.parseLong(id));
    }

    
}
