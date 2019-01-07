package structures;

import java.util.*;

/**
 * Implementation of a Union-Find (or Disjoint-Set) data structure.
 *
 * @param <T> The type of value being stored.
 *
 * @author Brian Yao
 */
public class UnionFind<T> {

	// Map elements to the names of the set containing them
	private Map<T, T> names;
	// Map sets to their sizes
	private Map<T, Integer> sizes;

	/**
	 * Initialize a UnionFind structure with the given values. Initially, each
	 * value belongs to a singleton set containing itself. The values are also
	 * assumed to be distinct (otherwise, duplicates are ignored).
	 *
	 * @param values The values to store in this UnionFind.
	 */
	public UnionFind(Collection<T> values) {
		names = new HashMap<>();
		sizes = new HashMap<>();
		for (T value : values) {
			names.put(value, value);
			sizes.put(value, 1);
		}
	}

	/**
	 * Find operation: get the "name" of the set containing the given
	 * value. The name is just another value which represents that set.
	 *
	 * @param value The value to look up.
	 * @return The name of the set containing the given value.
	 */
	public T find(T value) {
		T name = value;
		Set<T> nameSet = new HashSet<>();
		while (!sizes.containsKey(name)) {
			nameSet.add(name);
			name = names.get(name);
		}

		// Update pointers along the way to point to the correct name
		for (T seen : nameSet) {
			names.put(seen, name);
		}

		return name;
	}

	/**
	 * Union operation: combines two sets into one. The parameters must be
	 * elements that could be returned by {@link UnionFind#find(Object)}.
	 *
	 * @param a The name of the first set.
	 * @param b The name of the second set.
	 */
	public void union(T a, T b) {
		if (sizes.get(a) < sizes.get(b)) {
			names.put(a, b);
			sizes.put(b, sizes.get(a) + sizes.get(b));
			sizes.remove(a);
		} else {
			names.put(b, a);
			sizes.put(a, sizes.get(a) + sizes.get(b));
			sizes.remove(b);
		}
	}

}
