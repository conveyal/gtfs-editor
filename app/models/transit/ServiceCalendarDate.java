package models.transit;


import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import org.hibernate.annotations.Type;

import play.db.jpa.Model;

import models.gtfs.GtfsSnapshot;

@Entity
public class ServiceCalendarDate extends Model {
	
	@ManyToOne
    public ServiceCalendar calendar;
	
	public String description;
	
	public String gtfsServiceId;
    public Date date;
    
    @Enumerated(EnumType.STRING)	
    public ServiceCalendarDateType exceptionType;
    
    public static BigInteger nativeInsert(EntityManager em, org.onebusaway.gtfs.model.ServiceCalendarDate gtfsServiceCalendarDate)
    {
    	Query idQuery = em.createNativeQuery("SELECT NEXTVAL('hibernate_sequence');");
		BigInteger nextId = (BigInteger)idQuery.getSingleResult();
		
		em.createNativeQuery("INSERT INTO servicecalendardate (id, gtfsserviceid, date, exceptiontype)" +
	    	"  VALUES(?, ?, ?, ?);")
	      .setParameter(1,  nextId)
	      .setParameter(2, gtfsServiceCalendarDate.getId())
	      .setParameter(3, gtfsServiceCalendarDate.getDate().getAsDate())
	      .setParameter(4, gtfsServiceCalendarDate.getExceptionType() == 1 ? ServiceCalendarDateType.ADDED.name() : ServiceCalendarDateType.REMOVED.name())
	      .executeUpdate();
		
		return nextId;
    }
    
    public String toString()
    {
    	SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    	return description + " (" +  df.format(date) + " -- " + exceptionType  + ")";
    }
}
