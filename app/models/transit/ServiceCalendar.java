package models.transit;


import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.Type;

import com.conveyal.gtfs.model.Calendar;
import com.conveyal.gtfs.model.Service;

import play.db.jpa.Model;
import models.gtfs.GtfsSnapshot;

@JsonIgnoreProperties({"entityId", "persistent"})
@Entity
public class ServiceCalendar extends Model {
	
	@ManyToOne
    public Agency agency;
	
	public String description;
	
	public String gtfsServiceId;
    public Boolean monday;
    public Boolean tuesday;
    public Boolean wednesday;
    public Boolean thursday;
    public Boolean friday;
    public Boolean saturday;
    public Boolean sunday;
    public Date startDate;
    public Date endDate;
    
	@JsonCreator
    public static ServiceCalendar factory(long id) {
      return ServiceCalendar.findById(id);
    }

    @JsonCreator
    public static ServiceCalendar factory(String id) {
      return ServiceCalendar.findById(Long.parseLong(id));
    }

    /**
     * Get a description for an OBA GTFS service calendar 
     */
    public static String getNameForGtfsServiceCalendar(org.onebusaway.gtfs.model.ServiceCalendar cal) {
        StringBuilder sb = new StringBuilder(14);
        
        if (cal.getMonday() == 1)
            sb.append("Mo");
        
        if (cal.getTuesday() == 1)
            sb.append("Tu");
        
        if (cal.getWednesday() == 1)
            sb.append("We");
        
        if (cal.getThursday() == 1)
            sb.append("Th");
        
        if (cal.getFriday() == 1)
            sb.append("Fr");
        
        if (cal.getSaturday() == 1)
            sb.append("Sa");
        
        if (cal.getSunday() == 1)
            sb.append("Su");
        
        return sb.toString();
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

    public static BigInteger nativeInsert(EntityManager em, org.onebusaway.gtfs.model.ServiceCalendar gtfsServiceCalendar, BigInteger agencyId)
    {
    	Query idQuery = em.createNativeQuery("SELECT NEXTVAL('hibernate_sequence');");
		BigInteger nextId = (BigInteger)idQuery.getSingleResult();
		
		em.createNativeQuery("INSERT INTO servicecalendar (id, gtfsserviceid, monday, tuesday, wednesday, thursday, friday, saturday, sunday, startdate, enddate, agency_id, description)" +
	    	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
	      .setParameter(1,  nextId)
	      .setParameter(2, gtfsServiceCalendar.getId())
	      .setParameter(3, gtfsServiceCalendar.getMonday() == 1 ? true : false)
	      .setParameter(4, gtfsServiceCalendar.getTuesday() == 1 ? true : false)
	      .setParameter(5, gtfsServiceCalendar.getWednesday() == 1 ? true : false)
	      .setParameter(6, gtfsServiceCalendar.getThursday() == 1 ? true : false)
	      .setParameter(7, gtfsServiceCalendar.getFriday() == 1 ? true : false)
	      .setParameter(8, gtfsServiceCalendar.getSaturday() == 1 ? true : false)
	      .setParameter(9, gtfsServiceCalendar.getSunday() == 1 ? true : false)
	      .setParameter(10, gtfsServiceCalendar.getStartDate().getAsDate())
	      .setParameter(11, gtfsServiceCalendar.getEndDate().getAsDate())
	      .setParameter(12, agencyId)
	      .setParameter(13, getNameForGtfsServiceCalendar(gtfsServiceCalendar))
	      .executeUpdate();
		
		return nextId;
    }

    /**
     * Convert this service to a GTFS service calendar.
     * @param startDate int, in GTFS format: YYYYMMDD
     * @param endDate int, again in GTFS format 
     */
	public Service toGtfs(int startDate, int endDate) {
		Service ret = new Service(getId().toString());
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
    
}
