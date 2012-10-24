package models.gtfs;
 
import java.util.*;

import javax.persistence.*;

import models.transit.Agency;

import play.db.jpa.*;
 


@Entity
public class GtfsSnapshotExport extends Model {

	@Enumerated(EnumType.STRING)
    public GtfsSnapshotExportStatus status;
	
	@Enumerated(EnumType.STRING)
    public GtfsSnapshotExportCalendars calendars;
	
	@Enumerated(EnumType.STRING)
    public GtfsSnapshotSource source;
	
	public String description;
    
    @ManyToMany
    public List<Agency> agencies;
	
	public Date mergeStarted;
    public Date mergeComplete;
    
    
    public GtfsSnapshotExport(List<Agency> agencies, GtfsSnapshotExportCalendars calendars, String descipriton) {
    	
    	this.agencies = agencies;
    	this.calendars = calendars;
    	this.description = descipriton;
    	
    	this.description = descipriton;
    	
    	this.source = GtfsSnapshotSource.MANUAL_EXPORT;
    	this.status = GtfsSnapshotExportStatus.INPROGRESS;
    	
    	this.mergeStarted =  new Date();
    	
    	this.save();
    }
    
    public String getDirectory()
    {
    	return "gtfs_" + this.id;
    }
    
    public String getZipFilename()
    {
    	return "gtfs_" + this.id + ".zip";
    }
    

}