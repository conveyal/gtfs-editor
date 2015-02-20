package jobs;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;

import com.mchange.v2.c3p0.impl.DbAuth;

import models.transit.Agency;
import models.transit.Route;
import models.transit.ServiceCalendar;
import models.transit.Stop;
import models.transit.StopTime;
import models.transit.TripPattern;
import models.transit.TripPatternStop;
import models.transit.Trip;

import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

public class ProcessGtfsSnapshotUpload extends Job {
/*
	private Long _gtfsSnapshotMergeId;

	private Map<String, BigInteger> agencyIdMap = new HashMap<String, BigInteger>();
	
	public ProcessGtfsSnapshotUpload(Long gtfsSnapshotMergeId) {
		this._gtfsSnapshotMergeId = gtfsSnapshotMergeId;
	}
	
	public void doJob() {
		
		GtfsSnapshotMerge snapshotMerge = null;
		while(snapshotMerge == null)
		{
			snapshotMerge = GtfsSnapshotMerge.findById(this._gtfsSnapshotMergeId);
			Logger.warn("Waiting for snapshotMerge to save...");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		GtfsReader reader = new GtfsReader();
    	GtfsDaoImpl store = new GtfsDaoImpl();
    	
    	Long agencyCount = new Long(0);
    	
    	try {
    		
    		File gtfsFile = new File(Play.configuration.getProperty("application.publicGtfsDataDirectory"), snapshotMerge.snapshot.getFilename());
    		
    		reader.setInputLocation(gtfsFile);
        	reader.setEntityStore(store);
        	reader.run();
    	    	
        	Logger.info("GtfsImporter: listing agencies...");
        	
	    	for (org.onebusaway.gtfs.model.Agency gtfsAgency : reader.getAgencies()) {
	    		 
	    		GtfsAgency agency = new GtfsAgency(gtfsAgency);
	    		agency.snapshot = snapshotMerge.snapshot; 
	    		agency.save();
	    
	    	}
	    	
	    	snapshotMerge.snapshot.agencyCount = store.getAllAgencies().size();
	    	snapshotMerge.snapshot.routeCount = store.getAllRoutes().size();
	    	snapshotMerge.snapshot.stopCount = store.getAllStops().size();
	    	snapshotMerge.snapshot.tripCount = store.getAllTrips().size();
	    	
	    	snapshotMerge.snapshot.save();
	    	
    	}
    	catch (Exception e) {
    		
        	Logger.error(e.toString()); 
        	
        	snapshotMerge.failed(e.toString());
    	}
	}*/
}

