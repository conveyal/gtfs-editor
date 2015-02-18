package controllers.api;

import java.util.List;

import models.VersionedDataStore;
import models.VersionedDataStore.AgencyTx;
import models.transit.Agency;
import models.transit.Route;
import models.transit.ServiceCalendar;
import models.transit.StopTime;
import models.transit.Trip;
import models.transit.TripPattern;
import controllers.Api;
import play.mvc.Controller;

public class TripController extends Controller {
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
        } catch (Exception e) {
            e.printStackTrace();
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
        	
        	tx.trips.put(trip.id, trip);
        	tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteTrip(String id, String agencyId) {
    	if (agencyId == null)
    		agencyId = session.get(agencyId);
    	
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
