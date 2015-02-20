package controllers.api;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.mapdb.Fun;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import models.VersionedDataStore;
import models.VersionedDataStore.AgencyTx;
import models.transit.Agency;
import models.transit.ScheduleException;
import models.transit.ServiceCalendar;
import models.transit.ServiceCalendar.ServiceCalendarForPattern;
import models.transit.Trip;
import models.transit.TripPattern;
import controllers.Api;
import play.mvc.Controller;

public class CalendarController extends Controller {

    public static void getCalendar(String id, String agencyId, final String patternId) {
    	if (agencyId == null) {
    		agencyId = session.get("agencyId");
    	}
    	
    	if (agencyId == null) {
    		badRequest();
    		return;
    	}
    	
    	final AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
    	
    	try {
    		if (id != null) {
    			if (!tx.calendars.containsKey(id)) {
    				notFound();
    				tx.rollback();
    				return;
    			}
    			
    			else {
    				ServiceCalendar c = tx.calendars.get(id);
    				c.addDerivedInfo(tx);
    				renderJSON(Api.toJson(c, false));
    			}
    		}
    		else if (patternId != null) {
    			if (!tx.tripPatterns.containsKey(patternId)) {
    				tx.rollback();
    				notFound();
    				return;
    			}
    			
    			Set<String> serviceCalendarIds = Sets.newHashSet();
    			for (Trip trip : tx.getTripsByPattern(patternId)) {
    				serviceCalendarIds.add(trip.calendarId);
    			}
    			
    			Collection<ServiceCalendarForPattern> ret = 
    					Collections2.transform(serviceCalendarIds, new Function<String, ServiceCalendarForPattern> () {

							@Override
							public ServiceCalendarForPattern apply(String input) {
								ServiceCalendar cal = tx.calendars.get(input);
								
								Long count = tx.tripCountByPatternAndCalendar.get(new Fun.Tuple2(patternId, cal.id));
								
								if (count == null) count = 0L;
								
								return new ServiceCalendarForPattern(cal, tx.tripPatterns.get(patternId), count);
							}
    						
    					});
    			
    			renderJSON(Api.toJson(ret, false));
    		}
    		else {
    			Collection<ServiceCalendar> cals = tx.calendars.values();
    			for (ServiceCalendar c : cals) {
    				c.addDerivedInfo(tx);
    			}
    			renderJSON(Api.toJson(cals, false));
    		}
    		
    		tx.rollback();
        } catch (Exception e) {
        	tx.rollback();
            e.printStackTrace();
            badRequest();
        }

    }

    public static void createCalendar() {
    	ServiceCalendar cal;
    	AgencyTx tx = null;

        try {
            cal = Api.mapper.readValue(params.get("body"), ServiceCalendar.class);

            if (!VersionedDataStore.agencyExists(cal.agencyId)) {
                badRequest();
                return;
            }

            tx = VersionedDataStore.getAgencyTx(cal.agencyId);
            
            if (tx.calendars.containsKey(cal.id)) {
            	tx.rollback();
            	badRequest();
            	return;
            }
            
            // check if gtfsServiceId is specified, if not create from DB id
            if(cal.gtfsServiceId == null) {
            	cal.gtfsServiceId = "CAL_" + cal.id.toString();
            }
            
            cal.addDerivedInfo(tx);
            
            tx.calendars.put(cal.id, cal);
            tx.commit();

            renderJSON(Api.toJson(cal, false));
        } catch (Exception e) {
        	if (tx != null) tx.rollback();
            e.printStackTrace();
            badRequest();
        }
    }

    public static void updateCalendar() {
    	ServiceCalendar cal;
    	AgencyTx tx = null;

        try {
            cal = Api.mapper.readValue(params.get("body"), ServiceCalendar.class);

            if (!VersionedDataStore.agencyExists(cal.agencyId)) {
                badRequest();
                return;
            }

            tx = VersionedDataStore.getAgencyTx(cal.agencyId);
            
            if (!tx.calendars.containsKey(cal.id)) {
            	tx.rollback();
            	badRequest();
            	return;
            }
            
            // check if gtfsServiceId is specified, if not create from DB id
            if(cal.gtfsServiceId == null) {
            	cal.gtfsServiceId = "CAL_" + cal.id.toString();
            }
            
            cal.addDerivedInfo(tx);
            
            tx.calendars.put(cal.id, cal);
            tx.commit();

            renderJSON(Api.toJson(cal, false));
        } catch (Exception e) {
        	if (tx != null) tx.rollback();
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteCalendar(String id, String agencyId) {
    	AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
    	
    	if (id == null || !tx.calendars.containsKey(id)) {
    		tx.rollback();
    		notFound();
    		return;
    	}
    	
    	// we just don't let you delete calendars unless there are no trips on them
    	Long count = tx.tripCountByCalendar.get(id);
    	if (count != null && count > 0) {
    		tx.rollback();
    		badRequest();
    		return;
    	}
    	
    	// drop this calendar from any schedule exceptions
    	for (ScheduleException ex : tx.getExceptionsByCalendar(id)) {
    		ex.customSchedule.remove(id);
    		tx.exceptions.put(ex.id, ex);
    	}
    	
    	tx.calendars.remove(id);
    	
    	tx.commit();
    	
    	ok();
    }
}
