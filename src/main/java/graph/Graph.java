package graph;

import graph.components.Edge;
import graph.components.Node;
import lombok.Getter;
import structures.OrderedPair;
import structures.UOPair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A class for a graph data structure. The implementation is specific to the
 * needs of certain algorithms, and probably not well suited for general
 * purposes. There are far better libraries such as JGraph with more
 * sophisticated parametrized graph types.
 *
 * This class is abstracted away from GraphBuilder, so it is an implementation
 * of an abstract graph data structure.
 *
 * @author Brian Yao
 */
public class Graph {

	@Getter
	private int constraints;

	@Getter
	private Set<Node> nodes;
	@Getter
	private Map<UOPair<Node>, List<Edge>> edges;

	/**
	 * Copy constructor.
	 *
	 * @param graph The graph to create a copy of.
	 */
	public Graph(Graph graph) {
		constraints = graph.constraints;

		// Make copies of the components from the given graph
		Map<Node, Node> oldToNew = graph.getNodes().stream()
			.collect(Collectors.toMap(Function.identity(), ignored -> new Node()));
		nodes = new HashSet<>(oldToNew.values());

		edges = new HashMap<>();
		for (Map.Entry<UOPair<Node>, List<Edge>> oldEntry : graph.getEdges().entrySet()) {
			Node newNode1 = oldToNew.get(oldEntry.getKey().getFirst());
			Node newNode2 = oldToNew.get(oldEntry.getKey().getSecond());
			List<Edge> newEdgeList = oldEntry.getValue().stream()
				.map(e -> new Edge(oldToNew.get(e.getFirstEnd()), oldToNew.get(e.getSecondEnd()), e.isDirected()))
				.collect(Collectors.toList());
			edges.put(new UOPair<>(newNode1, newNode2), newEdgeList);
		}
	}

	/**
	 * Create an empty graph with the provided constraints.
	 */
	public Graph(int constraints) {
		this.constraints = constraints;
		nodes = new HashSet<>();
		edges = new HashMap<>();
	}

	/**
	 * Check if the specified restriction is placed on this graph.
	 *
	 * @param constraint The restriction bit mask.
	 * @return true iff this graph is subject to the provided constraint.
	 */
	public boolean hasConstraint(int constraint) {
		return (constraints & constraint) == constraint;
	}

	/**
	 * Add constraint(s) to the graph.
	 *
	 * @param constraint The added constraint(s).
	 */
	public void addConstraint(int constraint) {
		constraints |= constraint;
	}

	/**
	 * Remove constraint(s) from the graph. If the graph does not have the
	 * provided constraints, no change is made.
	 *
	 * @param constraint The removed constraint(s).
	 */
	public void removeConstraint(int constraint) {
		constraints &= ~constraint;
	}

	/**
	 * Check if the graph contains the specified node.
	 *
	 * @param node The node to check for inclusion.
	 * @return true iff this graph contains the specified node.
	 */
	public boolean containsNode(Node node) {
		return nodes.contains(node);
	}

	/**
	 * Check if the graph contains the specified edge.
	 *
	 * @param edge The edge to check for inclusion.
	 * @return true iff the graph contains the specified edge.
	 */
	public boolean containsEdge(Edge edge) {
		UOPair<Node> ends = new UOPair<>(edge.getFirstEnd(), edge.getSecondEnd());
		return edges.get(ends) != null && edges.get(ends).contains(edge);
	}

	/**
	 * Checks if the graph is empty. The graph is empty if it has no nodes.
	 *
	 * @return true iff the graph is empty.
	 */
	public boolean isEmpty() {
		return nodes.isEmpty();
	}

	/**
	 * Get the number of edges in this graph. Every edge, directed or not, is
	 * counted separately.
	 *
	 * @return The total number of edges in this graph.
	 */
	public int edgeCount() {
		int numEdges = 0;
		for (List<Edge> edgeList : edges.values()) {
			numEdges += edgeList.size();
		}
		return numEdges;
	}

	/**
	 * Add the provided node to this graph.
	 *
	 * @param n The node to add.
	 */
	public void addNode(Node n) {
		if (nodes.contains(n)) {
			throw new IllegalArgumentException("Cannot add a node more than once.");
		}
		nodes.add(n);
	}

	/**
	 * Remove the specified node from the graph, and return the map of edges
	 * which were connected to the specified node which were removed as a result.
	 *
	 * @param n The node to remove.
	 * @return The edges removed as a result of the node's removal.
	 */
	public Map<UOPair<Node>, List<Edge>> removeNode(Node n) {
		if (!this.containsNode(n)) {
			throw new IllegalArgumentException("Cannot remove a node that is not in the graph.");
		}

		Map<UOPair<Node>, List<Edge>> removedEdgeMap = new HashMap<>();
		for (Node neighbor : n.getNeighbors(false)) {
			UOPair<Node> key = new UOPair<>(neighbor, n);

			// Remove edges connected to the deleted node
			if (edges.get(key) != null) {
				List<Edge> removedEdgeList = edges.remove(key);
				removedEdgeMap.put(key, removedEdgeList);
			}
		}

		// Remove node
		nodes.remove(n);

		return removedEdgeMap;
	}

	/**
	 * Add the specified edge to this graph. If this graph is a multigraph,
	 * the edge will be added at the end of the list.
	 *
	 * @param e The edge to add.
	 * @return true if the edge was successfully added, false if the edge has
	 * already been added.
	 */
	public boolean addEdge(Edge e) {
		UOPair<Node> key = new UOPair<>(e.getFirstEnd(), e.getSecondEnd());
		int nextIndex = edges.containsKey(key) ? edges.get(key).size() : 0;
		return this.addEdge(e, nextIndex);
	}

	/**
	 * Add the specified edge to this graph. Extra data may be necessary. For
	 * example, if we are inserting an edge into a multigraph, we may specify
	 * the location of the new edge relative to the other edges between those
	 * two endpoints. In this case, we would pass an integer index as the data.
	 *
	 * @param e     The edge to add.
	 * @param index The index in the list to add the new edge. This is only
	 *              relevant for multigraphs.
	 * @return true if the edge was successfully added, false if the edge has
	 * already been added.
	 */
	public boolean addEdge(Edge e, int index) {
		if (this.containsEdge(e)) {
			throw new IllegalArgumentException("Cannot add an edge more than once.");
		}
		UOPair<Node> key = new UOPair<>(e.getFirstEnd(), e.getSecondEnd());

		if (this.hasConstraint(GraphConstraint.SIMPLE)) {
			if (edges.get(key) == null) {
				edges.put(key, Collections.singletonList(e));

				// Add this edge to its endpoints' data
				e.getFirstEnd().addEdge(e);
				e.getSecondEnd().addEdge(e);

				return true;
			} else {
				// Simple graph restriction violated
				throw new IllegalArgumentException("Cannot add edge if the graph is to remain simple.");
			}
		}

		if (this.hasConstraint(GraphConstraint.MULTIGRAPH)) {
			// Multigraph-specific procedure
			edges.computeIfAbsent(key, ignored -> new ArrayList<>());
			if (edges.get(key).contains(e)) {
				// If the edge already exists, do not add again
				return false;
			}
			edges.get(key).add(index, e);
		}

		// Add this edge to its endpoints' data
		e.getFirstEnd().addEdge(e);
		e.getSecondEnd().addEdge(e);

		return true;
	}

	/**
	 * Remove the specified edge from this graph.
	 *
	 * @param e The edge to remove.
	 * @return The index (among other edges between the same pair of nodes as that which
	 * was removed) of the edge which was removed. This is always 0 for simple
	 * graphs.
	 */
	public int removeEdge(Edge e) {
		if (!this.containsEdge(e)) {
			throw new IllegalArgumentException("Cannot remove an edge that is not in the graph.");
		}

		// Remove this edge from its endpoints' data
		OrderedPair<Node> endpoints = e.getEndpoints();
		endpoints.getFirst().removeEdge(e);
		endpoints.getSecond().removeEdge(e);

		UOPair<Node> key = new UOPair<>(e.getFirstEnd(), e.getSecondEnd());
		List<Edge> pairEdges = edges.get(key);

		// If the edge e is in the graph
		if (pairEdges.size() == 1) {
			// If there is only one edge to remove (includes simple graph case)
			edges.remove(key);
			return 0;
		} else if (this.hasConstraint(GraphConstraint.MULTIGRAPH)) {
			// Perform multigraph procedures
			int removedIndex = pairEdges.indexOf(e);
			pairEdges.remove(removedIndex);
			return removedIndex;
		}

		// The given edge isn't in this graph
		return -1;
	}

	/**
	 * Adds all components from the given graph to this one.
	 *
	 * @param graph The graph whose components to add.
	 */
	public void addAll(Graph graph) {
		graph.getNodes().forEach(this::addNode);
		graph.getEdges().values().forEach(list -> list.forEach(this::addEdge));
	}

	/**
	 * Get a set of all edges in the graph.
	 *
	 * @return The set of edges in this graph.
	 */
	public Set<Edge> edgeSet() {
		Set<Edge> edgeSet = new HashSet<>();
		for (Map.Entry<UOPair<Node>, List<Edge>> edgeEntry : edges.entrySet()) {
			edgeSet.addAll(edgeEntry.getValue());
		}
		return edgeSet;
	}

	/**
	 * Obtain the induced subgraph from this graph given the set of nodes in
	 * the subgraph. All edges whose endpoints are in the provided set of
	 * nodes are included.
	 *
	 * @param nodes The set of nodes in the desired subgraph.
	 * @return The induced subgraph.
	 */
	public Graph inducedSubgraph(Set<Node> nodes) {
		Graph subgraph = new Graph(constraints);
		for (Node n : nodes) {
			subgraph.addNode(n);
		}
		for (Map.Entry<UOPair<Node>, List<Edge>> edgeEntry : edges.entrySet()) {
			Node first = edgeEntry.getKey().getFirst();
			Node second = edgeEntry.getKey().getSecond();
			if (nodes.contains(first) && nodes.contains(second)) {
				// Add all relevant edges
				for (Edge e : edgeEntry.getValue()) {
					subgraph.addEdge(e);
				}
			}
		}
		return subgraph;
	}

	/**
	 * Return an adjacency list representation of this graph in the form of a
	 * string. The format for each node is:
	 * <p>
	 * nodeid:neighbor0,neighbor1,neighbor2,...
	 * <p>
	 * where each value is an integer id. If there are no neighbors, there is
	 * no colon.
	 *
	 * @return The adjacency list for this graph.
	 */
	public String asAdjacencyList() {
		StringBuilder adjList = new StringBuilder();
		for (Node n : nodes) {
			adjList.append(n.getId() + ":");
			for (Node neighbor : n.getNeighbors(true)) {
				adjList.append(neighbor.getId() + ",");
			}
			adjList.replace(adjList.length() - 1, 1, "\n");
		}
		return adjList.toString();
	}

}
