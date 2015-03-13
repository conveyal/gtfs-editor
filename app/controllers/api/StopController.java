package controllers.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import models.transit.*;
import org.geotools.referencing.GeodeticCalculator;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import controllers.Base;
import controllers.Application;
import controllers.Secure;
import controllers.Security;
import datastore.VersionedDataStore;
import datastore.AgencyTx;
import play.data.binding.As;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class StopController extends Controller {
	@Before
	static void initSession() throws Throwable {
		 
		if(!Security.isConnected() && !Application.checkOAuth(request, session))
			Secure.login();
	}
	
    public static void getStop(String id, String patternId, String agencyId, Boolean majorStops, Double west, Double east, Double north, Double south) {    	
    	if (agencyId == null)
    		agencyId = session.get("agencyId");
    	
    	if (agencyId == null) {
    		badRequest();
    		return;
    	}
    	
    	final AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
 
    	try {
	      	if (id != null) {
	    		if (!tx.stops.containsKey(id)) {
	    			tx.rollback();
	    			notFound();
	    			return;
	    		}
	    		
	    		String stopJson = Base.toJson(tx.stops.get(id), false);
	    		tx.rollback();
	    		renderJSON(stopJson);
	    	}
	      	else if (Boolean.TRUE.equals(majorStops)) {
	      		// get the major stops for the agency
	      		Collection<Stop> stops = Collections2.transform(tx.majorStops, new Function<String, Stop> () {
					@Override
					public Stop apply(String input) {
						// TODO Auto-generated method stub
						return tx.stops.get(input);
					}	      			
	      		});
	      		
	      		String stopsJson = Base.toJson(stops, false);
	      		tx.rollback();
	      		renderJSON(stopsJson);
	      	}
	    	else if (west != null && east != null && south != null && north != null) {
				Collection<Stop> matchedStops = tx.getStopsWithinBoundingBox(north, east, south, west);
				String stp = Base.toJson(matchedStops, false);
				tx.rollback();
	    		renderJSON(stp);
	    	}
	    	else if (patternId != null) {
	    		if (!tx.tripPatterns.containsKey(patternId)) {
	    			notFound();
	    			return;
	    		}
	    		
	    		TripPattern p = tx.tripPatterns.get(patternId);
	    		
	    		Collection<Stop> ret = Collections2.transform(p.patternStops, new Function<TripPatternStop, Stop> () {
					@Override
					public Stop apply(TripPatternStop input) {
						return tx.stops.get(input.stopId);
					}
	    		});
	    		
	    		String json = Base.toJson(ret, false);
	    		tx.rollback();
	    		renderJSON(json);
	    	}
	    	else {
	    		tx.rollback();
	    		badRequest();
	    	}
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    		badRequest();
    		tx.rollback();
    	}
    	
    }

    public static void createStop() {
    	AgencyTx tx = null;
        try {
            Stop stop = Base.mapper.readValue(params.get("body"), Stop.class);
            
            if (!VersionedDataStore.agencyExists(stop.agencyId)) {
            	badRequest();
            	return;
            }
            
            tx = VersionedDataStore.getAgencyTx(stop.agencyId);
            
            if (tx.stops.containsKey(stop.id)) {
            	badRequest();
            	return;
            }
            
            tx.stops.put(stop.id, stop);
            tx.commit();
            renderJSON(Base.toJson(stop, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        } finally {
        	if (tx != null) tx.rollbackIfOpen();
        }
        	
    }


    public static void updateStop() {
    	AgencyTx tx = null;
        try {
            Stop stop = Base.mapper.readValue(params.get("body"), Stop.class);
            
            if (!VersionedDataStore.agencyExists(stop.agencyId)) {
            	badRequest();
            	return;
            }
            
            tx = VersionedDataStore.getAgencyTx(stop.agencyId);
            
            if (!tx.stops.containsKey(stop.id)) {
            	badRequest();
            	return;
            }
            
            tx.stops.put(stop.id, stop);
            tx.commit();
            renderJSON(Base.toJson(stop, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        } finally {
        	if (tx != null) tx.rollbackIfOpen();
        }
    }

    public static void deleteStop(String id, String agencyId) {
    	if (agencyId == null)
    		agencyId = session.get("agencyId");
    	
    	if (agencyId == null) {
    		badRequest();
    		return;
    	}
    	
    	AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);
    	try {
    		if (!tx.stops.containsKey(id)) {
    			notFound();
    			return;
    		}
    		
    		if (!tx.getTripPatternsByStop(id).isEmpty()) {
    			badRequest();
    			return;
    		}
    		
    		Stop s = tx.stops.remove(id);
    		tx.commit();
    		renderJSON(Base.toJson(s, false));
    	} catch (Exception e) {
    		badRequest();
    		e.printStackTrace();
    	} finally {
    		tx.rollbackIfOpen();
    	}
    }
    
    
    public static void findDuplicateStops(String agencyId) {
		if (agencyId == null)
			agencyId = session.get("agencyId");

		if (agencyId == null) {
			badRequest();
			return;
		}

		AgencyTx atx = VersionedDataStore.getAgencyTx(agencyId);

    	try {
			List<List<Stop>> ret = new ArrayList<List<Stop>>();

			for (Stop stop : atx.stops.values()) {
				// find nearby stops, within 5m
				// at the equator, 1 degree is 111 km
				// everywhere else this will overestimate, which is why we have a distance check as well (below)
				double thresholdDegrees = 5 / 111000d;

				Collection<Stop> candidateStops = atx.getStopsWithinBoundingBox(
						stop.getLat() + thresholdDegrees,
						stop.getLon() + thresholdDegrees,
						stop.getLat() - thresholdDegrees,
						stop.getLon() - thresholdDegrees);

				// we will always find a single stop, this one.
				if (candidateStops.size() <= 1)
					continue;

				List<Stop> duplicatesOfThis = new ArrayList<Stop>();

				// note: this stop will be added implicitly because it is distance zero from itself
				GeodeticCalculator gc = new GeodeticCalculator();
				gc.setStartingGeographicPoint(stop.getLon(), stop.getLat());
				for (Stop other : candidateStops) {
					gc.setDestinationGeographicPoint(other.getLon(), other.getLat());
					if (gc.getOrthodromicDistance() < 10) {
						duplicatesOfThis.add(other);
					}
				}

				if (duplicatesOfThis.size() > 1) {
					ret.add(duplicatesOfThis);
				}
			}

			renderJSON(Base.toJson(ret, false));
    	 } catch (Exception e) {
             e.printStackTrace();
             badRequest();
         }
		finally {
			atx.rollback();
		}
	}

    public static void mergeStops(String agencyId, @As(",") List<String> mergedStopIds) {
		if (mergedStopIds.size() <= 1) {
			badRequest();
			return;
		}

		if (agencyId == null)
			agencyId = session.get("agencyId");

		if (agencyId == null) {
			badRequest();
			return;
		}

		AgencyTx tx = VersionedDataStore.getAgencyTx(agencyId);

		try {
			Stop.merge(mergedStopIds, tx);
			tx.commit();
		} finally {
			tx.rollbackIfOpen();
		}
	}
}
