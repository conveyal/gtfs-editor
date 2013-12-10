package models.gtfs;
 
import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
 


@Entity
public class GtfsSnapshotValidation extends Model {
 
	@ManyToOne
    public GtfsSnapshot snapshot;
	
	@Enumerated(EnumType.STRING)
    public GtfsSnapshotValidationStatus status;
	
	public String validationDesciption;
    
    public GtfsSnapshotValidation() {
        
    }

}