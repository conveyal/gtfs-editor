package models.gtfs;
 
import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
 


@Entity
public class GtfsSnapshot extends Model {
 
    public String description;
    public Date creationDate;
    
    public Integer agencyCount;
    public Integer routeCount;
    public Integer stopCount;
    public Integer tripCount;
    
    @Enumerated(EnumType.STRING)
    public GtfsSnapshotSource source; 
    
    public GtfsSnapshot(String description, Date creationDate, GtfsSnapshotSource source) {
        this.description = description;
        this.creationDate = creationDate;
        this.source = source;
    }
    
    public String getFilename()
    {
    	return "gtfs_" + this.id + ".zip";
    }
    
    
    public Boolean alreadyMerged()
    {
    	Long matchingSnapshots = GtfsSnapshotMerge.count("snapshot = ? AND status = ?", this, GtfsSnapshotMergeStatus.COMPLETED);
    	
    	if(matchingSnapshots > 0 )
    		return true;
    	else
    		return false;
    }
    
    
    public Boolean mergeInProgress()
    {
    	Long matchingSnapshots = GtfsSnapshotMerge.count("snapshot = ? AND status = ?", this, GtfsSnapshotMergeStatus.INPROGRESS);
    	
    	if(matchingSnapshots > 0 )
    		return true;
    	else
    		return false;
    }
    
    public List<GtfsSnapshotMerge> getMerges()
    {
    	List<GtfsSnapshotMerge> merges = GtfsSnapshotMerge.find("snapshot = ?", this).fetch();
    	
    	return merges;
    }

}