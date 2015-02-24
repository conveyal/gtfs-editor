package datastore;

import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Map.Entry;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DB.BTreeMapMaker;
import org.mapdb.Fun.Tuple2;
import org.mapdb.DBMaker;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import play.Logger;
import utils.ClassLoaderSerializer;

/** A wrapped transaction, so the database just looks like a POJO */
public class DatabaseTx {
	/** the database (transaction). subclasses must initialize. */
	protected final DB tx;
	
	/** has this transaction been closed? */
	boolean closed = false;
	
	/** Convenience function to get a map */
	protected final <T1, T2> BTreeMap <T1, T2> getMap (String name) {
		return getMapMaker(name)
				.makeOrGet();
	}
	
	/** get a map maker, that can then be further modified */
	protected final BTreeMapMaker getMapMaker (String name) {
		return tx.createTreeMap(name)
				// use java serialization to allow for schema upgrades
				.valueSerializer(new ClassLoaderSerializer());
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
	
	/** efficiently create a BTreeMap from another BTreeMap */
	protected <K, V> int pump(String mapName, BTreeMap<K, V> source) {
		if (source.size() == 0)
			return 0;
		
		Iterator<Entry<K, V>> values = source.descendingMap().entrySet().iterator();
		Iterator<Tuple2<K, V>> valueTuples = Iterators.transform(values, new Function<Entry<K, V>, Tuple2<K, V>> () {
			@Override
			public Tuple2<K, V> apply(Entry<K, V> input) {
				return new Tuple2<K, V>(input.getKey(), input.getValue());
			}			
		});
		
		return getMapMaker(mapName)
			.pumpSource(valueTuples)
			.make()
			.size();
	}
	
	protected final void finalize () {
		if (!closed) {
			Logger.error("DB transaction left unclosed, this signifies a memory leak!");
			rollback();
		}
	}
}