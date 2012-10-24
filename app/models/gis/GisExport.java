package models.gis;
 
import java.io.File;
import java.util.*;

import javax.persistence.*;

import jobs.ProcessGisExport;

import models.gtfs.GtfsSnapshotExportCalendars;
import models.gtfs.GtfsSnapshotExportStatus;
import models.gtfs.GtfsSnapshotSource;
import models.transit.Agency;

import play.db.jpa.*;
 
@Entity
public class GisExport extends Model {
 
	@ManyToMany
    public List<Agency> agencies;

	@Enumerated(EnumType.STRING)
	public GisUploadType type;
	
	@Enumerated(EnumType.STRING)
	public GisExportStatus status;
	
    public String description;
    public Date creationDate;
    
   
    public GisExport(List<Agency> agencies, GisUploadType type, String descipriton) 
    {
    	
    	this.agencies = agencies;
    	this.type = type;
    	this.description = descipriton;
    
    	this.status = GisExportStatus.PROCESSING;
    	
    	this.creationDate =  new Date();
    	
    	this.save();
    }
    
    public String getFilename()
    {
    	return "gis_" + this.id + ".zip";
    }
    
  
}