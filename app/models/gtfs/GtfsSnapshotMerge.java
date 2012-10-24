package models.gtfs;
 
import java.util.*;
import java.io.File;
import java.math.BigInteger;

import javax.persistence.*;

import models.transit.Agency;
import models.transit.Route;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.TripShape;

import org.apache.commons.lang.StringUtils;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import play.Logger;
import play.Play;
import play.db.DB;
import play.db.jpa.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ManyToOne;
import javax.persistence.Persistence;
 


@Entity
public class GtfsSnapshotMerge extends Model {
 
	@Enumerated(EnumType.STRING)
    public GtfsSnapshotMergeStatus status; 
	
    public Date mergeStarted;
    public Date mergeComplete;
    
    public String description;
    
    @ManyToOne
    public GtfsSnapshot snapshot;
   
	
    public GtfsSnapshotMerge(GtfsSnapshot snapshot) {
    	this.status = GtfsSnapshotMergeStatus.INPROGRESS;
    	this.mergeStarted = new Date();
    	this.snapshot = snapshot;
       
    }
    
    public void complete(String description)
    {
    	this.status = GtfsSnapshotMergeStatus.COMPLETED;
    	this.description = description;
    	this.mergeComplete = new Date();
    	this.save();
    }
    
    public void failed(String description)
    {
    	this.em().getTransaction().rollback();

    	this.em().getTransaction().begin();
    	
    	this.em().createNativeQuery("UPDATE gtfssnapshotmerge SET status = ?, mergeComplete = ?, description = ? WHERE id = ?")
    		.setParameter(1, GtfsSnapshotMergeStatus.FAILED.name())
    		.setParameter(2, new Date())
    		.setParameter(3, description)
    		.setParameter(4, this.id)
    		.executeUpdate();
    		
    	this.em().getTransaction().commit();
    }
   
    public List<GtfsSnapshotMergeTask> getTasks()
    {
    	List<GtfsSnapshotMergeTask> tasks = GtfsSnapshotMergeTask.find("merge = ?", this).fetch();
    	
    	return tasks;
    }
    
    

}


