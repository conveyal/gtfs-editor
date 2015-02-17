package models;

import java.io.File;
import java.util.Map;

import models.transit.Agency;
import models.transit.Route;
import models.transit.RouteType;
import models.transit.ScheduleException;
import models.transit.ServiceCalendar;
import models.transit.Stop;
import models.transit.Trip;
import models.transit.TripPattern;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Fun.Tuple2;
import org.mapdb.Serializer;
import org.mapdb.TxMaker;

import com.google.common.collect.Maps;

import play.Logger;
import play.Play;

/**
 * Create a new versioned datastore. A versioned data store handles multiple databases,
 * the global DB and the agency-specific DBs. It handles creating transactions, and saving and restoring
 * snapshots.
 * @author mattwigway
 *
 */
public class VersionedDataStore {
	private static File dataDirectory = new File((String) Play.configuration.get("database-directory"));
	private static TxMaker globalTxMaker;
	
	private static Map<String, TxMaker> agencyTxMakers = Maps.newConcurrentMap();
	
	static {
		File globalDataDirectory = new File(dataDirectory, "global");
		globalDataDirectory.mkdirs();
		
		// initialize the global database
		globalTxMaker = DBMaker.newFileDB(globalDataDirectory)
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
		// should this check if the agency exists?
		// that would add additional overhead.
		if (!agencyTxMakers.containsKey(agencyId)) {
			synchronized (agencyTxMakers) {
				if (!agencyTxMakers.containsKey(agencyId)) {
					File path = new File(dataDirectory, agencyId);
					path.mkdirs();
					
					TxMaker agencyTxm = DBMaker.newFileDB(path)
							.mmapFileEnable()
							.compressionEnable()
							.makeTxMaker();
					
					agencyTxMakers.put(agencyId, agencyTxm);
				}
			}
		}
		
		return new AgencyTx(agencyTxMakers.get(agencyId).makeTx());
	}
	
	/** A wrapped transaction, so the database just looks like a POJO */
	public static abstract class DatabaseTx {
		/** the database (transaction). subclasses must initialize. */
		protected final DB tx;
		
		/** Convenience function to get a map */
		protected final <T1, T2> Map<T1, T2> getMap (String name) {
			return tx.createTreeMap(name)
					// use java serialization to allow for schema upgrades
					.valueSerializer(Serializer.JAVA)
					.makeOrGet();
		}
		
		protected DatabaseTx (DB tx) {
			this.tx = tx;
		}
		
		public void commit() {
			tx.commit();
		}
		
		public void rollback() {
			tx.rollback();
		}
		
		protected final void finalize () {
			Logger.error("DB transaction left unclosed, this signifies a memory leak!");
			rollback();
		}
	}
	
	/** a transaction in the global database */
	public static class GlobalTx extends DatabaseTx {

		public Map<String, Agency> agencies;
		
		/** Stops for all agencies, keys are agency_id, stop_id */
		public Map<Tuple2<String, String>, Stop> stops;
		
		/** Accounts */
		public Map<String, Account> accounts;
		
		/** OAuth tokens */
		public Map<String, OAuthToken> tokens;
		
		/** Route types */
		public Map<String, RouteType> routeTypes;
		
		public GlobalTx (DB tx) {
			super(tx);
			
			agencies = getMap("agencies");
			stops = getMap("stops");
			accounts = getMap("accounts");
			tokens = getMap("tokens");
			routeTypes = getMap("routeTypes");
		}
	}
	
	/** a transaction in an agency database */
	public static class AgencyTx extends DatabaseTx {
		public Map<String, TripPattern> tripPatterns;
		public Map<String, Route> routes;
		public Map<String, Trip> trips;
		public Map<String, ServiceCalendar> calendars;
		public Map<String, ScheduleException> exceptions;
		
		public AgencyTx (DB tx) {
			super(tx);
			
			tripPatterns = getMap("tripPatterns");
			routes = getMap("routes");
			trips = getMap("trips");
			calendars = getMap("calendars");
			exceptions = getMap("exceptions");
		}
	}
}
