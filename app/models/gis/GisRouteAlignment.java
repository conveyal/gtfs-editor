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

import org.apache.commons.lang.StringUtils;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.hibernate.annotations.Type;

import play.db.jpa.Model;

@Entity
public class GisRouteAlignment extends Model {
	
	@ManyToOne
    public GisRoute gisRoute;
	
    public String description;
    
    public Boolean reverseAlignment = false;
    
    @Type(type = "org.hibernatespatial.GeometryUserType") 
    public LineString gtfsShape;    
    
    public void clear()
    {
    	List<GisRouteControlPointSequence> pointSequence = GisRouteControlPointSequence.find("gisRouteAlignment = ?", this).fetch();
    	
    	for(GisRouteControlPointSequence point : pointSequence)
    	{
    		point.delete();
    	}
    	   	
    	this.gtfsShape = null;
    	this.save();
    }
    
    public void createDefaultAlignement()
    {
    	Boolean linear = true;
    	
    	List<GisRouteControlPoint> controlPoints = GisRouteControlPoint.find("gisRoute = ? ORDER BY originalSequence", this.gisRoute).fetch();
    	
    	for(GisRouteControlPoint cp : controlPoints)
    	{
    		if(GisRouteSegment.count("toPoint = ? AND (reverse = false OR reverse is null)", cp) > 1)
    			linear = false;
    		if(GisRouteSegment.count("fromPoint = ? AND (reverse = false OR reverse is null)", cp) > 1)
    			linear = false;
    	}
    	
    	if(linear)
    	{
    		GisRouteControlPoint cp1 = null;
    		GisRouteControlPoint cp2 = null;
    		
    		for(GisRouteControlPoint cp : controlPoints)
        	{
    			cp1 = cp2;
    			cp2 = cp;
    			
    			if(cp1 != null)
    			{
    				GisRouteSegment segment = GisRouteSegment.find("fromPoint = ? and toPoint = ?", cp1, cp2).first();
    				
	    			GisRouteControlPointSequence sequencePoint = new GisRouteControlPointSequence();
	    			
	    			sequencePoint.gisRouteAlignment = this;
	    			sequencePoint.segment = segment;
	    			sequencePoint.sequence = cp2.originalSequence;
	    			
	    			sequencePoint.save();
    			}
    			else
    			{
    				GisRouteControlPointSequence sequencePoint = new GisRouteControlPointSequence();
	    			
	    			sequencePoint.gisRouteAlignment = this;
	    			sequencePoint.controlPoint = cp2;
	    			sequencePoint.sequence = cp2.originalSequence;
	    			
	    			sequencePoint.save();
    			}
        	}
    	}
    	
    	updateGtfsRoute();
    }
    
    public void updateGtfsRoute()
    {
    	LineString gtfsRoute = getCurrentGtfsRoute();
    	
    	if(gtfsRoute != null)
    		gtfsRoute = (LineString)DouglasPeuckerSimplifier.simplify((Geometry)gtfsRoute, 0.00001);
    	
    	
    	this.gtfsShape = gtfsRoute;
    	this.save();
    }
    
    
    public LineString getCurrentGtfsRoute()
    {
    	ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
    	
    	List<GisRouteControlPointSequence> pointSequence = GisRouteControlPointSequence.find("gisRouteAlignment = ? order by sequence", this).fetch();
    	
    	for(GisRouteControlPointSequence ps : pointSequence)
    	{
    	
    		if(ps.segment != null)
			{
				GisRouteSegment segment = GisRouteSegment.findById(ps.segment.id);		
					
				for(Coordinate coord : segment.segment.getCoordinates())
				{
					coords.add(coord);
				}
			}
			else
    		{
    			coords.add(ps.controlPoint.controlPoint.getCoordinate());
    		}
    	}
    	
    	GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
    	
    	if(coords.size() > 1)
    	{
    		LineString geom = geometryFactory.createLineString(coords.toArray(new Coordinate[coords.size()]));
    		
    		if(reverseAlignment)
    			geom = (LineString)geom.reverse();
    		
    		return geom; 
    	}
    	else 
    		return null;
    }
   
}




