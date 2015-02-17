package models.transit;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import models.Model;

import org.hibernate.annotations.Type;

public class RouteType extends Model implements Serializable {
	public static final long serialVersionUID = 1;
	
    public String localizedVehicleType;
	public String description;
	
    public GtfsRouteType gtfsRouteType;
    
    public HvtRouteType hvtRouteType;
    
    /*
    @JsonCreator
    public static RouteType factory(long id) {
      return RouteType.findById(id);
    }

    @JsonCreator
    public static RouteType factory(String id) {
      return RouteType.findById(Long.parseLong(id));
    }
    */

    
}
