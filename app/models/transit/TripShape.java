package models.transit;



import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.ManyToOne;
import javax.persistence.Query;
import javax.persistence.Transient;

import com.conveyal.gtfs.model.Shape;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.PrecisionModel;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Type;

import play.db.jpa.Model;
import utils.EncodedPolylineBean;
import utils.PolylineEncoder;

@Entity
public class TripShape extends Model {
	/** unused during export */
    public String gtfsShapeId;
    public String description;
    
    public Double describedDistance;
    
    @ManyToOne
    public Agency agency;
    
    @Type(type = "org.hibernatespatial.GeometryUserType") 
    public LineString shape;
    
    @Type(type = "org.hibernatespatial.GeometryUserType") 
    public LineString simpleShape;
    

    public void updateShapeFromEncoded(String encoded) {
        
        String linestring = generateLinestring(encoded);

        TripShape.em().createNativeQuery("UPDATE tripshape SET shape = ST_GeomFromText( ?, 4326) WHERE id = ?;")
            .setParameter(1,  linestring)
            .setParameter(2,  this.id)
            .executeUpdate();
        
        this.refresh();
    }

    public String generateEncoded() {
        
        EncodedPolylineBean ecb = PolylineEncoder.createEncodings(shape);
        
        return ecb.getPoints();
    }

    public static TripShape createFromEncoded(String encoded) {
    	
        
    	BigInteger tripShapeId = TripShape.nativeInsert(TripShape.em(), "", generateLinestring(encoded), 0.0);

        return TripShape.findById(tripShapeId.longValue());
    }

    public static TripShape createFromPattern(TripPattern tp) {
    	
    	List<String> points = new ArrayList<String>();
    	
    	Collections.sort(tp.patternStops);
    	
        for(TripPatternStop tps : tp.patternStops) {
        
            points.add(new Double(tps.stop.locationPoint().getX()).toString() + " " + new Double(tps.stop.locationPoint().getY()).toString());
        }
        
        if(points.size() > 0) {
        	String linestring = "LINESTRING(" + StringUtils.join(points, ", ") + ")";
        	
        	
        	BigInteger tripShapeId = TripShape.nativeInsert(TripShape.em(), "", linestring, 0.0);

            return TripShape.findById(tripShapeId.longValue());
        }
        else 
        	return null;
        
    }
    
    public static String generateLinestring(String encoded) {
        
        EncodedPolylineBean ecb = new EncodedPolylineBean(encoded, null, 0);
        List<Coordinate> coords = PolylineEncoder.decode(ecb);
        
        List<String> points = new ArrayList<String>();
        for(Coordinate coord : coords) {
            points.add(new Double(coord.y).toString() + " " + new Double(coord.x).toString());
        }
        
        String linestring = "LINESTRING(" + StringUtils.join(points, ", ") + ")";

        return linestring;
    }

    public static BigInteger nativeInsert(EntityManager em, String shapeId, String shape, Double distance)
    {
    	Query idQuery = em.createNativeQuery("SELECT NEXTVAL('hibernate_sequence');");
    	BigInteger nextId = (BigInteger)idQuery.getSingleResult();
    	
        em.createNativeQuery("INSERT INTO tripshape (id, gtfsshapeid, shape, describeddistance)" +
        	"  VALUES(?, ?, ST_GeomFromText( ?, 4326), ?);")
          .setParameter(1,  nextId)
          .setParameter(2,  shapeId)	            
          .setParameter(3,  shape)
          .setParameter(4,  distance)
          .executeUpdate();
        
        return nextId;
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

	@Transient
	@JsonIgnore
	public String getGtfsId() {
		if (gtfsShapeId != null && !gtfsShapeId.isEmpty())
			return gtfsShapeId;
		else
			return id.toString();
	}
   
}
