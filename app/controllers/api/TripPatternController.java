package controllers.api;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import models.transit.Route;
import models.transit.StopTime;
import models.transit.Trip;
import models.transit.TripPattern;
import models.transit.TripPatternStop;

import org.geotools.geometry.jts.JTS;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;
import org.opengis.referencing.operation.MathTransform;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.linearref.LengthLocationMap;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

import controllers.Api;
import datastore.VersionedDataStore;
import datastore.AgencyTx;
import datastore.GlobalTx;
import play.Logger;
import play.db.jpa.JPA;
import play.mvc.Controller;

public class TripPatternController extends Controller {
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
            	   renderJSON(Api.toJson(tx.tripPatterns.get(id), false));
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
	            	
	            	renderJSON(Api.toJson(patts, false));     	
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
            tripPattern = Api.mapper.readValue(params.get("body"), TripPattern.class);
            
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

            renderJSON(Api.toJson(tripPattern, false));
        } catch (Exception e) {
            e.printStackTrace();
            badRequest();
        }
    }

    public static void updateTripPattern() {
        TripPattern tripPattern;
        AgencyTx tx = null;
        try {
            tripPattern = Api.mapper.readValue(params.get("body"), TripPattern.class);
            
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

            renderJSON(Api.toJson(tripPattern, false));
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
    
    public static void calcTripPatternTimes(Long id, Double velocity, int defaultDwell) {
    	/*
    	TripPattern tripPattern = TripPattern.findById(id);
    	
    	List<TripPatternStop> patternStops = TripPatternStop.find("pattern = ? ORDER BY stopSequence", tripPattern).fetch();
    	
    	Double distanceAlongLine = 0.0;
    	
        for(TripPatternStop patternStop : patternStops)
        {
        	patternStop.defaultTravelTime = (int) Math.round((patternStop.defaultDistance - distanceAlongLine) / velocity);
            patternStop.defaultDwellTime = defaultDwell;
        	
        	distanceAlongLine = patternStop.defaultDistance;
        	
        	patternStop.save();
        }*/
    
        ok();
    }
}
