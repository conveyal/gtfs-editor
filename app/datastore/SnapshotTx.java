package datastore;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import models.transit.Route;
import models.transit.ScheduleException;
import models.transit.Trip;
import models.transit.TripPattern;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.Fun.Function1;
import org.mapdb.Fun.Tuple2;

import play.Logger;

import com.conveyal.gtfs.model.Calendar;
import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/** represents a snapshot database. It's generally not actually a transaction, but rather writing to a transactionless db, for speed */
public class SnapshotTx extends DatabaseTx {	
	/** create a snapshot database */
	public SnapshotTx(DB tx) {
		super(tx);
	}
	
	/** make the snapshot */
	public void make (AgencyTx master) {
		// make sure it's empty
		if (tx.getAll().size() != 0)
			throw new IllegalStateException("Cannot snapshot into non-empty db");
		
		int rcount = pump("routes", (BTreeMap) master.routes);
		Logger.info("Snapshotted %s routes", rcount);
		int ccount = pump("calendars", (BTreeMap) master.calendars);
		Logger.info("Snapshotted %s calendars", ccount);
		int ecount = pump("exceptions", (BTreeMap) master.exceptions);
		Logger.info("Snapshotted %s schedule exceptions", ecount);
		int tpcount = pump("tripPatterns", (BTreeMap) master.tripPatterns);
		Logger.info("Snapshotted %s patterns", tpcount);
		int tcount = pump("trips", (BTreeMap) master.trips);
		Logger.info("Snapshotted %s trips", tcount);
		this.commit();
		Logger.info("Snapshot finished");
	}
	
	/** restore into an agency. this will OVERWRITE ALL DATA IN THE AGENCY's MASTER BRANCH. */
	public void restore (String agencyId) {
		DB target = VersionedDataStore.getRawAgencyTx(agencyId);
		
		// clear out the agency tx, including all indices, etc.
		for (String obj : target.tx.getAll().keySet()) {
			if (obj.equals("snapshotVersion"))
				// except don't overwrite the counter that keeps track of snapshot versions
				continue;
			else
				target.tx.delete(obj);
		}
		
		int rcount = target.pump("routes", (BTreeMap) this.<String, Route>getMap("routes"));
		Logger.info("Restored %s routes", rcount);
		int ccount = target.pump("calendars", (BTreeMap) this.<String, Calendar>getMap("calendars"));
		Logger.info("Restored %s schedule exceptions", ccount);
		int ecount = target.pump("exceptions", (BTreeMap) this.<String, ScheduleException>getMap("exceptions"));
		Logger.info("Restored %s schedule exceptions", ecount);
		int pcount = target.pump("tripPatterns", (BTreeMap) this.<String, TripPattern>getMap("tripPatterns"));
		Logger.info("Restored %s patterns", pcount);
		int tcount = target.pump("trips", (BTreeMap) this.<String, Trip>getMap("trips"));
		Logger.info("Restored %s trips", tcount);
		
		Logger.info("Rebuilding indices, this could take a little while . . . ");
		target.buildSecondaryIndices();
		Logger.info("done.");
		
		target.commit();
	}
}
