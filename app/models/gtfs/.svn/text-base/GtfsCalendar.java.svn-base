package models.gtfs;

import java.util.*;


import org.onebusaway.gtfs.model.ServiceCalendar;

 
public class GtfsCalendar {
 
/*    
 
service_id	
Required. The service_id contains an ID that uniquely identifies a set of dates when service is available for one or more routes. Each service_id value can appear at most once in a calendar.txt file. This value is dataset unique. It is referenced by the trips.txt file.

monday	
Required. The monday field contains a binary value that indicates whether the service is valid for all Mondays.

A value of 1 indicates that service is available for all Mondays in the date range. (The date range is specified using the start_date and end_date fields.)
A value of 0 indicates that service is not available on Mondays in the date range.
Note: You may list exceptions for particular dates, such as holidays, in the calendar_dates.txt file.
tuesday	
Required. The tuesday field contains a binary value that indicates whether the service is valid for all Tuesdays.

A value of 1 indicates that service is available for all Tuesdays in the date range. (The date range is specified using the start_date and end_date fields.)
A value of 0 indicates that service is not available on Tuesdays in the date range.
Note: You may list exceptions for particular dates, such as holidays, in the calendar_dates.txt file.
wednesday	
Required. The wednesday field contains a binary value that indicates whether the service is valid for all Wednesdays.

A value of 1 indicates that service is available for all Wednesdays in the date range. (The date range is specified using the start_date and end_date fields.)
A value of 0 indicates that service is not available on Wednesdays in the date range.
Note: You may list exceptions for particular dates, such as holidays, in the calendar_dates.txt file.
thursday	
Required. The thursday field contains a binary value that indicates whether the service is valid for all Thursdays.

A value of 1 indicates that service is available for all Thursdays in the date range. (The date range is specified using the start_date and end_date fields.)
A value of 0 indicates that service is not available on Thursdays in the date range.
Note: You may list exceptions for particular dates, such as holidays, in the calendar_dates.txt file.
friday	
Required. The friday field contains a binary value that indicates whether the service is valid for all Fridays.

A value of 1 indicates that service is available for all Fridays in the date range. (The date range is specified using the start_date and end_date fields.)
A value of 0 indicates that service is not available on Fridays in the date range.
Note: You may list exceptions for particular dates, such as holidays, in the calendar_dates.txt file
saturday	
Required. The saturday field contains a binary value that indicates whether the service is valid for all Saturdays.

A value of 1 indicates that service is available for all Saturdays in the date range. (The date range is specified using the start_date and end_date fields.)
A value of 0 indicates that service is not available on Saturdays in the date range.
Note: You may list exceptions for particular dates, such as holidays, in the calendar_dates.txt file.
sunday	
Required. The sunday field contains a binary value that indicates whether the service is valid for all Sundays.

A value of 1 indicates that service is available for all Sundays in the date range. (The date range is specified using the start_date and end_date fields.)
A value of 0 indicates that service is not available on Sundays in the date range.
Note: You may list exceptions for particular dates, such as holidays, in the calendar_dates.txt file.
start_date	
Required. The start_date field contains the start date for the service.

The start_date field's value should be in YYYYMMDD format.
end_date	
Required. The end_date field contains the end date for the service. This date is included in the service interval.

The end_date field's value should be in YYYYMMDD format.
*/
	
	
    public String serviceId;
    public Integer monday;
    public Integer tuesday;
    public Integer wednesday;
    public Integer thursday;
    public Integer friday;
    public Integer saturday;
    public Integer sunday;
    public Date startDate;
    public Date endDate;
    
    
    public GtfsCalendar(ServiceCalendar calendar) {
    	this.serviceId = calendar.getServiceId().toString();
    	this.monday = calendar.getMonday();
    	this.tuesday = calendar.getTuesday();
    	this.wednesday = calendar.getWednesday();
    	this.thursday = calendar.getThursday();
    	this.friday = calendar.getFriday();
    	this.saturday = calendar.getSaturday();
    	this.sunday = calendar.getSunday();
    	this.startDate = calendar.getStartDate().getAsDate();
    	this.endDate = calendar.getEndDate().getAsDate();
    } 
    
    
}