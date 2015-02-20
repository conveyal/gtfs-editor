package models.transit;


import static java.util.Collections.sort;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import models.Model;
import models.VersionedDataStore.AgencyTx;

import org.geotools.ows.bindings.UpdateSequenceTypeBinding;
import org.hibernate.annotations.Type;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.LineString;

import play.Logger;
import utils.JacksonSerializers;

public class TripPattern extends Model implements Serializable {
	public static final long serialVersionUID = 1;

    public String name;
    public String headsign;

    @JsonSerialize(using=JacksonSerializers.EncodedPolylineSerializer.class)
    @JsonDeserialize(using=JacksonSerializers.EncodedPolylineDeserializer.class)
    public LineString shape;
    
    public String routeId;
    
    public String agencyId;

    public List<TripPatternStop> patternStops;

    public Integer headway;

    public TripPattern()
    {

    }

    public TripPattern(String name, String headsign, LineString shape, Route route)
    {
    	this.name = name;
    	this.headsign = headsign;
    	this.shape = shape;
    	this.routeId = route.id;
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
                    if (i < originalStops.size() && !originalStops.get(i).stopId.equals(newStops.get(i + 1).stopId)) {
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

            // insert a skipped stop at the difference location
            
            for (Trip trip : tx.getTripsByPattern(originalTripPattern.id)) {
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
                    if (!originalStops.get(i).stopId.equals(newStops.get(i - 1).stopId)) {
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
            
            for (Trip trip : tx.getTripsByPattern(originalTripPattern.id)) {
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
            
            for (Trip trip : tx.getTripsByPattern(originalTripPattern.id)) {
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
