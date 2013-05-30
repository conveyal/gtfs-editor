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

    public static BigInteger nativeInsert(EntityManager em, org.onebusaway.gtfs.model.ServiceCalendar gtfsServiceCalendar, BigInteger agencyId)
    {
    	Query idQuery = em.createNativeQuery("SELECT NEXTVAL('hibernate_sequence');");
		BigInteger nextId = (BigInteger)idQuery.getSingleResult();
		
		em.createNativeQuery("INSERT INTO servicecalendar (id, gtfsserviceid, monday, tuesday, wednesday, thursday, friday, saturday, sunday, startdate, enddate, agency_id)" +
	    	"  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
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
	      .executeUpdate();
		
		return nextId;
    }
    
}
