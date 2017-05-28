package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A class containing data structure-related utility methods.
 * 
 * @author Brian
 */
public class StructureUtils {

	/**
	 * Make a shallow copy of a map whose value type is a list.
	 * 
	 * @param original The original map to copy.
	 * @return The shallow copy.
	 */
	public static <K, V> Map<K, List<V>> shallowCopy(Map<K, List<V>> original) {
		Map<K, List<V>> newMap = new HashMap<K, List<V>>();
		for (Map.Entry<K, List<V>> entry : original.entrySet()) {
			newMap.put(entry.getKey(), new ArrayList<V>(entry.getValue()));
		}
		return newMap;
	}
	
	/**
	 * Return an arbitrary element from a collection.
	 * 
	 * @param collection The collection to retrieve an element from.
	 * @return An arbitrary element of the collection.
	 */
	public static <E> E arbitraryElement(Collection<E> collection) {
		if (collection.isEmpty()) {
			return null;
		}
		return collection.iterator().next();
	}
	
	/**
	 * Return k arbitrary elements from a set.
	 * 
	 * @param set The set to retrieve elements from.
	 * @return A set containing k arbitrary elements of the given set.
	 */
	public static <E> Set<E> arbitraryKElements(Collection<E> set, int k) {
		if (k > set.size() || k < 0) {
			throw new IllegalArgumentException("Number of arbitrary elements k must be non-negative and at most the size of the collection");
		}
		
		Set<E> elements = new HashSet<>();
		Iterator<E> setIterator = set.iterator();
		int count = 0;
		while (count++ < k) {
			elements.add(setIterator.next());
		}
		return elements;
	}
	
	/**
	 * Return a pseudorandom element from a collection. This runs in linear
	 * time of the size of the collection.
	 * 
	 * @param set The collection to retrieve an element from.
	 * @return A random element of the provided collection.
	 */
	public static <E> E randomElement(Collection<E> collection) {
		if (collection.isEmpty()) {
			return null;
		}
		int index = (int) (Math.random() * collection.size());
		int currentIndex = 0;
		Iterator<E> iterator = collection.iterator();
		while (currentIndex++ < index) {
			iterator.next();
		}
		return iterator.next();
	}
	
}
