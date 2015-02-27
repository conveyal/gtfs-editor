package controllers.api;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import models.transit.Agency;
import models.transit.Stop;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import controllers.Api;
import controllers.Application;
import controllers.Secure;
import controllers.Security;
import datastore.VersionedDataStore;
import datastore.AgencyTx;
import datastore.GlobalTx;
import play.data.binding.As;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import utils.JacksonSerializers;

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
	    		
	    		renderJSON(Api.toJson(tx.stops.get(id), false));
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
	      		
	      		renderJSON(Api.toJson(stops, false));
	      	}
	    	else if (west != null && east != null && south != null && north != null) {
	    		// find all the stops in this bounding box
	    		// avert your gaze please as I write these generic types
	    		Tuple2<Double, Double> min = new Tuple2<Double, Double>(west, south);
	    		Tuple2<Double, Double> max = new Tuple2<Double, Double>(east, north);
	    		
	    		Set<Tuple2<Tuple2<Double, Double>, String>> matchedKeys =
	    				tx.stopsGix.subSet(new Tuple2(min, null), new Tuple2(max, Fun.HI));
	    		
	    		Collection<Stop> matchedStops =
	    				Collections2.transform(matchedKeys, new Function<Tuple2<Tuple2<Double, Double>, String>, Stop> () {

					@Override
					public Stop apply(
							Tuple2<Tuple2<Double, Double>, String> input) {
						return tx.stops.get(input.b);
					}
	    		});
	    		
	    		renderJSON(Api.toJson(matchedStops, false));
	    	}
	    	else if (patternId != null) {
	    		if (!tx.tripPatterns.containsKey(patternId)) {
	    			notFound();
	    			tx.rollback();
	    			return;
	    		}
	    		
	    		TripPattern p = tx.tripPatterns.get(patternId);
	    		
	    		Collection<Stop> ret = Collections2.transform(p.patternStops, new Function<TripPatternStop, Stop> () {
					@Override
					public Stop apply(TripPatternStop input) {
						return tx.stops.get(input.stopId);
					}
	    		});
	    		
	    		renderJSON(Api.toJson(ret, false));
	    	}
	    	else {
	    		badRequest();
	    	}
	    	
    		tx.rollback();
    	} catch (Exception e) {
    		tx.rollbackIfOpen();
    		e.printStackTrace();
    		badRequest();
    		return;
    	}   	
    }

    public static void createStop() {
    	AgencyTx tx = null;
        try {
            Stop stop = Api.mapper.readValue(params.get("body"), Stop.class);
            
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
            renderJSON(Api.toJson(stop, false));
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
            Stop stop = Api.mapper.readValue(params.get("body"), Stop.class);
            
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
            renderJSON(Api.toJson(stop, false));
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
    		
    		if (tx.countTripPatternsAtStop(id) > 0) {
    			badRequest();
    			return;
    		}
    		
    		Stop s = tx.stops.remove(id);
    		tx.commit();
    		renderJSON(Api.toJson(s, false));
    	} catch (Exception e) {
    		badRequest();
    		e.printStackTrace();
    	} finally {
    		tx.rollbackIfOpen();
    	}
    }
    
    
    /*public static void findDuplicateStops(String agencyId) {

    	try {    	
    		
    		List<List<Stop>> duplicateStopPairs = Stop.findDuplicateStops(BigInteger.valueOf(agencyId.longValue()));
    		renderJSON(Api.toJson(duplicateStopPairs, false));
    		
    	 } catch (Exception e) {
             e.printStackTrace();
             badRequest();
         }
    }

    public static void mergeStops(Long stop1Id, @As(",") List<String> mergedStopIds) {
        
        if(stop1Id == null)
            badRequest();

        Stop stop1 = Stop.findById(stop1Id);

        for(String stopIdStr : mergedStopIds) {

            Stop stop2 = Stop.findById(Long.parseLong(stopIdStr));

            if(stop1 == null && stop2 == null)
                badRequest();

            stop1.merge(stop2);

            ok();
        }
    }*/
}
