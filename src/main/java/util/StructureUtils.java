package util;

import graph.components.Edge;
import graph.components.Node;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import structures.UOPair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class containing data structure-related utility methods.
 *
 * @author Brian Yao
 */
public class StructureUtils {

	/**
	 * Make a shallow copy of a map whose value type is a list. This ensures
	 * that the original lists are not modified when we modify the values
	 * of the shallow copy.
	 *
	 * @param original The original map to copy.
	 * @return The shallow copy.
	 */
	public static <K, V> Map<K, List<V>> shallowCopy(Map<K, List<V>> original) {
		Map<K, List<V>> newMap = new HashMap<>();
		for (Map.Entry<K, List<V>> entry : original.entrySet()) {
			newMap.put(entry.getKey(), new ArrayList<>(entry.getValue()));
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
			throw new IllegalArgumentException("Number of arbitrary elements k must be non-negative and at most " +
												   "the size of the collection");
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
	 * @param collection The collection to retrieve an element from.
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

	/**
	 * Converts a set of Nodes to GBNodes, assuming the nodes are associated
	 * with a context.
	 *
	 * @param nodes The set of plain nodes to convert.
	 * @return The set of corresponding GBNodes.
	 */
	public static Set<GBNode> toGbNodes(Collection<Node> nodes) {
		return nodes.stream().map(Node::getGbNode).collect(Collectors.toSet());
	}

	/**
	 * Converts a map of Edges to GBEdges, assuming the edges are associated
	 * with a context.
	 *
	 * @param edges The set of plain edges to convert.
	 * @return The set of corresponding GBNodes.
	 */
	public static Map<UOPair<GBNode>, List<GBEdge>> toGbEdges(Map<UOPair<Node>, List<Edge>> edges) {
		Map<UOPair<GBNode>, List<GBEdge>> gbEdgeMap = new HashMap<>();
		edges.entrySet().forEach(edgeEntry -> {
			UOPair<Node> oldPair = edgeEntry.getKey();
			UOPair<GBNode> gbPair = new UOPair<>(oldPair.getFirst().getGbNode(), oldPair.getSecond().getGbNode());
			List<GBEdge> gbEdges = edgeEntry.getValue().stream().map(Edge::getGbEdge).collect(Collectors.toList());

			// Fill map
			gbEdgeMap.put(gbPair, gbEdges);
		});

		return gbEdgeMap;
	}

	/**
	 * Converts a set of GBNodes to Nodes.
	 *
	 * @param gbNodes The set of GBNodes to convert.
	 * @return The set of corresponding Nodes.
	 */
	public static Set<Node> toNodes(Collection<GBNode> gbNodes) {
		return gbNodes.stream().map(GBNode::getNode).collect(Collectors.toSet());
	}

	/**
	 * Converts a map of GBEdges to Edges.
	 *
	 * @param gbEdges The set of GBEdges to convert.
	 * @return The set of corresponding Edges.
	 */
	public static Map<UOPair<Node>, List<Edge>> toEdges(Map<UOPair<GBNode>, List<GBEdge>> gbEdges) {
		Map<UOPair<Node>, List<Edge>> edgeMap = new HashMap<>();
		gbEdges.entrySet().forEach(edgeEntry -> {
			UOPair<GBNode> oldPair = edgeEntry.getKey();
			UOPair<Node> nodePair = new UOPair<>(oldPair.getFirst().getNode(), oldPair.getSecond().getNode());
			List<Edge> edges = edgeEntry.getValue().stream().map(GBEdge::getEdge).collect(Collectors.toList());

			// Fill map
			edgeMap.put(nodePair, edges);
		});

		return edgeMap;
	}

}
