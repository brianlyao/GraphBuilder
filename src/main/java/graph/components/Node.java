package graph.components;

import graph.components.gb.GBNode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An instance represents a node component of a graph.
 *
 * @author Brian Yao
 */
public class Node extends GraphComponent {

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

	// The GBNode associated with this node (if any).
	@Getter @Setter
	private GBNode gbNode;

	/**
	 * Creates a node.
	 */
	public Node() {
		undirectedEdges = new HashMap<>();
		outgoingDirectedEdges = new HashMap<>();
		incomingDirectedEdges = new HashMap<>();
		selfEdges = new HashSet<>();
	}

	/**
	 * Add an edge containing this node as an endpoint to this node's data.
	 *
	 * @param e The edge to add.
	 */
	public void addEdge(Edge e) {
		Node other = e.getOtherEndpoint(this);
		if (this == other) {
			// Self edge case
			selfEdges.add(e);
			return;
		}

		// Handle non-self edges
		if (!e.isDirected()) {
			undirectedEdges.computeIfAbsent(other, ignored -> new HashSet<>());
			undirectedEdges.get(other).add(e);
		} else {
			if (e.getFirstEnd() == this) {
				outgoingDirectedEdges.computeIfAbsent(other, ignored -> new HashSet<>());
				outgoingDirectedEdges.get(other).add(e);
			} else if (e.getSecondEnd() == this) {
				incomingDirectedEdges.computeIfAbsent(other, ignored -> new HashSet<>());
				incomingDirectedEdges.get(other).add(e);
			}
		}
	}

	/**
	 * Remove an edge from this node's data.
	 *
	 * @param e The edge to remove.
	 */
	public void removeEdge(Edge e) {
		Node other = e.getOtherEndpoint(this);
		if (this == other) {
			// Self edge case
			selfEdges.remove(e);
			return;
		}

		// Handle simple edges
		if (!e.isDirected()) {
			Set<Edge> toNeighborUndir = this.undirectedEdges.get(other);
			if (toNeighborUndir != null && !toNeighborUndir.isEmpty()) {
				toNeighborUndir.remove(e);
			}
		} else {
			if (e.getFirstEnd() == this) {
				Set<Edge> toNeighbor = outgoingDirectedEdges.get(other);
				if (toNeighbor != null && !toNeighbor.isEmpty()) {
					toNeighbor.remove(e);
				}
			} else if (e.getSecondEnd() == this) {
				Set<Edge> fromNeighbor = incomingDirectedEdges.get(other);
				if (fromNeighbor != null && !fromNeighbor.isEmpty()) {
					fromNeighbor.remove(e);
				}
			}
		}
	}

	/**
	 * Get the number of self edges.
	 *
	 * @return The number of self edges.
	 */
	public int numSelfEdges() {
		return selfEdges.size();
	}

	/**
	 * Get the number of undirected edges.
	 *
	 * @return The number of undirected edges.
	 */
	public int numUndirectedEdges() {
		int count = 0;
		for (Set<Edge> edgeSet : undirectedEdges.values()) {
			count += edgeSet.size();
		}
		return count;
	}

	/**
	 * Get the number of outgoing directed edges.
	 *
	 * @return The number of outgoing directed edges.
	 */
	public int numOutgoingDirectedEdges() {
		int count = 0;
		for (Set<Edge> edgeSet : outgoingDirectedEdges.values()) {
			count += edgeSet.size();
		}
		return count;
	}

	/**
	 * Get the number of incoming directed edges.
	 *
	 * @return The number of incoming directed edges.
	 */
	public int numIncomingDirectedEdges() {
		int count = 0;
		for (Set<Edge> edgeSet : incomingDirectedEdges.values()) {
			count += edgeSet.size();
		}
		return count;
	}

	/**
	 * Get a map of the edges from this node to this neighbors. We may disregard
	 * incoming directed edges if we want. The returned map will not contain any
	 * self edges.
	 *
	 * @param followDirected true iff we want to disregard incoming directed edges.
	 * @return A map of edges from this node to its neighbors.
	 */
	public Map<Node, Set<Edge>> getNeighboringEdges(boolean followDirected) {
		Map<Node, Set<Edge>> neighboring = new HashMap<>(undirectedEdges);
		for (Map.Entry<Node, Set<Edge>> outEntry : outgoingDirectedEdges.entrySet()) {
			Set<Edge> neighborValue = neighboring.get(outEntry.getKey());
			if (neighborValue == null) {
				neighboring.put(outEntry.getKey(), outEntry.getValue());
			} else {
				neighborValue.addAll(outEntry.getValue());
			}
		}

		if (!followDirected) {
			for (Map.Entry<Node, Set<Edge>> inEntry : incomingDirectedEdges.entrySet()) {
				Set<Edge> neighborValue = neighboring.get(inEntry.getKey());
				if (neighborValue == null) {
					neighboring.put(inEntry.getKey(), inEntry.getValue());
				} else {
					neighborValue.addAll(inEntry.getValue());
				}
			}
		}

		return neighboring;
	}

	/**
	 * Find the set of all edges having this node and the specified neighbor
	 * as endpoints. If the followDirected parameter is set to true, the
	 * set will exclude all edges incoming toward this node.
	 *
	 * @param neighbor       The node neigboring this one (second endpoint).
	 * @param followDirected True if we wish to exclude incoming edges.
	 * @return A set of neigboring edges whose second endpoint is the provided
	 * neighbor node.
	 */
	public Set<Edge> getEdgesToNeighbor(Node neighbor, boolean followDirected) {
		return getNeighboringEdges(followDirected).get(neighbor);
	}

	/**
	 * Get the set of nodes directly connected to this node via an edge. If we want, we
	 * may choose to disregard neighbors linked only by a directed edge pointing toward
	 * this node.
	 *
	 * @param followDirected true if we want to disregard neighbors with only directed edges
	 *                       toward this node, false otherwise.
	 * @return The set of neighbor nodes.
	 */
	public Set<Node> getNeighbors(boolean followDirected) {
		Set<Node> neighbors = new HashSet<>();

		if (!selfEdges.isEmpty()) {
			neighbors.add(this);
		}
		undirectedEdges.values().forEach(
			edgeSet -> edgeSet.forEach(e -> neighbors.add(e.getOtherEndpoint(this)))
		);
		outgoingDirectedEdges.values().forEach(
			edgeSet -> edgeSet.forEach(e -> neighbors.add(e.getOtherEndpoint(this)))
		);
		if (!followDirected) {
			incomingDirectedEdges.values().forEach(
				edgeSet -> edgeSet.forEach(e -> neighbors.add(e.getOtherEndpoint(this)))
			);
		}

		return neighbors;
	}

	@Override
	public String toString() {
		return String.format("{%d}", this.getId());
	}

}
