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
public class GisRouteControlPointSequence extends Model {
	
	@ManyToOne
    public GisRouteAlignment gisRouteAlignment;

	@ManyToOne
    public GisRouteControlPoint controlPoint;
	
	@ManyToOne
    public GisRouteSegment segment;
	
	public Integer sequence;
   
}


