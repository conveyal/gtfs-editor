package models.gtfs;
 
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.onebusaway.gtfs.model.Agency;

import play.db.jpa.Model;
 
@JsonIgnoreProperties({"entityId", "persistent"})
@Entity

public class GtfsAgency extends Model {
 
    public String agencyId;
    public String name;
    public String url;
    public String timezone;
    public String lang;
    public String phone;
 
    @ManyToOne
    public GtfsSnapshot snapshot;
    
    public GtfsAgency(org.onebusaway.gtfs.model.Agency agency) {
        this.agencyId = agency.getId();
        this.name = agency.getName();
        this.url = agency.getUrl();
        this.timezone = agency.getTimezone();
        this.lang = agency.getLang();
        this.phone = agency.getPhone();
    }
    
}