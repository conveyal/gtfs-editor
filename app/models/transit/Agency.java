package models.transit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import play.Logger;
import play.db.jpa.Model;

@JsonIgnoreProperties({"entityId", "persistent"})
@Entity
public class Agency extends Model {
	
	public String gtfsAgencyId;
    public String name;
    public String url;
    public String timezone;
    public String lang;
    public String phone;
    
    public String color;
    
    public Boolean systemMap;

    public Double defaultLat;
    public Double defaultLon;
    
    @ManyToOne
    public RouteType defaultRouteType;

    @JsonCreator
    public static Agency factory(long id) {
      return Agency.findById(id);
    }

    @JsonCreator
    public static Agency factory(String id) {
      return Agency.findById(Long.parseLong(id));
    }
    
    public Agency(org.onebusaway.gtfs.model.Agency agency) {
        this.gtfsAgencyId = agency.getId();
        this.name = agency.getName();
        this.url = agency.getUrl();
        this.timezone = agency.getTimezone();
        this.lang = agency.getLang();
        this.phone = agency.getPhone();
    }
    
    public Agency(String gtfsAgencyId, String name, String url, String timezone, String lang, String phone) {
        this.gtfsAgencyId = gtfsAgencyId;
        this.name = name;
        this.url = url;
        this.timezone = timezone;
        this.lang = lang;
        this.phone = phone;
    }

	public com.conveyal.gtfs.model.Agency toGtfs() {
		com.conveyal.gtfs.model.Agency ret = new com.conveyal.gtfs.model.Agency();
		
		String gtfsAgencyId = id.toString();
		if(this.gtfsAgencyId != null && !this.gtfsAgencyId.isEmpty())
			gtfsAgencyId = this.gtfsAgencyId;
		
		ret.agency_id = gtfsAgencyId;
		ret.agency_name = name;
		try {
			ret.agency_url = new URL(url);
		} catch (MalformedURLException e) {
			Logger.warn("Unable to coerce {} to URL", url);
			ret.agency_url = null;
		}
		ret.agency_timezone = timezone;
		ret.agency_lang = lang;
		ret.agency_phone = phone;
		
		return ret;
	}
}
