package datastore;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import models.Account;
import models.OAuthToken;
import models.Snapshot;
import models.transit.Agency;
import models.transit.Route;
import models.transit.RouteType;
import models.transit.ScheduleException;
import models.transit.ServiceCalendar;
import models.transit.Stop;
import models.transit.Trip;
import models.transit.TripPattern;

import org.mapdb.Atomic;
import org.mapdb.BTreeKeySerializer;
import org.mapdb.BTreeMap;
import org.mapdb.Bind;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Bind.MapWithModificationListener;
import org.mapdb.Fun;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Serializer;
import org.mapdb.TxMaker;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.protobuf.ServiceException;

import play.Logger;
import play.Play;
import utils.ClassLoaderSerializer;

/**
 * Create a new versioned datastore. A versioned data store handles multiple databases,
 * the global DB and the agency-specific DBs. It handles creating transactions, and saving and restoring
 * snapshots.
 * @author mattwigway
 *
 */
public class VersionedDataStore {
	private static File dataDirectory = new File((String) Play.configuration.get("application.database-directory"));
	private static TxMaker globalTxMaker;
	
	private static Map<String, TxMaker> agencyTxMakers = Maps.newConcurrentMap();
		
	static {
		File globalDataDirectory = new File(dataDirectory, "global");
		globalDataDirectory.mkdirs();
		
		// initialize the global database
		globalTxMaker = DBMaker.newFileDB(new File(globalDataDirectory, "global.db"))
					.mmapFileEnable()
					.compressionEnable()
					.makeTxMaker();
	}
	
	/** Start a transaction in the global database */
	public static GlobalTx getGlobalTx () {
		return new GlobalTx(globalTxMaker.makeTx());
	}
	
	/**
	 * Start a transaction in an agency database. No checking is done to ensure the agency exists;
	 * if it does not you will get a (hopefully) empty DB, unless you've done the same thing previously.
	 */
	public static AgencyTx getAgencyTx (String agencyId) {
		return new AgencyTx(getRawAgencyTx(agencyId));
	}
	
	/**
	 * Get a raw MapDB transaction for the given database. Use at your own risk - doesn't properly handle indexing, etc.
	 * Intended for use primarily with database restore
	 */
	static DB getRawAgencyTx (String agencyId) {
		if (!agencyTxMakers.containsKey(agencyId)) {
			synchronized (agencyTxMakers) {
				if (!agencyTxMakers.containsKey(agencyId)) {
					File path = new File(dataDirectory, agencyId);
					path.mkdirs();
					
					TxMaker agencyTxm = DBMaker.newFileDB(new File(path, "master.db"))
							.mmapFileEnable()
							.compressionEnable()
							.makeTxMaker();
					
					agencyTxMakers.put(agencyId, agencyTxm);
				}
			}
		}
		
		return agencyTxMakers.get(agencyId).makeTx();
	}
	
	/** Take a snapshot of an agency database. The snapshot will be saved in the global database. */
	public static Snapshot takeSnapshot (String agencyId, String name) {
		AgencyTx tx = getAgencyTx(agencyId);
		GlobalTx gtx = getGlobalTx();
		int version = -1;
		DB snapshot = null;
		Snapshot ret = null;
		try {
			version = tx.getNextSnapshotId();
			
			Logger.info("Creating snapshot %s for agency %s", agencyId, version);
			long startTime = System.currentTimeMillis();
			
			ret = new Snapshot(agencyId, version);
			
			if (gtx.snapshots.containsKey(ret.id))
				throw new IllegalStateException("Duplicate snapshot IDs");

			ret.snapshotTime = System.currentTimeMillis();
			ret.name = name;
			ret.current = true;
			
			snapshot = getSnapshotDb(agencyId, version, false);
			
			new SnapshotTx(snapshot).make(tx);
			// for good measure
			snapshot.commit();
			snapshot.close();
			
			gtx.snapshots.put(ret.id, ret);
			gtx.commit();
			tx.commit();
			
			Logger.info("Saving snapshot took %.2f seconds", (System.currentTimeMillis() - startTime) / 1000D);
			
			return ret;
		} catch (Exception e) {
			// clean up
			if (snapshot != null && !snapshot.isClosed())
				snapshot.close();
			
			if (version >= 0) {
				File snapshotDir = getSnapshotDir(agencyId, version);
				
				if (snapshotDir.exists()) {
					for (File file : snapshotDir.listFiles()) {
						file.delete();
					}
				}
			}
			
			// re-throw
			throw e;
		} finally {
			tx.rollbackIfOpen();
			gtx.rollbackIfOpen();
		}
	}
	
	/** restore a snapshot */
	public static void restore (Snapshot s) {
		SnapshotTx tx = new SnapshotTx(getSnapshotDb(s.agencyId, s.version, true));
		try {
			Logger.info("Restoring snapshot %s of agency %s", s.version, s.agencyId);
			long startTime = System.currentTimeMillis();
			tx.restore(s.agencyId);
			Logger.info("Restored snapshot in %.2f seconds", (System.currentTimeMillis() - startTime) / 1000D);
		} finally {
			tx.close();
		}
	}
	
	/** get the directory in which to store a snapshot */
	public static DB getSnapshotDb (String agencyId, int version, boolean readOnly) {
		File thisSnapshotDir = getSnapshotDir(agencyId, version);
		thisSnapshotDir.mkdirs();
		File snapshotFile = new File(thisSnapshotDir, "snapshot_" + version + ".db");
		
		// we don't use transactions for snapshots - makes them faster
		// and smaller.
		// at the end everything gets committed and flushed to disk, so this thread
		// will not complete until everything is done.
		// also, we compress the snapshot databases
		DBMaker maker = DBMaker.newFileDB(snapshotFile)
				.compressionEnable();
				
		if (readOnly)
			maker.readOnly();
		
		return maker.make();
	}
	
	/** get the directory in which a snapshot is stored */
	public static File getSnapshotDir (String agencyId, int version) {
		File agencyDir = new File(dataDirectory, agencyId);
		File snapshotsDir = new File(agencyDir, "snapshots");
		return new File(snapshotsDir, "" + version);
	}
	
	/** Convenience function to check if an agency exists */
	public static boolean agencyExists (String agencyId) {
		GlobalTx tx = getGlobalTx();
		boolean exists = tx.agencies.containsKey(agencyId);
		tx.rollback();
		return exists;
	}
	
	/** A wrapped transaction, so the database just looks like a POJO */
	public static class DatabaseTx {
		/** the database (transaction). subclasses must initialize. */
		protected final DB tx;
		
		/** has this transaction been closed? */
		boolean closed = false;
		
		/** Convenience function to get a map */
		protected final <T1, T2> BTreeMap <T1, T2> getMap (String name) {
			return tx.createTreeMap(name)
					// use java serialization to allow for schema upgrades
					.valueSerializer(new ClassLoaderSerializer())
					.makeOrGet();
		}
		
		/**
		 * Convenience function to get a set. These are used as indices so they use the default serialization;
		 * if we make a schema change we drop and recreate them.
		 */
		protected final <T> NavigableSet <T> getSet (String name) {
			return tx.createTreeSet(name)
					.makeOrGet();
		}
		
		protected DatabaseTx (DB tx) {
			this.tx = tx;
		}
		
		public void commit() {
			tx.commit();
			closed = true;
		}
		
		public void rollback() {
			tx.rollback();
			closed = true;
		}
		
		/** roll this transaction back if it has not been committed or rolled back already */
		public void rollbackIfOpen () {
			if (!closed) rollback();
		}
		
		protected final void finalize () {
			if (!closed) {
				Logger.error("DB transaction left unclosed, this signifies a memory leak!");
				rollback();
			}
		}
	}
}
