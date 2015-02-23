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
import models.VersionedDataStore;
import models.VersionedDataStore.AgencyTx;
import models.VersionedDataStore.GlobalTx;

import org.geotools.ows.bindings.UpdateSequenceTypeBinding;
import org.geotools.referencing.GeodeticCalculator;
import org.hibernate.annotations.Type;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;

import play.Logger;
import utils.GeoUtils;
import utils.JacksonSerializers;

public class TripPattern extends Model implements Serializable {
	public static final long serialVersionUID = 1;

    public String name;
    public String headsign;

    public LineString shape;
    
    // if true, use straight-line rather than shape-based distances
    public boolean useStraightLineDistances;
    
    /**
     * Lines showing how stops are being snapped to the shape.
     * @return
     */
    @JsonProperty("stopConnections")
    public LineString[] jsonGetStopConnections () {
    	if (useStraightLineDistances || shape == null)
    		return null;
    	
    	GlobalTx gtx = VersionedDataStore.getGlobalTx();
    	
    	try {
    		LineString[] ret = new LineString[patternStops.size()];
    	
    		double[] coordDistances = getCoordDistances(shape);
    		LocationIndexedLine shapeIdx = new LocationIndexedLine(shape);
    		    	
    		for (int i = 0; i < ret.length; i++) {
    			TripPatternStop ps = patternStops.get(i);
    			
    			if (ps.shapeDistTraveled == null) {
    				return null;
    			}
    			
    			Coordinate snapped = shapeIdx.extractPoint(getLoc(coordDistances, ps.shapeDistTraveled));
    			// offset it slightly so that line creation does not fail if the stop is coincident
    			snapped.x = snapped.x - 0.00000001;
    			Coordinate stop = gtx.stops.get(patternStops.get(i).stopId).location.getCoordinate();
    			ret[i] = GeoUtils.geometyFactory.createLineString(new Coordinate[] {stop, snapped});
    		}
    		
    		return ret;
    	} finally {
    		gtx.rollback();
    	}
    	
    }
    
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

    public void calcShapeDistTraveled () {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	calcShapeDistTraveled(tx);
    	tx.rollback();
    }
    
    /**
     * Calculate the shape dist traveled along the current shape. Do this by snapping points but constraining order.
     * 
     * To make this a bit more formal, here is the algorithm:
     * 
     * 1. We snap each stop to the nearest point on the shape, sliced by the shape_dist_traveled of the previous stop to ensure monotonicity.
     * 2. then compute the distance from stop to snapped point
     * 3. multiply by 2, create a buffer of that radius around the stop, and intersect with the shape.
     * 4. if it intersects in 1 or 2 places, assume that you have found the correct location for that stop and
     *    "fix" it into that position.
     * 5. otherwise, mark it to be returned to on the second pass
     * 6. on the second pass, just snap to the closest point on the subsection of the shape defined by the previous and next stop positions.
     */
	public void calcShapeDistTraveled(GlobalTx tx) {
		if (patternStops.size() == 0)
			return;
		
		// we don't actually store shape_dist_traveled, but rather the distance from the previous point along the shape
		// however, for the algorithm it's more useful to have the cumulative dist traveled
		double[] shapeDistTraveled = new double[patternStops.size()];
		
		useStraightLineDistances = false;
		
		if (shape == null) {
			calcShapeDistTraveledStraightLine(tx);
			return;
		}
		
		// compute the shape dist traveled of each coordinate of the shape
		double[] shapeDist = getCoordDistances(shape);
		
		double[] coordDist = shapeDist;
		
		for (int i = 0; i < shapeDistTraveled.length; i++) {
			shapeDistTraveled[i] = -1;
		}
		
		// location along the entire shape
		LocationIndexedLine shapeIdx = new LocationIndexedLine(shape);
		// location along the subline currently being considered
		LocationIndexedLine subIdx = shapeIdx;
		
		LineString subShape = shape;
		
		double lastShapeDistTraveled = 0;
		
		int fixed = 0;
		
		GeodeticCalculator gc = new GeodeticCalculator();
		
		// detect backwards shapes
		int backwards = 0;
		
		double lastPos = -1;
		for (TripPatternStop tps : patternStops) {
			Stop stop = tx.stops.get(tps.stopId);
			double pos = getDist(shapeDist, shapeIdx.project(stop.location.getCoordinate()));
			
			if (lastPos > 0) {
				if (pos > lastPos)
					backwards--;
				else if (pos > lastPos)
					backwards++;				
			}
			
			lastPos = pos;
		}
		
		if (backwards > 0) {
			Logger.warn("Detected likely backwards shape for trip pattern %s (%s) on route %s, reversing", id, name, routeId);
			this.shape = (LineString) this.shape.reverse();
			calcShapeDistTraveled(tx);
			return;
		}
		else if (backwards == 0) {
			Logger.warn("Unable to tell if shape is backwards for trip pattern %s (%s) on route %s, assuming it is correct", id, name, routeId);
		}
		
		// first pass: fix the obvious stops
		for (int i = 0; i < shapeDistTraveled.length; i++) {
			TripPatternStop tps = patternStops.get(i);
			Stop stop = tx.stops.get(tps.stopId);
			LinearLocation candidateLoc = subIdx.project(stop.location.getCoordinate());
			Coordinate candidatePt = subIdx.extractPoint(candidateLoc);
			
			// step 2: compute distance
			gc.setStartingGeographicPoint(stop.location.getX(), stop.location.getY());
			gc.setDestinationGeographicPoint(candidatePt.x, candidatePt.y);
			double dist = gc.getOrthodromicDistance();
			
			// don't snap stops more than 1km
			if (dist > 1000) {
				Logger.warn("Stop is more than 1km from its shape, using straight-line distances");
				this.calcShapeDistTraveledStraightLine(tx);
				return;
			}
						
			// step 3: compute buffer
			// add 5m to the buffer so that if the stop sits exactly atop two lines we don't just pick one
			Polygon buffer = GeoUtils.bufferGeographicPoint(stop.location.getCoordinate(), dist * 2 + 5, 20);
			
			Geometry intersection = buffer.intersection(shape);
			if (intersection.getNumGeometries() == 1) {
				// good, only one intersection
				shapeDistTraveled[i] = lastShapeDistTraveled + getDist(coordDist, candidateLoc);
				lastShapeDistTraveled = shapeDistTraveled[i];
				
				// recalculate shape dist traveled and idx
				subShape = (LineString) subIdx.extractLine(candidateLoc, subIdx.getEndIndex());
				subIdx = new LocationIndexedLine(subShape);
				
				coordDist = getCoordDistances(subShape);
				
				fixed++;
			}
		}
		
		Logger.info("Fixed %s / %s stops after first round for trip pattern %s (%s) on route %s", fixed, shapeDistTraveled.length, id, name, routeId);
		
		// pass 2: fix the rest of the stops
		lastShapeDistTraveled = 0;
		for (int i = 0; i < shapeDistTraveled.length; i++) {
			TripPatternStop tps = patternStops.get(i);
			Stop stop = tx.stops.get(tps.stopId);
			
			if (shapeDistTraveled[i] >= 0) {
				lastShapeDistTraveled = shapeDistTraveled[i];
				continue;
			}
			
			// find the next shape dist traveled
			double nextShapeDistTraveled = shapeDist[shapeDist.length - 1];
			for (int j = i; j < shapeDistTraveled.length; j++) {
				if (shapeDistTraveled[j] >= 0) {
					nextShapeDistTraveled = shapeDistTraveled[j];
					break;
				}
			}		
			
			// create and index the subshape
			// recalculate shape dist traveled and idx
			subShape = (LineString) shapeIdx.extractLine(getLoc(shapeDist, lastShapeDistTraveled), getLoc(shapeDist, nextShapeDistTraveled));
			
			if (subShape.getLength() < 0.00000001) {
				Logger.warn("Two stops on trip pattern %s map to same point on shape", id);
				shapeDistTraveled[i] = lastShapeDistTraveled;
				continue;
			}
			
			subIdx = new LocationIndexedLine(subShape);
			
			coordDist = getCoordDistances(subShape);
			
			LinearLocation loc = subIdx.project(stop.location.getCoordinate());
			shapeDistTraveled[i] = lastShapeDistTraveled + getDist(coordDist, loc);
			lastShapeDistTraveled = shapeDistTraveled[i];
		}
		
		// assign default distances
		for (int i = 0; i < shapeDistTraveled.length; i++) {
			patternStops.get(i).shapeDistTraveled = shapeDistTraveled[i];
		}
	}
	
	/** Calculate distances using straight line geometries */
	public void calcShapeDistTraveledStraightLine(GlobalTx tx) {
		useStraightLineDistances = true;
		GeodeticCalculator gc = new GeodeticCalculator();
		Stop prev = tx.stops.get(patternStops.get(0).stopId);
		patternStops.get(0).shapeDistTraveled = 0D;
		double previousDistance = 0;
		for (int i = 1; i < patternStops.size(); i++) {
			TripPatternStop ps = patternStops.get(i);
			Stop stop = tx.stops.get(ps.stopId);
			gc.setStartingGeographicPoint(prev.location.getX(), prev.location.getY());
			gc.setDestinationGeographicPoint(stop.location.getX(), stop.location.getY());
			previousDistance = ps.shapeDistTraveled = previousDistance + gc.getOrthodromicDistance();
		}
	}

	/** get the distances from the start of the line string to every coordinate along the line string */
	private static double[] getCoordDistances(LineString line) {
		double[] coordDist = new double[line.getNumPoints()];
		coordDist[0] = 0;
		
		Coordinate prev = line.getCoordinateN(0);
		GeodeticCalculator gc = new GeodeticCalculator();
		for (int j = 1; j < coordDist.length; j++) {
			Coordinate current = line.getCoordinateN(j);
			gc.setStartingGeographicPoint(prev.x, prev.y);
			gc.setDestinationGeographicPoint(current.x, current.y);
			coordDist[j] = coordDist[j - 1] + gc.getOrthodromicDistance();
			prev = current;
		}
		
		return coordDist;
	}

	/**
	 * From an array of distances at coordinates and a distance, get a linear location for that distance.
	 */
	private static LinearLocation getLoc(double[] distances, double distTraveled) {
		if (distTraveled < 0)
			return null;
		
		// this can happen due to rounding errors
		else if (distTraveled >= distances[distances.length - 1]) {
			Logger.warn("Shape dist traveled past end of shape, was %s, expected max %s, clamping", distTraveled, distances[distances.length - 1]);
			return new LinearLocation(distances.length - 1, 0);
		}
		
		for (int i = 1; i < distances.length; i++) {
			if (distTraveled <= distances[i]) {
				// we have found the appropriate segment
				double frac = (distTraveled - distances[i - 1]) / (distances[i] - distances[i - 1]);
				return new LinearLocation(i - 1, frac);
			}
		}
		
		return null;
	}
	
	/**
	 * From an array of distances at coordinates and linear locs, get a distance for that location.
	 */
	private static double getDist(double[] distances, LinearLocation loc) {
		if (loc.getSegmentIndex() == distances.length - 1)
			return distances[distances.length - 1];
		
		return distances[loc.getSegmentIndex()] + (distances[loc.getSegmentIndex() + 1] - distances[loc.getSegmentIndex()]) * loc.getSegmentFraction();
	}
}
