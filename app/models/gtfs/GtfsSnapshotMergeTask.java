package models.gtfs;
 
import java.util.*;
import java.io.File;
import javax.persistence.*;

import models.transit.Agency;
import models.transit.Route;
import models.transit.Stop;

import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.PrecisionModel;

import play.Play;
import play.db.DB;
import play.db.jpa.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
 


@Entity
public class GtfsSnapshotMergeTask extends Model {
 
	@Enumerated(EnumType.STRING)
    public GtfsSnapshotMergeTaskStatus status;
    public String description;
    
    public Date taskStarted;
    public Date taskCompleted;
    
    @ManyToOne
    public GtfsSnapshotMerge merge;
   
	
    public GtfsSnapshotMergeTask(GtfsSnapshotMerge merge) {
    	this.merge = merge;
    }
    
    public void startTask()
    {
    	this.taskStarted = new Date();
    	this.save();
    }
    
    public void completeTask(String description, GtfsSnapshotMergeTaskStatus status)
    {
    	this.description = description;
    	this.status = status;
    	this.taskStarted = new Date();
    	
    	this.save();
    }

}

