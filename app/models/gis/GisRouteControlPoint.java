package models.gis;



import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import models.transit.Agency;

import org.apache.commons.lang.StringUtils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.annotations.Type;

import play.db.jpa.Model;

@Entity
public class GisRouteControlPoint extends Model {
	
	@ManyToOne
    public GisRoute gisRoute;
	
    @Type(type = "org.hibernatespatial.GeometryUserType") 
    public Point controlPoint;  
    
    public Integer originalSequence;
    
    public void clear()
    {
    	List<GisRouteSegment> toSegments = GisRouteSegment.find("toPoint = ?", this).fetch();
    	
    	for(GisRouteSegment segment : toSegments)
    	{
    		segment.delete();
    	}
    	
    	List<GisRouteSegment> fromSegments = GisRouteSegment.find("fromPoint = ?", this).fetch();
    	
    	for(GisRouteSegment segment : fromSegments)
    	{
    		segment.delete();
    	}
    }
    
}


