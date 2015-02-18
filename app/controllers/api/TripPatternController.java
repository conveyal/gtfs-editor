package controllers.api;

import static java.util.Collections.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import models.VersionedDataStore;
import models.VersionedDataStore.AgencyTx;
import models.VersionedDataStore.GlobalTx;
import models.transit.Route;
import models.transit.StopTime;
import models.transit.Trip;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import models.transit.TripShape;

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
import play.Logger;
import play.db.jpa.JPA;
import play.mvc.Controller;

public class TripPatternController extends Controller {
    public static void getTripPattern(String id, String routeId, String agencyId) {
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
            
            tx.commit();
            
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
            
            if(tripPattern.encodedShape != null) {
            	TripShape ts = new TripShape(tripPattern.encodedShape);
            	tx.shapes.put(ts.id, ts);
            	tripPattern.shapeId = ts.id;	
            }
            
            if (tx.tripPatterns.containsKey(tripPattern.id)) {
            	tx.rollback();
            	badRequest();
            }
            
            tx.tripPatterns.put(tripPattern.id, tripPattern);

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
                
            
            if (tripPattern.encodedShape != null) {
                if (originalTripPattern.shapeId != null) {
                	// get and save the shape
                	TripShape shape = tx.shapes.get(originalTripPattern.shapeId);
                    shape.updateShapeFromEncoded(tripPattern.encodedShape);
                    tripPattern.shapeId = originalTripPattern.shapeId;
                }
                else {
                	TripShape ts = new TripShape(tripPattern.encodedShape);
                	tx.shapes.put(ts.id, ts);
                	tripPattern.shapeId = ts.id;	
                }
            }
            else {
                tripPattern.shapeId = null;
                // need to remove old shapes...
            }
                        
            // update stop times
            try {
            	reconcilePatternStops(originalTripPattern, tripPattern, tx);
            } catch (IllegalStateException e) {
            	tx.rollback();
            	badRequest();
            	return;
            }
            	            
            if(tripPattern.encodedShape != null) {
            	TripShape ts = new TripShape(tripPattern.encodedShape);
            	tx.shapes.put(ts.id, ts);
            	tripPattern.shapeId = ts.id;	
            }
            
            if (tx.tripPatterns.containsKey(tripPattern.id)) {
            	tx.rollback();
            	badRequest();
            }
            
            tx.tripPatterns.put(tripPattern.id, tripPattern);

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
    
    /**
     * update the trip pattern stops and the associated stop times
     * see extensive discussion in ticket #102
     * basically, we assume only one stop has changed---either it's been removed, added or moved
     * this is consistent with the use of Backbone.save in the UI, and
     * also with the principle of least magic possible
     * of course, we check to ensure that that is the case and fail if it's not
     * this lets us easily detect what has happened simply by length
     */
    public static void reconcilePatternStops(TripPattern originalTripPattern, TripPattern newTripPattern, AgencyTx tx) {        
    	// convenience
    	List<TripPatternStop> originalStops = originalTripPattern.patternStops;
    	List<TripPatternStop> newStops = newTripPattern.patternStops;
    	
    	// no need to do anything
    	// see #174
    	if (originalStops.size() == 0)
    		return;
        
        // ADDITIONS
        if (originalStops.size() == newStops.size() - 1) {
            // we have an addition; find it

            int differenceLocation = -1;
            for (int i = 0; i < newStops.size(); i++) {
                if (differenceLocation != -1) {
                    // we've already found the addition
                    if (i < originalStops.size() && originalStops.get(i).stopId.equals(newStops.get(i + 1).stopId)) {
                        // there's another difference, which we weren't expecting
                        throw new IllegalStateException("Multiple differences found when trying to detect stop addition");
                    }
                }

                // if we've reached where one trip has an extra stop, or if the stops at this position differ
                else if (i == newStops.size() - 1 || !originalStops.get(i).stopId.equals(newStops.get(i).stopId)) {
                    // we have found the difference
                    differenceLocation = i;
                }
            }

            // repack stop times by inserting a skipped stop at the difference location
            Set<Tuple2<String, String>> tripKeys =
            		tx.tripsByRoute.subSet(new Tuple2(originalTripPattern.id, null), new Tuple2(originalTripPattern.id, Fun.HI));
            
            for (Tuple2<String, String> key : tripKeys) {
            	Trip trip = tx.trips.get(key.b);
            	trip.stopTimes.add(differenceLocation, null);
            	// TODO: safe?
            	tx.trips.put(trip.id, trip);
            }            
        }
        
        // DELETIONS
        else if (originalStops.size() == newStops.size() + 1) {
            // we have an deletion; find it
            int differenceLocation = -1;
            for (int i = 0; i < originalStops.size(); i++) {
                if (differenceLocation != -1) {
                    if (originalStops.get(i).stopId.equals(newStops.get(i - 1).stopId)) {
                        // there is another difference, which we were not expecting
                        throw new IllegalStateException("Multiple differences found when trying to detect stop removal");
                    }
                }
                
                // we've reached the end and the only difference is length (so the last stop is the different one)
                // or we've found the difference
                else if (i == originalStops.size() - 1 || !originalStops.get(i).stopId.equals(newStops.get(i).stopId)) {
                    differenceLocation = i;
                }
            }
            
            // remove stop times for removed pattern stop
            Tuple2<String, String> removedStopId = originalStops.get(differenceLocation).stopId;
            
            Set<Tuple2<String, String>> tripKeys =
            		tx.tripsByRoute.subSet(new Tuple2(originalTripPattern.id, null), new Tuple2(originalTripPattern.id, Fun.HI));
            
            for (Tuple2<String, String> key : tripKeys) {
            	Trip trip = tx.trips.get(key.b);
            	StopTime removed = trip.stopTimes.remove(differenceLocation);
            	
            	if (!removed.stopId.equals(removedStopId)) {
            		throw new IllegalStateException("Attempted to remove wrong stop!");
            	}
            	
            	// TODO: safe?
            	tx.trips.put(trip.id, trip);
            }
        }
        
        // TRANSPOSITIONS
        else if (originalStops.size() == newStops.size()) {
            // Imagine the trip patterns pictured below (where . is a stop, and lines indicate the same stop)
            // the original trip pattern is on top, the new below
            // . . . . . . . .
            // | |  \ \ \  | |
            // * * * * * * * *
            // also imagine that the two that are unmarked are the same
            // (the limitations of ascii art, this is prettier on my whiteboard)
            // There are three regions: the beginning and end, where stopSequences are the same, and the middle, where they are not
            // The same is true of trips where stops were moved backwards
            
            // find the left bound of the changed region
            int firstDifferentIndex = 0;
            while (originalStops.get(firstDifferentIndex).stopId.equals(newStops.get(firstDifferentIndex).stopId)) {
            	firstDifferentIndex++;
                
                if (firstDifferentIndex == originalStops.size())
                    // trip patterns do not differ at all, nothing to do
                    return;
            }
            
            // find the right bound of the changed region
            int lastDifferentIndex = originalStops.size() - 1;
            while (originalStops.get(lastDifferentIndex).stopId.equals(newStops.get(lastDifferentIndex).stopId)) {
                lastDifferentIndex--;
            }
            
            // TODO: write a unit test for this
            if (firstDifferentIndex == lastDifferentIndex) {
                throw new IllegalStateException("stop substitutions are not supported, region of difference must have length > 1");
            }
            
            // figure out whether a stop was moved left or right
            // note that if the stop was only moved one position, it's impossible to tell, and also doesn't matter,
            // because the requisite operations are equivalent
            int from, to;
            
            // TODO: ensure that this is all that happened (i.e. verify stop ID map inside changed region)
            if (originalStops.get(firstDifferentIndex).stopId.equals(newStops.get(lastDifferentIndex).stopId)) {
            	// stop was moved right
            	from = firstDifferentIndex;
            	to = lastDifferentIndex;
            }

            else if (newStops.get(firstDifferentIndex).stopId.equals(originalStops.get(lastDifferentIndex).stopId)) {
                // stop was moved left
            	from = lastDifferentIndex;
            	to = firstDifferentIndex;
            }
            
            else {
                throw new IllegalStateException("not a simple, single move!");
            }

            Set<Tuple2<String, String>> tripKeys =
            		tx.tripsByRoute.subSet(new Tuple2(originalTripPattern.id, null), new Tuple2(originalTripPattern.id, Fun.HI));
            
            for (Tuple2<String, String> key : tripKeys) {
            	Trip trip = tx.trips.get(key.b);

            	StopTime moved = trip.stopTimes.remove(from);
            	trip.stopTimes.add(to, moved);
            	trip.invalid = true;
            	
            	// TODO: safe?
            	tx.trips.put(trip.id, trip);
            }
        }

        
        // OTHER STUFF IS NOT SUPPORTED
        else {
            throw new IllegalStateException("Changes to trip pattern stops must be made one at a time");
        }
    }
}
