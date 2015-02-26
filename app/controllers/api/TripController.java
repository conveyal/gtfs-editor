package controllers.api;

import java.util.List;

import models.transit.Agency;
import models.transit.Route;
import models.transit.ServiceCalendar;
import models.transit.StopTime;
import models.transit.Trip;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import controllers.Api;
import controllers.Application;
import controllers.Secure;
import controllers.Security;
import datastore.VersionedDataStore;
import datastore.AgencyTx;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class TripController extends Controller {
	@Before
	static void initSession() throws Throwable {
		 
		if(!Security.isConnected() && !Application.checkOAuth(request, session))
			Secure.login();
	}
	
    public static void getTrip(String id, String patternId, String calendarId, String agencyId) {
    	if (agencyId == null)
    		agencyId = session.get("agencyId");
    	
    	if (agencyId == null) {
    		badRequest();
    		return;
    	}
    	
    	AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
    	
        try {
            if (id != null) {
            	if (tx.trips.containsKey(id))
            		renderJSON(Api.toJson(tx.trips.get(id), false));
            	else
            		notFound();
            }
            else if (patternId != null && calendarId != null) {
            	if (!tx.tripPatterns.containsKey(patternId) || !tx.calendars.containsKey(calendarId)) {
            		notFound();
            	}
            	else {
            		renderJSON(Api.toJson(tx.getTripsByPatternAndCalendar(patternId, calendarId), false));
            	}
            }

            else if(patternId != null) {
            	renderJSON(Api.toJson(tx.getTripsByPattern(patternId), false));
            }
            else {
            	renderJSON(Api.toJson(tx.trips.values(), false));
            }
                
        } catch (Exception e) {
        	tx.rollback();
            e.printStackTrace();
            badRequest();
        }

    }
    
    public static void createTrip() {
    	AgencyTx tx = null;
    	
        try {
        	Trip trip = Api.mapper.readValue(params.get("body"), Trip.class);
        	
        	if (!VersionedDataStore.agencyExists(trip.agencyId)) {
        		badRequest();
        		return;
        	}
        	
        	tx = VersionedDataStore.getAgencyTx(trip.agencyId);
        	
        	if (tx.trips.containsKey(trip.id)) {
        		tx.rollback();
        		badRequest();
        		return;
        	}
        	
        	if (!tx.tripPatterns.containsKey(trip.patternId) || trip.stopTimes.size() != tx.tripPatterns.get(trip.patternId).patternStops.size()) {
        		tx.rollback();
        		badRequest();
        		return;
        	}
        	
        	tx.trips.put(trip.id, trip);
        	tx.commit();
        	
        	renderJSON(Api.toJson(trip, false));
        } catch (Exception e) {
            e.printStackTrace();
            if (tx != null) tx.rollbackIfOpen();
            badRequest();
        }
    }
    
    public static void updateTrip() {
    	AgencyTx tx = null;
    	
        try {
        	Trip trip = Api.mapper.readValue(params.get("body"), Trip.class);
        	
        	if (!VersionedDataStore.agencyExists(trip.agencyId)) {
        		badRequest();
        		return;
        	}
        	
        	tx = VersionedDataStore.getAgencyTx(trip.agencyId);
        	
        	if (!tx.trips.containsKey(trip.id)) {
        		tx.rollback();
        		badRequest();
        		return;
        	}
        	
        	if (!tx.tripPatterns.containsKey(trip.patternId) || trip.stopTimes.size() != tx.tripPatterns.get(trip.patternId).patternStops.size()) {
        		tx.rollback();
        		badRequest();
        		return;
        	}
        	
        	TripPattern patt = tx.tripPatterns.get(trip.patternId);
        	
        	// confirm that each stop in the trip matches the stop in the pattern
        	
        	for (int i = 0; i < trip.stopTimes.size(); i++) {
        		TripPatternStop ps = patt.patternStops.get(i);
        		StopTime st =  trip.stopTimes.get(i);
        		
        		if (st == null)
        			// skipped stop
        			continue;
        		
        		if (!st.stopId.equals(ps.stopId)) {
        			Logger.error("Mismatch between stop sequence in trip and pattern at position %s, pattern: %s, stop: %s", i, ps.stopId, st.stopId);
        			tx.rollback();
        			badRequest();
        			return;
        		}
        	}
        	
        	tx.trips.put(trip.id, trip);
        	tx.commit();
        	
        	renderJSON(Api.toJson(trip, false));
        } catch (Exception e) {
        	if (tx != null) tx.rollbackIfOpen();
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteTrip(String id, String agencyId) {
    	if (agencyId == null)
    		agencyId = session.get("agencyId");
    	
        if (id == null || agencyId == null) {
            badRequest();
            return;
        }

        AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
        tx.trips.remove(id);
        tx.commit();
        
        ok();
    }
}
