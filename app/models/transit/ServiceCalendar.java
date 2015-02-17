package models.transit;


import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;

import com.conveyal.gtfs.model.Calendar;
import com.conveyal.gtfs.model.Service;

import models.Model;

public class ServiceCalendar extends Model implements Serializable {
	public static final long serialVersionUID = 1;
	
    public String agencyId;
	
	public String description;
	
	public String gtfsServiceId;
    public Boolean monday;
    public Boolean tuesday;
    public Boolean wednesday;
    public Boolean thursday;
    public Boolean friday;
    public Boolean saturday;
    public Boolean sunday;
    public LocalDate startDate;
    public LocalDate endDate;
    
    public ServiceCalendar() {};
    
    public ServiceCalendar(Calendar calendar) {
    	this.gtfsServiceId = calendar.service.service_id;
    	this.monday = calendar.monday == 1;
    	this.tuesday = calendar.tuesday == 1;
    	this.wednesday = calendar.wednesday == 1;
    	this.thursday = calendar.thursday == 1;
    	this.friday = calendar.friday == 1;
    	this.saturday = calendar.saturday == 1;
    	this.sunday = calendar.sunday == 1;
    	this.startDate = fromGtfs(calendar.start_date);
    	this.endDate = fromGtfs(calendar.end_date);
    	inferName();
    }
    
    public ServiceCalendar clone () {
    	ServiceCalendar r = new ServiceCalendar();
    	r.agency = agency;
    	r.description = description;
    	r.gtfsServiceId = gtfsServiceId;
    	r.monday = monday;
    	r.tuesday = tuesday;
    	r.wednesday = wednesday;
    	r.thursday = thursday;
    	r.friday = friday;
    	r.saturday = saturday;
    	r.sunday = sunday;
    	r.startDate = startDate;
    	r.endDate = endDate;
  
    	return r;
    }

    // TODO: time zones
	private static LocalDate fromGtfs(int date) {
		int day = date % 100;
		date -= day;
		int month = (date % 10000) / 100;
		date -= month * 100;
		int year = date / 10000;
		
		return new LocalDate(year, month, day);
	}

	// give the UI a little information about the content of this calendar
    /*public long getNumberOfTrips () {
    	return Trip.count("serviceCalendar = ?", this);
    }*/
    
    /*
    public Collection<String> getRoutes () {
    	List<String> ret = new ArrayList<String>();
    	
    	// we're using a JPA native query here because looping over all trips for an agency is too slow
    	Query q = Trip.em().createQuery("SELECT DISTINCT t.route FROM Trip t WHERE serviceCalendar_id = ?");
    	q.setParameter(1, this.id);
    	List<Route> routes = q.getResultList();
    	
    	for (Route route : routes) {
    		String name = route.routeShortName;
    		
    		if (name == null || name.isEmpty())
    			name = route.routeLongName;
    		
    		ret.add(name);
    	}
    	
    	return ret;
    }
    */
    
    // these are computed properties that we can't actually set, but
    // unfortunately this seems to be the only way to explicitly ignore only
    // these properties on deserialization.
    // https://github.com/FasterXML/jackson-databind/issues/95
    /*public void setNumberOfTrips (long trips) {}
    public void setRoutes (Collection<String> routes) {}*/

    /**
     * Infer the name of this calendar 
     */
    public void inferName () {
        StringBuilder sb = new StringBuilder(14);
        
        if (monday)
            sb.append("Mo");
        
        if (tuesday)
            sb.append("Tu");
        
        if (wednesday)
            sb.append("We");
        
        if (thursday)
            sb.append("Th");
        
        if (friday)
            sb.append("Fr");
        
        if (saturday)
            sb.append("Sa");
        
        if (sunday)
            sb.append("Su");
        
        this.description = sb.toString();
        
        if (this.description.equals("") && this.gtfsServiceId != null)
        	this.description = gtfsServiceId;
    }
    
    public String toString() {

    	String str = "";

    	if(this.monday)
    		str += "Mo";

    	if(this.tuesday)
    		str += "Tu";

    	if(this.wednesday)
    		str += "We";

    	if(this.thursday)
    		str += "Th";

    	if(this.friday)
    		str += "Fr";

    	if(this.saturday)
    		str += "Sa";

    	if(this.sunday)
    		str += "Su";

    	return str;
    }

    /**
     * Convert this service to a GTFS service calendar.
     * @param startDate int, in GTFS format: YYYYMMDD
     * @param endDate int, again in GTFS format 
     */
	public Service toGtfs(int startDate, int endDate) {
		Service ret = new Service(id);
		ret.calendar = new Calendar();
		ret.calendar.service = ret;
		ret.calendar.start_date = startDate;
		ret.calendar.end_date = endDate;
		ret.calendar.sunday     = sunday    ? 1 : 0;
		ret.calendar.monday     = monday    ? 1 : 0;
		ret.calendar.tuesday    = tuesday   ? 1 : 0;
		ret.calendar.wednesday  = wednesday ? 1 : 0;
		ret.calendar.thursday   = thursday  ? 1 : 0;
		ret.calendar.friday     = friday    ? 1 : 0;
		ret.calendar.saturday   = saturday  ? 1 : 0;
		
		// TODO: calendar dates
		return ret;
	}
	
	// equals and hashcode use DB ID; they are used to put service calendar dates into a HashMultimap in ProcessGtfsSnapshotExport
	public int hashCode () {
		return id.hashCode();
	}
    
	public boolean equals(Object o) {
		if (o instanceof ServiceCalendar) {
			ServiceCalendar c = (ServiceCalendar) o;
			
			return id.equals(c.id);
		}
		
		return false;
	}
	
	/**
	 * Used to represent a service calendar and its service on a particular route.
	 */
	public static class ServiceCalendarForPattern {
		public String description;
		public String id;
		public long routeTrips;
		
		public ServiceCalendarForPattern(ServiceCalendar cal, TripPattern patt, long routeTrips	) {
			this.description = cal.description;
			this.id = cal.id;
			this.routeTrips = routeTrips;
		}
	}
}
