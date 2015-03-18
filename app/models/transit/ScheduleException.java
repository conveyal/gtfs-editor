package models.transit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Model;

import org.joda.time.LocalDate;

/**
 * Represents an exception to the schedule, which could be "On January 18th, run a Sunday schedule"
 * (useful for holidays), or could be "on June 23rd, run the following services" (useful for things
 * like early subway shutdowns, re-routes, etc.)
 * 
 * Unlike the GTFS schedule exception model, we assume that these special calendars are all-or-nothing;
 * everything that isn't explicitly running is not running. That is, creating special service means the
 * user starts with a blank slate.
 *  
 * @author mattwigway
 */

public class ScheduleException extends Model implements Cloneable, Serializable {
	public static final long serialVersionUID = 1;
	
	/** The agency whose service this schedule exception describes */
	public String agencyId;
	
	/**
	 * If non-null, run service that would ordinarily run on this day of the week.
	 * Takes precedence over any custom schedule.
	 */
	public ExemplarServiceDescriptor exemplar;
	
	/** The name of this exception, for instance "Presidents' Day" or "Early Subway Shutdowns" */
	public String name;
	
	/** The dates of this service exception */
	public List<LocalDate> dates;
	
	/** A custom schedule. Only used if like == null */
	public List<String> customSchedule;
	
	public boolean serviceRunsOn(ServiceCalendar service) {
		switch (exemplar) {
		case MONDAY:
			return service.monday;
		case TUESDAY:
			return service.tuesday;
		case WEDNESDAY:
			return service.wednesday;
		case THURSDAY:
			return service.thursday;
		case FRIDAY:
			return service.friday;
		case SATURDAY:
			return service.saturday;
		case SUNDAY:
			return service.sunday;
		case NO_SERVICE:
			// special case for quickly turning off all service.
			return false;
		case CUSTOM:
			return customSchedule.contains(service.id);
		default:
			// can't actually happen, but java requires a default with a return here
			return false;
		}
	}
	
	/**
	 * Represents a desire about what service should be like on a particular day.
	 * For example, run Sunday service on Presidents' Day, or no service on New Year's Day.
	 */
	public static enum ExemplarServiceDescriptor {
		MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, NO_SERVICE, CUSTOM;
	}
	
	public ScheduleException clone () throws CloneNotSupportedException {
		ScheduleException c = (ScheduleException) super.clone();
		c.dates = new ArrayList<LocalDate>(this.dates);
		c.customSchedule = new ArrayList<String>(this.customSchedule);
		return c;
	}
}
