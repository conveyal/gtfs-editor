package models.transit;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import models.Model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import play.Logger;

public class Agency extends Model implements Serializable {
	public static final long serialVersionUID = 1;
	
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
    
    public String routeTypeId;

    /*
    @JsonCreator
    public static Agency factory(long id) {
      return Agency.findById(id);
    }

    @JsonCreator
    public static Agency factory(String id) {
      return Agency.findById(Long.parseLong(id));
    }
    */
    
    public Agency(com.conveyal.gtfs.model.Agency agency) {
        this.gtfsAgencyId = agency.agency_id;
        this.name = agency.agency_name;
        this.url = agency.agency_url != null ? agency.agency_url.toString() : null;
        this.timezone = agency.agency_timezone;
        this.lang = agency.agency_lang;
        this.phone = agency.agency_phone;
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
