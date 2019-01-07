package structures;

import graph.components.Edge;
import graph.components.Node;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Per-node data for maintaining an adjacency list.
 *
 * @author Brian Yao
 */
public class AdjListData {

	// A map of undirected edges sharing this node as an endpoint.
	// The map's key is the endpoint of the edge which is not this node.
	// The value of the map is a set of edges.
	@Getter
	private Map<Node, Set<Edge>> undirectedEdges;

	// A map of directed edges leaving this node.
	// The map's key is the endpoint of the edge which is not this node.
	// The value of the map is a set of edges.
	@Getter
	private Map<Node, Set<Edge>> outgoingDirectedEdges;

	// Get the map of directed edges entering this node.
	// The map's key is the endpoint of the edge which is not this node.
	// The value of the map is a set of edges.
	@Getter
	private Map<Node, Set<Edge>> incomingDirectedEdges;

	// The set of ALL self edges attached to this node, including both
	// undirected and directed ones.
	@Getter
	private Set<Edge> selfEdges;

	private Node node;

	public AdjListData(Node n) {
		node = n;
		undirectedEdges = new HashMap<>();
		outgoingDirectedEdges = new HashMap<>();
		incomingDirectedEdges = new HashMap<>();
		selfEdges = new HashSet<>();
	}

	/**
	 * @param e The edge to check.
	 * @return true iff this edge exists in this data.
	 */
	public boolean hasEdge(Edge e) {
		if (!e.hasEndpoint(node)) {
			return false;
		}

		Node other = e.getOtherEndpoint(node);
		if (node == other) {
			return selfEdges.contains(e);
		} else if (!e.isDirected()) {
			return undirectedEdges.get(other) != null && undirectedEdges.get(other).contains(e);
		} else if (e.getFirstEnd() == node) {
			return outgoingDirectedEdges.get(other) != null && outgoingDirectedEdges.get(other).contains(e);
		} else {
			return incomingDirectedEdges.get(other) != null && incomingDirectedEdges.get(other).contains(e);
		}
	}

	/**
	 * Add an edge to this data.
	 *
	 * @param e The edge to add.
	 */
	public void addEdge(Edge e) {
		if (this.hasEdge(e)) {
			throw new IllegalArgumentException("Cannot add an edge to this node's metadata more than once.");
		}

		Node other = e.getOtherEndpoint(node);
		if (node == other) {
			// Self edge case
			selfEdges.add(e);
			return;
		}

		if (!e.isDirected()) {
			undirectedEdges.computeIfAbsent(other, $ -> new HashSet<>());
			undirectedEdges.get(other).add(e);
		} else {
			if (e.getFirstEnd() == node) {
				outgoingDirectedEdges.computeIfAbsent(other, $ -> new HashSet<>());
				outgoingDirectedEdges.get(other).add(e);
			} else if (e.getSecondEnd() == node) {
				incomingDirectedEdges.computeIfAbsent(other, $ -> new HashSet<>());
				incomingDirectedEdges.get(other).add(e);
			}
		}
	}

	/**
	 * Remove an edge from this data.
	 *
	 * @param e The edge to remove.
	 */
	public void removeEdge(Edge e) {
		if (!this.hasEdge(e)) {
			throw new IllegalArgumentException("Cannot remove an edge which does not exist in this node's metadata.");
		}

		Node other = e.getOtherEndpoint(node);
		if (node == other) {
			// Self edge case
			selfEdges.remove(e);
			return;
		}

		if (!e.isDirected()) {
			undirectedEdges.get(other).remove(e);
			if (undirectedEdges.get(other).isEmpty()) {
				undirectedEdges.remove(other);
			}
		} else {
			if (e.getFirstEnd() == node) {
				outgoingDirectedEdges.get(other).remove(e);
				if (outgoingDirectedEdges.get(other).isEmpty()) {
					outgoingDirectedEdges.remove(other);
				}
			} else if (e.getSecondEnd() == node) {
				incomingDirectedEdges.get(other).remove(e);
				if (incomingDirectedEdges.get(other).isEmpty()) {
					incomingDirectedEdges.remove(other);
				}
			}
		}
	}

	/**
	 * Get a map of the edges from this data's node to this neighbors. Set
	 * followDirected to true to exclude incoming directed edges. The returned
	 * map will not contain any self edges.
	 *
	 * @param followDirected true if we wish to exclude incoming edges.
	 * @return A map of edges from this node to its neighbors.
	 */
	public Map<Node, Set<Edge>> getNeighboringEdges(boolean followDirected) {
		Map<Node, Set<Edge>> neighboring = new HashMap<>();
		undirectedEdges.forEach((node, edges) -> neighboring.put(node, new HashSet<>(edges)));
		for (Map.Entry<Node, Set<Edge>> outEntry : outgoingDirectedEdges.entrySet()) {
			neighboring.computeIfAbsent(outEntry.getKey(), $ -> new HashSet<>());
			neighboring.get(outEntry.getKey()).addAll(outEntry.getValue());
		}

		if (!followDirected) {
			for (Map.Entry<Node, Set<Edge>> inEntry : incomingDirectedEdges.entrySet()) {
				neighboring.computeIfAbsent(inEntry.getKey(), $ -> new HashSet<>());
				neighboring.get(inEntry.getKey()).addAll(inEntry.getValue());
			}
		}

		return neighboring;
	}

	/**
	 * Find the set of all edges having this data's node and the specified
	 * neighbor as endpoints. Set followDirected to true to exclude incoming
	 * directed edges.
	 *
	 * @param neighbor       The node neigboring this one (second endpoint).
	 * @param followDirected true if we wish to exclude incoming edges.
	 * @return A set of neigboring edges whose second endpoint is the provided
	 * neighbor node.
	 */
	public Set<Edge> getEdgesToNeighbor(Node neighbor, boolean followDirected) {
		Set<Edge> toNeighbor = new HashSet<>();
		if (undirectedEdges.containsKey(neighbor)) {
			toNeighbor.addAll(undirectedEdges.get(neighbor));
		}
		if (outgoingDirectedEdges.containsKey(neighbor)) {
			toNeighbor.addAll(outgoingDirectedEdges.get(neighbor));
		}
		if (!followDirected && incomingDirectedEdges.containsKey(neighbor)) {
			toNeighbor.addAll(incomingDirectedEdges.get(neighbor));
		}

		return toNeighbor;
	}

	/**
	 * Get the set of nodes directly connected to this data's node via an edge.
	 * Set followDirected to true to exclude neighbors only connected via
	 * incoming directed edges.
	 *
	 * @param followDirected true if we want to exclude neighbors with only
	 *                       directed edges toward this node, false otherwise.
	 * @return The set of neighbor nodes.
	 */
	public Set<Node> getNeighbors(boolean followDirected) {
		Set<Node> neighbors = new HashSet<>();

		if (!selfEdges.isEmpty()) {
			neighbors.add(node);
		}
		undirectedEdges.values().forEach(
			edgeSet -> edgeSet.forEach(e -> neighbors.add(e.getOtherEndpoint(node)))
		);
		outgoingDirectedEdges.values().forEach(
			edgeSet -> edgeSet.forEach(e -> neighbors.add(e.getOtherEndpoint(node)))
		);
		if (!followDirected) {
			incomingDirectedEdges.values().forEach(
				edgeSet -> edgeSet.forEach(e -> neighbors.add(e.getOtherEndpoint(node)))
			);
		}

		return neighbors;
	}

}
