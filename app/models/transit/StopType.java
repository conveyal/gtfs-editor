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
public class StopType extends Model {
	
    public String stopType;
	public String description;

	public Boolean interpolated;
	public Boolean majorStop;
	
    
    @JsonCreator
    public static StopType factory(long id) {
      return StopType.findById(id);
    }

    @JsonCreator
    public static StopType factory(String id) {
      return StopType.findById(Long.parseLong(id));
    }

    
}
