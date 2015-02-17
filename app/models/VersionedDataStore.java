package models;

import java.io.File;
import java.util.Map;
import java.util.NavigableSet;

import models.transit.Agency;
import models.transit.Route;
import models.transit.RouteType;
import models.transit.ScheduleException;
import models.transit.ServiceCalendar;
import models.transit.Stop;
import models.transit.Trip;
import models.transit.TripPattern;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.Bind;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Bind.MapWithModificationListener;
import org.mapdb.Fun;
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
		protected final <T1, T2> MapWithModificationListener<T1, T2> getMap (String name) {
			return tx.createTreeMap(name)
					// use java serialization to allow for schema upgrades
					.valueSerializer(Serializer.JAVA)
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

		public MapWithModificationListener<String, Agency> agencies;
		
		/** Stops for all agencies, keys are agency_id, stop_id */
		public MapWithModificationListener<Tuple2<String, String>, Stop> stops;
		
		/** Accounts */
		public MapWithModificationListener<String, Account> accounts;
		
		/** OAuth tokens */
		public MapWithModificationListener<String, OAuthToken> tokens;
		
		/** Route types */
		public MapWithModificationListener<String, RouteType> routeTypes;
		
		/**
		 * Spatial index of stops. Set<Tuple2<Tuple2<Lon, Lat>, stop ID>>
		 * This is not a true spatial index, but should be sufficiently efficient for our purposes.
		 * Jan Kotek describes this approach here, albeit in Czech: https://groups.google.com/forum/#!msg/mapdb/ADgSgnXzkk8/Q8J9rWAWXyMJ
		 */
		public NavigableSet<Tuple2<Tuple2<Double, Double>, Tuple2<String, String>>> stopsGix;
		
		public GlobalTx (DB tx) {
			super(tx);
			
			agencies = getMap("agencies");
			stops = getMap("stops");
			accounts = getMap("accounts");
			tokens = getMap("tokens");
			routeTypes = getMap("routeTypes");
			
			// secondary indices
			stopsGix = getSet("stopsGix");
			
			Bind.secondaryKeys(stops, stopsGix, new Fun.Function2<Tuple2<Double, Double>[], Tuple2<String, String>, Stop> () {

				@Override
				public Tuple2<Double, Double>[] run(
						Tuple2<String, String> stopId, Stop stop) {
					return new Tuple2[] { new Tuple2(stop.location.getX(), stop.location.getY()) }; 
				}
								
			});
		}
	}
	
	/** a transaction in an agency database */
	public static class AgencyTx extends DatabaseTx {
		public MapWithModificationListener<String, TripPattern> tripPatterns;
		public MapWithModificationListener<String, Route> routes;
		public MapWithModificationListener<String, Trip> trips;
		public MapWithModificationListener<String, ServiceCalendar> calendars;
		public MapWithModificationListener<String, ScheduleException> exceptions;
		
		// secondary indices
		
		/** Set containing tuples <Route ID, Trip ID> */
		public NavigableSet<Tuple2<String, String>> tripsByRoute;
		
		/** <route ID, trip pattern ID> */
		public NavigableSet<Tuple2<String, String>> tripPatternsByRoute;
		
		public AgencyTx (DB tx) {
			super(tx);
			
			tripPatterns = getMap("tripPatterns");
			routes = getMap("routes");
			trips = getMap("trips");
			calendars = getMap("calendars");
			exceptions = getMap("exceptions");
			
			// build secondary indices
			// we store indices in the mapdb not because we care about persistence, but because then they
			// will be managed within the context of MapDB transactions
			tripsByRoute = getSet("tripsByRoute");
			
			// bind the trips to the routes
			Bind.secondaryKeys(trips, tripsByRoute, new Fun.Function2<String[], String, Trip>() {

				@Override
				public String[] run(String tripId, Trip trip) {
					// TODO Auto-generated method stub
					return new String[] { trip.routeId };
				}
			});
			
			tripPatternsByRoute = getSet("tripPatternsByRoute");
			Bind.secondaryKeys(tripPatterns, tripPatternsByRoute, new Fun.Function2<String[], String, TripPattern>() {

				@Override
				public String[] run(String tripId, TripPattern trip) {
					// TODO Auto-generated method stub
					return new String[] { trip.routeId };
				}
			});
			
		}
	}
}
