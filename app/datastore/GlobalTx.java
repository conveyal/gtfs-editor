package datastore;

import java.util.NavigableSet;

import models.Account;
import models.OAuthToken;
import models.Snapshot;
import models.transit.Agency;
import models.transit.RouteType;
import models.transit.Stop;

import org.mapdb.BTreeMap;
import org.mapdb.Bind;
import org.mapdb.Bind.MapWithModificationListener;
import org.mapdb.DB;
import org.mapdb.Fun.Function2;
import org.mapdb.Fun.Tuple2;

/** a transaction in the global database */
public class GlobalTx extends DatabaseTx {
	public BTreeMap<String, Agency> agencies;
	
	/** Stops for all agencies, keys are agency_id, stop_id */
	public BTreeMap<Tuple2<String, String>, Stop> stops;
	
	/** Accounts */
	public BTreeMap<String, Account> accounts;
	
	/** OAuth tokens */
	public BTreeMap<String, OAuthToken> tokens;
	
	/** Route types */
	public BTreeMap<String, RouteType> routeTypes;
	
	/** Snapshots of agency DBs, keyed by agency_id, version */
	public BTreeMap<Tuple2<String, Integer>, Snapshot> snapshots;
	
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
		snapshots = getMap("snapshots");
		
		// secondary indices
		stopsGix = getSet("stopsGix");
		
		Bind.secondaryKeys(stops, stopsGix, new Function2<Tuple2<Double, Double>[], Tuple2<String, String>, Stop> () {

			@Override
			public Tuple2<Double, Double>[] run(
					Tuple2<String, String> stopId, Stop stop) {
				return new Tuple2[] { new Tuple2(stop.location.getX(), stop.location.getY()) }; 
			}
							
		});
	}
}
