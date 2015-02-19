package controllers.api;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;

import models.VersionedDataStore;
import models.VersionedDataStore.GlobalTx;
import models.transit.Agency;
import models.transit.Stop;
import controllers.Api;
import play.data.binding.As;
import play.mvc.Controller;

public class StopController extends Controller {
    public static void getStop(String id, String agencyId, Double west, Double east, Double north, Double south) {
    	if (agencyId == null) {
    		badRequest();
    		return;
    	}
    	
    	final GlobalTx tx = VersionedDataStore.getGlobalTx();    	
 
    	try {
	    	Tuple2<String, String> stopId = new Tuple2(agencyId, id);
	    	
	    	if (id != null) {
	    		if (!tx.stops.containsKey(stopId)) {
	    			tx.rollback();
	    			notFound();
	    			return;
	    		}
	    		
	    		renderJSON(Api.toJson(tx.stops.get(stopId), false));
	    	}
	    	else if (west != null && east != null && south != null && north != null) {
	    		// find all the stops in this bounding box
	    		// avert your gaze please as I write these generic types
	    		Tuple2<Double, Double> min = new Tuple2<Double, Double>(west, south);
	    		Tuple2<Double, Double> max = new Tuple2<Double, Double>(east, north);
	    		
	    		Set<Tuple2<Tuple2<Double, Double>, Tuple2<String, String>>> matchedKeys =
	    				tx.stopsGix.subSet(new Tuple2(min, null), new Tuple2(max, Fun.HI));
	    		
	    		Collection<Stop> matchedStops =
	    				Collections2.transform(matchedKeys, new Function<Tuple2<Tuple2<Double, Double>, Tuple2<String, String>>, Stop> () {

					@Override
					public Stop apply(
							Tuple2<Tuple2<Double, Double>, Tuple2<String, String>> input) {
						return tx.stops.get(input.b);
					}
	    		});
	    		
	    		renderJSON(Api.toJson(matchedStops, false));
	    	}
	    	else {
	    		badRequest();
	    	}
	    	
    		tx.rollback();
    	} catch (Exception e) {
    		tx.rollback();
    		e.printStackTrace();
    		badRequest();
    		return;
    	}   	
    }

    public static void createStop() {
        GlobalTx tx = VersionedDataStore.getGlobalTx();

        try {
            Stop stop = Api.mapper.readValue(params.get("body"), Stop.class);
            stop.generateId();            
            tx.stops.put(stop.id, stop);
            tx.commit();
            renderJSON(Api.toJson(stop, false));
        } catch (Exception e) {
        	tx.rollback();
            e.printStackTrace();
            badRequest();
        }
    }


    public static void updateStop() {
        GlobalTx tx = VersionedDataStore.getGlobalTx();

        try {
            Stop stop = Api.mapper.readValue(params.get("body"), Stop.class);

            if (!tx.stops.containsKey(stop.id)) {
            	badRequest();
            	tx.rollback();
            	return;
            }
            
            tx.stops.put(stop.id, stop);
            return;
            
        } catch (Exception e) {
        	tx.rollback();
            e.printStackTrace();
            badRequest();
        }
    }

    public static void deleteStop(Long id) {
    	
    	// TODO: what to do?
    	ok();
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
