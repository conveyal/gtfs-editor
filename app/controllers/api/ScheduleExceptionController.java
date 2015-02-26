package controllers.api;

import java.util.List;

import models.transit.Agency;
import models.transit.ScheduleException;
import controllers.Api;
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
    				renderJSON(Api.toJson(tx.exceptions.get(exceptionId), false));
    		}
    		else {
    			renderJSON(Api.toJson(tx.exceptions.values(), false));
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
			ScheduleException ex = Api.mapper.readValue(params.get("body"), ScheduleException.class);
			
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
			
			if (tx.exceptions.containsKey(ex.id)) {
				tx.rollback();
				badRequest();
				return;
			}
			
			tx.exceptions.put(ex.id, ex);
			
			tx.commit();
			
			renderJSON(Api.toJson(ex, false));			
		} catch (Exception e) {
			if (tx != null) tx.rollback();
			e.printStackTrace();
			badRequest();
		}    	
    }
    
    public static void updateScheduleException () {
    	AgencyTx tx = null;
    	try {
			ScheduleException ex = Api.mapper.readValue(params.get("body"), ScheduleException.class);
			
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
			
			renderJSON(Api.toJson(ex, false));			
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
