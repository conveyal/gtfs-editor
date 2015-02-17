package models.transit;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.conveyal.gtfs.model.Shape;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

import models.Model;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Type;

import utils.EncodedPolylineBean;
import utils.PolylineEncoder;

public class TripShape extends Model implements Serializable {
	public static final long serialVersionUID = 1;
	/** unused during export */
    public String gtfsShapeId;
    public String description;
    
    private static GeometryFactory geometryFactory = new GeometryFactory();
    
    public Double describedDistance;
    
    public Agency agency;
    
    public LineString shape;
    
    public LineString simpleShape;
    
    /**
     * Create a new trip shape from a GTFS shape.
     *
     * @param shape
     */
    public TripShape(Collection<Shape> shapes, String shapeId) {
    	// we make the assumption that the map has a defined sort order, because it's coming from a GTFSFeed, and it does.
    	// however, we check our assumption
    	
    	if (shapes.size() < 2)
    		throw new InvalidShapeException("Must have at least two points in a shape!");
    	
    	Coordinate[] coords = new Coordinate[shapes.size()];
    	
    	int last = Integer.MIN_VALUE;
    	int i = 0;
    	for (Shape shape : shapes) {
    		if (last > shape.shape_pt_sequence)
    			throw new RuntimeException("Shape points out of sequence, this implies a bug.");
    		
    		last = shape.shape_pt_sequence;
    		
    		coords[i++] = new Coordinate(shape.shape_pt_lon, shape.shape_pt_lat);
    	}
    	
    	this.shape = geometryFactory.createLineString(coords);
    	this.gtfsShapeId = shapeId;
    }
    
    /**
     * Create a trip shape from an encoded polyline.
     */
    public TripShape(String encoded) {
    	this.shape = generateLinestring(encoded);
    }

    public void updateShapeFromEncoded(String encoded) {
    	this.shape = generateLinestring(encoded);
    }

    public String generateEncoded() {
        
        EncodedPolylineBean ecb = PolylineEncoder.createEncodings(shape);
        
        return ecb.getPoints();
    }
    
    public static LineString generateLinestring(String encoded) {
        
        EncodedPolylineBean ecb = new EncodedPolylineBean(encoded, null, 0);
        List<Coordinate> coords = PolylineEncoder.decode(ecb);
        return geometryFactory.createLineString(coords.toArray(new Coordinate[coords.size()]));
    }

	public Shape[] toGtfs() {
		Coordinate[] coords = this.shape.getCoordinates();
		Shape[] ret = new Shape[coords.length];
		
		for (int i = 0; i < coords.length; i++) {
			// TODO: shape_dist_traveled
			ret[i] = new Shape(getGtfsId(), coords[i].y, coords[i].x, i, Double.NaN);
		}
		
		return ret;
	}

	@JsonIgnore
	public String getGtfsId() {
		if (gtfsShapeId != null && !gtfsShapeId.isEmpty())
			return gtfsShapeId;
		else
			return id.toString();
	}
	
	public static class InvalidShapeException extends IllegalArgumentException {
		public InvalidShapeException(String msg) {
			super(msg);
		}
	}
}
