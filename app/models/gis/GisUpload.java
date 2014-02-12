package models.gis;
 
import java.io.File;
import java.util.*;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import jobs.ProcessGisUpload;
import models.transit.Agency;
import play.db.jpa.*;
 
@JsonIgnoreProperties({"entityId", "persistent"})
@Entity
public class GisUpload extends Model {
 
	@ManyToOne
    public Agency agency;

	@Enumerated(EnumType.STRING)
	public GisUploadType type;
	
	@Enumerated(EnumType.STRING)
	public GisUploadStatus status;
	
    public String description;
    public Date creationDate;
    
    public String fieldId;
    public String fieldName;
    public String fieldType;
    public String fieldDescription;
    
    public GisUpload(Agency agency, Date creationDate, GisUploadType type, String description)
    {
    	this.agency = agency;
    	this.type = type;
    	this.description = description;
    	this.creationDate = creationDate;
    	
    	this.type = type;
    	
    	this.save();
    }
    
    public Long routeCount()
    {
    	return GisRoute.count("gisUpload = ?", this);
    }
    
    public Long stopCount()
    {
    	return GisStop.count("gisUpload = ?", this);
    }
    
    public void processFields()
    {
    	ProcessGisUpload uploadJob = new ProcessGisUpload(this.id);
        
        uploadJob.doJob();   
    }
}