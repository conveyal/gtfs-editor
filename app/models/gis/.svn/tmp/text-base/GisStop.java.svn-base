package models.gis;



import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

import models.transit.Agency;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Type;

import play.db.jpa.Model;

@Entity
public class GisStop extends Model {
	
	@ManyToOne
    public GisUpload gisUpload;
	
    @ManyToOne
    public Agency agency;
    
    public String oid;
	
	public String stopId;
	public String stopName;
    public String description;
    
    @Type(type = "org.hibernatespatial.GeometryUserType") 
    public Point shape;  
}