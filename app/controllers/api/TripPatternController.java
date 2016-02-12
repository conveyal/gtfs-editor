package controllers.api;

import static java.util.Collections.sort;

import java.util.Collection;
import java.util.Set;

import models.transit.Trip;
import models.transit.TripPattern;

import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

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
public class TripPatternController extends Controller {
	@Before
	static void initSession() throws Throwable {
		 
		if(!Security.isConnected() && !Application.checkOAuth(request, session))
			Secure.login("");
	}
	
    public static void getTripPattern(String id, String routeId, String agencyId) {
    	if (agencyId == null)
    		agencyId = session.get("agencyId");
    	
    	if (agencyId == null) {
    		badRequest();
    		return;
    	}
    	
    	final AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
    	
        try {
        	
            if(id != null) {
               if (!tx.tripPatterns.containsKey(id))
            	   notFound();
               else            	   
            	   renderJSON(Base.toJson(tx.tripPatterns.get(id), false));
            }
            else if (routeId != null) {
            	
            	if (!tx.routes.containsKey(routeId))
            		notFound();
            	else {
            		Set<Tuple2<String, String>> tpKeys = tx.tripPatternsByRoute.subSet(new Tuple2(routeId, null), new Tuple2(routeId, Fun.HI));
	            	
	            	Collection<TripPattern> patts = Collections2.transform(tpKeys, new Function<Tuple2<String, String>, TripPattern> () {
	
						@Override
						public TripPattern apply(Tuple2<String, String> input) {
							return tx.tripPatterns.get(input.b);
						}
	            	});
	            	
	            	renderJSON(Base.toJson(patts, false));
            	}
            }
            else {
            	badRequest();
            }
            
            tx.rollback();
            
        } catch (Exception e) {
        	tx.rollback();
            e.printStackTrace();
            badRequest();
        }
    }

    public static void createTripPattern() {
        TripPattern tripPattern;
        
        try {
            tripPattern = Base.mapper.readValue(params.get("body"), TripPattern.class);
            
            if (session.contains("agencyId") && !session.get("agencyId").equals(tripPattern.agencyId))
            	badRequest();
            
            if (!VersionedDataStore.agencyExists(tripPattern.agencyId)) {
            	badRequest();
            	return;
            }
            
            AgencyTx tx = VersionedDataStore.getAgencyTx(tripPattern.agencyId);
            
            if (tx.tripPatterns.containsKey(tripPattern.id)) {
            	tx.rollback();
            	badRequest();
            }
            
            tripPattern.calcShapeDistTraveled();
            
            tx.tripPatterns.put(tripPattern.id, tripPattern);
            tx.commit();

            renderJSON(Base.toJson(tripPattern, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void updateTripPattern() {
        TripPattern tripPattern;
        AgencyTx tx = null;
        try {
            tripPattern = Base.mapper.readValue(params.get("body"), TripPattern.class);
            
            if (session.contains("agencyId") && !session.get("agencyId").equals(tripPattern.agencyId))
            	badRequest();
            
            if (!VersionedDataStore.agencyExists(tripPattern.agencyId)) {
            	badRequest();
            	return;
            }
            
            if (tripPattern.id == null) {
                badRequest();
                return;
            }
            
            tx = VersionedDataStore.getAgencyTx(tripPattern.agencyId);
            
            TripPattern originalTripPattern = tx.tripPatterns.get(tripPattern.id);
            
            if(originalTripPattern == null) {
            	tx.rollback();
                badRequest();
                return;
            }
                        
            // update stop times
            try {
            	TripPattern.reconcilePatternStops(originalTripPattern, tripPattern, tx);
            } catch (IllegalStateException e) {
            	tx.rollback();
            	badRequest();
            	return;
            }
            
            tripPattern.calcShapeDistTraveled();
            
            tx.tripPatterns.put(tripPattern.id, tripPattern);
            tx.commit();

            renderJSON(Base.toJson(tripPattern, false));
        } catch (Exception e) {
        	if (tx != null) tx.rollback();
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteTripPattern(String id, String agencyId) {
    	if (agencyId == null)
    		agencyId = session.get("agencyId");
    	
        if(id == null || agencyId == null) {
            badRequest();
            return;
        }

        AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);

	    try {
	        // first zap all trips on this trip pattern
	        for (Trip trip : tx.getTripsByPattern(id)) {
	        	tx.trips.remove(trip.id);
	        }
	        
	        tx.tripPatterns.remove(id);
	        tx.commit();
    	} finally {
    		tx.rollbackIfOpen();
    	}
    }
}
