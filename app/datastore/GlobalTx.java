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
	
	/** Accounts */
	public BTreeMap<String, Account> accounts;
	
	/** OAuth tokens */
	public BTreeMap<String, OAuthToken> tokens;
	
	/** Route types */
	public BTreeMap<String, RouteType> routeTypes;
	
	/** Snapshots of agency DBs, keyed by agency_id, version */
	public BTreeMap<Tuple2<String, Integer>, Snapshot> snapshots;
	
	public GlobalTx (DB tx) {
		super(tx);
		
		agencies = getMap("agencies");
		accounts = getMap("accounts");
		tokens = getMap("tokens");
		routeTypes = getMap("routeTypes");
		snapshots = getMap("snapshots");
	}
}
