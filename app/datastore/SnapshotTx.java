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
		int scount = pump("stops", (BTreeMap) master.stops);
		Logger.info("Snapshotted %s stops", scount);
		this.commit();
		Logger.info("Snapshot finished");
	}
	
	/** restore into an agency. this will OVERWRITE ALL DATA IN THE AGENCY's MASTER BRANCH. */
	public void restore (String agencyId) {
		DB targetTx = VersionedDataStore.getRawAgencyTx(agencyId);

		for (String obj : targetTx.getAll().keySet()) {
			if (obj.equals("snapshotVersion"))
				// except don't overwrite the counter that keeps track of snapshot versions
				continue;
			else
				targetTx.delete(obj);
		}
		
		int rcount, ccount, ecount, pcount, tcount, scount;
		
		if (tx.exists("routes"))
			rcount = pump(targetTx, "routes", (BTreeMap) this.<String, Route>getMap("routes"));
		else
			rcount = 0;
		Logger.info("Restored %s routes", rcount);
		
		if (tx.exists("calendars"))
			ccount = pump(targetTx, "calendars", (BTreeMap) this.<String, Calendar>getMap("calendars"));
		else
			ccount = 0;
		Logger.info("Restored %s calendars", ccount);
		
		if (tx.exists("exceptions"))
			ecount = pump(targetTx, "exceptions", (BTreeMap) this.<String, ScheduleException>getMap("exceptions"));
		else
			ecount = 0;
		Logger.info("Restored %s schedule exceptions", ecount);
		
		if (tx.exists("tripPatterns"))
			pcount = pump(targetTx, "tripPatterns", (BTreeMap) this.<String, TripPattern>getMap("tripPatterns"));
		else
			pcount = 0;
		Logger.info("Restored %s patterns", pcount);
		
		if (tx.exists("trips"))
			tcount = pump(targetTx, "trips", (BTreeMap) this.<String, Trip>getMap("trips"));
		else
			tcount = 0;
		Logger.info("Restored %s trips", tcount);
		
		if (tx.exists("stops"))
			scount = pump(targetTx, "stops", (BTreeMap) this.<String, Trip>getMap("stops"));
		else
			scount = 0;
		Logger.info("Restored %s stops", scount);
		
		// make an agencytx to build indices
		Logger.info("Rebuilding indices, this could take a little while . . . ");
		AgencyTx atx = new AgencyTx(targetTx);
		Logger.info("done.");
		
		atx.commit();
	}
	
	/** close the underlying data store */
	public void close () {
		tx.close();
		closed = true;
	}
}
