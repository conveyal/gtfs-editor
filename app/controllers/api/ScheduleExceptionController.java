package controllers.api;

import org.joda.time.LocalDate;

import models.transit.ScheduleException;
import controllers.Base;
import controllers.Application;
import controllers.Secure;
import controllers.Security;
import datastore.VersionedDataStore;
import datastore.AgencyTx;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class ScheduleExceptionController extends Controller {
	@Before
	static void initSession() throws Throwable {
		 
		if(!Security.isConnected() && !Application.checkOAuth(request, session))
			Secure.login();
	}
	
    /** Get all of the schedule exceptions for an agency */
    public static void getScheduleException (String exceptionId, String agencyId) {
    	if (agencyId == null)
    		agencyId = session.get("agencyId");
    	
    	if (agencyId == null) {
    		badRequest();
    		return;
    	}
    	
    	AgencyTx tx = null;
    	
    	try {
    		tx = VersionedDataStore.getAgencyTx(agencyId);
    		
    		if (exceptionId != null) {
    			if (!tx.exceptions.containsKey(exceptionId))
    				badRequest();
    			else
    				renderJSON(Base.toJson(tx.exceptions.get(exceptionId), false));
    		}
    		else {
    			renderJSON(Base.toJson(tx.exceptions.values(), false));
    		}
    		tx.rollback();
    	} catch (Exception e) {
    		if (tx != null) tx.rollback();
    		e.printStackTrace();
    		badRequest();
    	}
    }
    
    public static void createScheduleException () {
    	AgencyTx tx = null;
    	try {
			ScheduleException ex = Base.mapper.readValue(params.get("body"), ScheduleException.class);
			
			if (!VersionedDataStore.agencyExists(ex.agencyId)) {
				badRequest();
				return;
			}
			
            if (session.contains("agencyId") && !session.get("agencyId").equals(ex.agencyId))
            	badRequest();
			
			tx = VersionedDataStore.getAgencyTx(ex.agencyId);
			
			if (ex.customSchedule != null) {
				for (String cal : ex.customSchedule) {
					if (!tx.calendars.containsKey(cal)) {
						tx.rollback();
						badRequest();
						return;
					}
				}
			}
			
			if (tx.exceptions.containsKey(ex.id)) {
				tx.rollback();
				badRequest();
				return;
			}
			
			for (LocalDate date : ex.dates) {
				if (tx.scheduleExceptionCountByDate.containsKey(date) && tx.scheduleExceptionCountByDate.get(date) > 0) {
					tx.rollback();
					badRequest();
					return;
				}
			}
			
			tx.exceptions.put(ex.id, ex);
			
			tx.commit();
			
			renderJSON(Base.toJson(ex, false));
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			e.printStackTrace();
			badRequest();
		}    	
    }
    
    public static void updateScheduleException () {
    	AgencyTx tx = null;
    	try {
			ScheduleException ex = Base.mapper.readValue(params.get("body"), ScheduleException.class);
			
            if (session.contains("agencyId") && !session.get("agencyId").equals(ex.agencyId))
            	badRequest();
			
			if (!VersionedDataStore.agencyExists(ex.agencyId)) {
				badRequest();
				return;
			}
			
			tx = VersionedDataStore.getAgencyTx(ex.agencyId);
			
			if (ex.customSchedule != null) {
				for (String cal : ex.customSchedule) {
					if (!tx.calendars.containsKey(cal)) {
						tx.rollback();
						badRequest();
						return;
					}
				}
			}
			
			if (!tx.exceptions.containsKey(ex.id)) {
				tx.rollback();
				badRequest();
				return;
			}
			
			tx.exceptions.put(ex.id, ex);
			
			tx.commit();
			
			renderJSON(Base.toJson(ex, false));
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			e.printStackTrace();
			badRequest();
		}   
    }
    
    public static void deleteScheduleException (String id, String agencyId) {
    	if (agencyId == null)
    		agencyId = session.get("agencyId");
    	
    	if (agencyId == null) {
    		badRequest();
    		return;
    	}
    	
    	AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
    	tx.exceptions.remove(id);
    	tx.commit();
    	
    	ok();
    }
}
