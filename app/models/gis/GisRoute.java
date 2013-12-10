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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import models.transit.Agency;
import models.transit.Route;

import org.apache.commons.lang.StringUtils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.annotations.Type;

import play.db.jpa.Model;

@Entity
public class GisRoute extends Model {
	
	@ManyToOne
    public GisUpload gisUpload;
	
    @ManyToOne
    public Agency agency;
    
    public String oid;
	
	public String routeId;
	public String routeName;
    public String description;
    
    @Enumerated(EnumType.STRING)
    public GisRouteType routeType;
    
    @Type(type = "org.hibernatespatial.GeometryUserType") 
    public MultiLineString originalShape;  
    		
    public void clear()
    {
    	List<GisRouteAlignment> gisRouteAlignments = GisRouteAlignment.find("gisRoute = ?", this).fetch();
    	
    	for(GisRouteAlignment gisRouteAlignment : gisRouteAlignments)
    	{
    		gisRouteAlignment.clear();
    		gisRouteAlignment.delete();
    	}
    	
    	List<GisRouteControlPoint> gisRouteControlPoints = GisRouteControlPoint.find("gisRoute = ?", this).fetch();
    	
    	for(GisRouteControlPoint gisRouteControlPoint : gisRouteControlPoints)
    	{
    		gisRouteControlPoint.clear();
    		gisRouteControlPoint.delete();
    	}
    	
    	List<Route> routes = Route.find("gisRoute = ?", this).fetch();
    	
    	for(Route route : routes)
    	{
    		route.gisRoute = null;
    		route.save();	
    	}
    }
    
    public void processSegments()
    {	
    	clear();
    	
    	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
    	
    	int geomCount = this.originalShape.getNumGeometries();
    	int i = 0;
    	
    	Integer sequenceId = 1;
    	
    	while(i < geomCount)
    	{
    		LineString lineSegment = (LineString)this.originalShape.getGeometryN(i);
    		
    		GisRouteControlPoint fromPoint = findControlPoint(geometryFactory.createPoint(lineSegment.getCoordinateN(0)), sequenceId);
    		
    		sequenceId = fromPoint.originalSequence + 1;
    		
     		GisRouteControlPoint toPoint = findControlPoint(geometryFactory.createPoint((lineSegment.getCoordinateN(lineSegment.getNumPoints() - 1))), sequenceId);
     		
     		sequenceId = toPoint.originalSequence + 1;
     		
     		if(GisRouteSegment.count("fromPoint = ? AND toPoint = ?", fromPoint, toPoint) == 0)
     		{
     		
	     		GisRouteSegment originalSegment =  new GisRouteSegment();
	     		
	     		originalSegment.reverse = false;
	     		originalSegment.fromPoint = fromPoint;
	     		originalSegment.toPoint = toPoint;
	     		originalSegment.segment = lineSegment;
	     		originalSegment.segment.setSRID(4326);
	     		originalSegment.save();
	     		
	     		// can't handle bi-directionality on loop segments (yet)
	     		if(fromPoint != toPoint)
	     		{
		     		GisRouteSegment reverseSegment =  new GisRouteSegment();
		     		
		     		reverseSegment.reverse = true;
		     		reverseSegment.fromPoint = toPoint;
		     		reverseSegment.toPoint = fromPoint;
		     		reverseSegment.segment = (LineString)lineSegment.reverse();
		     		reverseSegment.segment.setSRID(4326);
		     		reverseSegment.save();
	     		}
     		}
     		
    		i++;
    	}
    }
    
    public GisRouteControlPoint findControlPoint(Point point, Integer sequenceId)
    {
    	List<GisRouteControlPoint> controlPoints = GisRouteControlPoint.find("gisRoute = ? and within(controlPoint, buffer(ST_GeomFromText(?, 4326), 0.00015)) = true", this, point.toText()).fetch();
    	
    	if(controlPoints.size() >= 1)
    		return controlPoints.get(0);
    	else
    	{
    		GisRouteControlPoint controlPoint = new GisRouteControlPoint();
    		controlPoint.gisRoute = this;
    		controlPoint.controlPoint = point;
    		controlPoint.controlPoint.setSRID(4326);
    		controlPoint.originalSequence = sequenceId;
    		controlPoint.save();
    		
    		return controlPoint;
    	}
    }
    
    public List<GisRouteControlPoint> getControlPoints()
    {
    	return  GisRouteControlPoint.find("gisRoute = ?", this).fetch();
    }
}




