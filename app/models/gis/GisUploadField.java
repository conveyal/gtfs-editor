package models.gis;
 
import java.io.File;
import java.util.*;
import javax.persistence.*;

import models.transit.Agency;

import play.db.jpa.*;
 
@Entity
public class GisUploadField extends Model {
 
	@ManyToOne
    public GisUpload gisUpload;
	
	public Long fieldPosition;
	public String fieldName;
	public String fieldType;
}