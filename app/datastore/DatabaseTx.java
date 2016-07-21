package datastore;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DB.BTreeMapMaker;
import org.mapdb.Fun.Tuple2;
import play.Logger;
import utils.ClassLoaderSerializer;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableSet;

/** A wrapped transaction, so the database just looks like a POJO */
public class DatabaseTx {
	/** the database (transaction). subclasses must initialize. */
	protected final DB tx;
	
	/** has this transaction been closed? */
	boolean closed = false;

	/** is this transaction read-only? */
	protected boolean readOnly;
	
	/** Convenience function to get a map */
	protected final <T1, T2> BTreeMap <T1, T2> getMap (String name) {
		try {
			return getMapMaker(tx, name)
					.makeOrGet();
		} catch (UnsupportedOperationException e) {
			// read-only data store
			return null;
		}
	}
	
	/** get a map maker, that can then be further modified */
	private static final BTreeMapMaker getMapMaker (DB tx, String name) {
		return tx.createTreeMap(name)
				// use java serialization to allow for schema upgrades
				.valueSerializer(new ClassLoaderSerializer());
	}
	
	/**
	 * Convenience function to get a set. These are used as indices so they use the default serialization;
	 * if we make a schema change we drop and recreate them.
	 */
	protected final <T> NavigableSet <T> getSet (String name) {
		try {
			return tx.createTreeSet(name)
					.makeOrGet();
		} catch (UnsupportedOperationException e) {
			// read-only data store
			return null;
		}
	}
	
	protected DatabaseTx (DB tx) {
		this.tx = tx;
	}
	
	public void commit() {
		try {
			tx.commit();
		} catch (UnsupportedOperationException e) {
			// probably read only, but warn
			Logger.warn("Rollback failed; if this is a read-only database this is not unexpected");
		}		closed = true;
	}
	
	public void rollback() {
		try {
			tx.rollback();
		} catch (UnsupportedOperationException e) {
			// probably read only, but warn
			Logger.warn("Rollback failed; if this is a read-only database this is not unexpected");
		}
		closed = true;
	}
	
	/** roll this transaction back if it has not been committed or rolled back already */
	public void rollbackIfOpen () {
		if (!closed) rollback();
	}
	
	/** efficiently copy a btreemap into this database */
	protected <K, V> int pump(String mapName, BTreeMap<K, V> source) {
		return pump(tx, mapName, source);
	}
	
	/** from a descending order iterator fill a new map in the specified database */
	protected static <K, V> int pump(DB tx, String mapName, Iterator<Tuple2<K, V>> pumpSource) {
		if (!pumpSource.hasNext())
			return 0;
		
		return getMapMaker(tx, mapName)
				.pumpSource(pumpSource)
				.make()
				.size();
	}
	
	/** efficiently create a BTreeMap in the specified database from another BTreeMap */
	protected static <K, V> int pump (DB tx, String mapName, BTreeMap<K, V> source) {
		if (source.size() == 0)
			return 0;
		
		return pump(tx, mapName, pumpSourceForMap(source));
	}
	
	/** get a pump source from a map */
	protected static <K, V> Iterator<Tuple2<K, V>> pumpSourceForMap(BTreeMap source) {
		Iterator<Entry<K, V>> values = source.descendingMap().entrySet().iterator();
		Iterator<Tuple2<K, V>> valueTuples = Iterators.transform(values, new Function<Entry<K, V>, Tuple2<K, V>> () {
			@Override
			public Tuple2<K, V> apply(Entry<K, V> input) {
				return new Tuple2<K, V>(input.getKey(), input.getValue());
			}			
		});
		
		return valueTuples;
	}
	
	protected final void finalize () {
		if (!closed) {
			Logger.error("DB transaction left unclosed, this signifies a memory leak!");
			rollback();
		}
	}
}