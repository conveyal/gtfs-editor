package utils;

import java.util.Map;
import java.util.Set;

import org.mapdb.Bind.MapListener;
import org.mapdb.Bind.MapWithModificationListener;
import org.mapdb.Fun.Function2;

public class BindUtils {
	/**
	 * Index the map keys of a subset of the given map. The set contains all of the keys for which filter returns true.
	 */
	public static <K, V> void subsetIndex (MapWithModificationListener<K, V> map, final Set<K> subset, final Function2<Boolean, K, V> filter) {
		// fill if empty
		if (subset.isEmpty()) {
			for (Map.Entry<K, V> e : map.entrySet()) {
				if (filter.run(e.getKey(), e.getValue())) {
					subset.add(e.getKey());
				}
			}
		}
		
		map.modificationListenerAdd(new MapListener<K, V>() {
			@Override
			public void update(K key, V oldVal, V newVal) {
				// addition
				if (newVal != null && filter.run(key, newVal)) {
					subset.add(key);
				}
				else {
					// doesn't matter if it's present
					subset.remove(key);
				}
			}
		});
	}
	
	/**
	 * Make a histogram where each item can be a member of multiple categories.
	 */
	public static <K, V, C> void multiHistogram (MapWithModificationListener<K, V> map,
			final Map<C, Long> histogram, final Function2<C[], K, V> categories) {
		if (histogram.isEmpty()) {
			for (Map.Entry<K, V> e : map.entrySet()) {
				for (C cat : categories.run(e.getKey(), e.getValue())) {
					if (!histogram.containsKey(cat))
						histogram.put(cat, 1L);
					else
						histogram.put(cat, histogram.get(cat) + 1);
				}
			}
		}
		
		map.modificationListenerAdd(new MapListener<K, V>() {
			@Override
			public void update(K key, V oldVal, V newVal) {
				if (oldVal != null) {
					for (C cat : categories.run(key, oldVal)) {
						histogram.put(cat, histogram.get(cat) - 1);
					}
				}
				
				if (newVal != null) {
					for (C cat : categories.run(key, newVal)) {
						if (!histogram.containsKey(cat))
							histogram.put(cat, 1L);
						else
							histogram.put(cat, histogram.get(cat) + 1);
					}
				}
			}
		});
	}
}
