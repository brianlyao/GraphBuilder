package graph;

import graph.components.Edge;
import graph.components.Node;
import lombok.Getter;
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
		this.constraints = graph.constraints;

		// Make copies of the components from the given graph
		Map<Node, Node> oldToNew = graph.getNodes().stream()
			.collect(Collectors.toMap(Function.identity(), $ -> new Node()));
		this.nodes = new HashSet<>(oldToNew.values());

		this.edges = new HashMap<>();
		for (Map.Entry<UOPair<Node>, List<Edge>> oldEntry : graph.getEdges().entrySet()) {
			Node newNode1 = oldToNew.get(oldEntry.getKey().getFirst());
			Node newNode2 = oldToNew.get(oldEntry.getKey().getSecond());
			List<Edge> newEdgeList = oldEntry.getValue().stream()
				.map(e -> new Edge(oldToNew.get(e.getFirstEnd()), oldToNew.get(e.getSecondEnd()), e.isDirected()))
				.collect(Collectors.toList());
			this.edges.put(new UOPair<>(newNode1, newNode2), newEdgeList);
		}
	}

	/**
	 * Create an empty graph with the provided constraints.
	 *
	 * @param constraints The graph's constraints.
	 * @see GraphConstraint
	 */
	public Graph(int constraints) {
		this.constraints = constraints;
		this.nodes = new HashSet<>();
		this.edges = new HashMap<>();
	}

	/**
	 * Create a graph with the given nodes and edges.
	 *
	 * @param constraints The graph's constraints.
	 * @param nodes       The set of nodes belonging to this graph.
	 * @param edges       The edges belonging to this graph.
	 * @see GraphConstraint
	 */
	private Graph(int constraints, Set<Node> nodes, Map<UOPair<Node>, List<Edge>> edges) {
		this.constraints = constraints;
		this.nodes = nodes;
		this.edges = edges;
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
		UOPair<Node> ends = edge.getUoEndpoints();
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
	 * Add the provided node to this graph.
	 *
	 * @param n The node to add.
	 */
	public void addNode(Node n) {
		if (this.containsNode(n)) {
			throw new IllegalArgumentException("Cannot add a node to this graph more than once.");
		}

		nodes.add(n);
	}

	/**
	 * Add the provided nodes to this graph.
	 *
	 * @param nodes The nodes to add.
	 */
	public void addNodes(Iterable<Node> nodes) {
		nodes.forEach(this::addNode);
	}

	/**
	 * Add the provided nodes to this graph.
	 *
	 * @param nodes The nodes to add.
	 */
	public void addNodes(Node... nodes) {
		Arrays.stream(nodes).forEach(this::addNode);
	}

	/**
	 * Remove the specified node from the graph. As a result, any edges
	 * incident to it are also removed. These are returned.
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
				removedEdgeList.forEach(Edge::removeSelfFromNodeData);

				removedEdgeMap.put(key, removedEdgeList);
			}
		}

		// Remove node
		nodes.remove(n);

		return removedEdgeMap;
	}

	/**
	 * Add the specified edge to this graph. If this graph is a multigraph,
	 * the edge will be added at the end of the list of edges with the same
	 * endpoints as the edge being added.
	 *
	 * @param e The edge to add.
	 * @return true if the edge was successfully added, false if the edge has
	 * already been added.
	 */
	public boolean addEdge(Edge e) {
		UOPair<Node> key = e.getUoEndpoints();
		int nextIndex = edges.containsKey(key) ? edges.get(key).size() : 0;
		return this.addEdge(e, nextIndex);
	}

	/**
	 * Add the specified edge to this multigraph. The location of the new edge
	 * relative to the other edges between those two endpoints must be
	 * provided.
	 *
	 * @param e     The edge to add.
	 * @param index The index in the list to add the new edge. This is only
	 *              relevant for multigraphs.
	 * @return true if the edge was successfully added, false if the edge has
	 *         already been added.
	 */
	public boolean addEdge(Edge e, int index) {
		if (this.containsEdge(e)) {
			throw new IllegalArgumentException("Cannot add an edge to this graph more than once.");
		}

		UOPair<Node> key = e.getUoEndpoints();

		// Check if the provided index is valid
		int maxIndex = edges.get(key) == null ? 0 : edges.get(key).size();
		if (index < 0 || index > maxIndex) {
			throw new IndexOutOfBoundsException("Index out of bounds when inserting edge: " + index +
													" ; it should be between 0 and " + maxIndex + ", inclusive.");
		}

		if (this.hasConstraint(GraphConstraint.SIMPLE)) {
			if (edges.get(key) == null && !e.isSelfEdge()) {
				edges.put(key, Collections.singletonList(e));
			} else {
				// Simple graph restriction violated
				throw new IllegalArgumentException("Cannot add edge if the graph is to remain simple.");
			}
		}

		if (this.hasConstraint(GraphConstraint.MULTIGRAPH)) {
			// Multigraph-specific procedure
			edges.computeIfAbsent(key, $ -> new ArrayList<>());
			edges.get(key).add(index, e);
		}

		// Add this edge to its endpoints' data
		e.addSelfToNodeData();

		return true;
	}

	/**
	 * Adds all the specified edges to this graph.
	 *
	 * @param edges The edges to add.
	 * @return true iff all edges were successfully added.
	 */
	public boolean addEdges(Iterable<Edge> edges) {
		boolean allAdded = true;
		for (Edge edge : edges) {
			allAdded = allAdded && this.addEdge(edge);
		}
		return allAdded;
	}

	/**
	 * Adds all the specified edges to this graph.
	 *
	 * @param edges The edges to add.
	 * @return true iff all edges were successfully added.
	 */
	public boolean addEdges(Edge... edges) {
		boolean allAdded = true;
		for (Edge edge : edges) {
			allAdded = allAdded && this.addEdge(edge);
		}
		return allAdded;
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
		// Remove this edge from its endpoints' data
		e.removeSelfFromNodeData();

		UOPair<Node> key = e.getUoEndpoints();
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
		throw new IllegalArgumentException("Cannot remove an edge that is not in the graph.");
	}

	/**
	 * Obtain the induced subgraph from this graph given the set of nodes in
	 * the subgraph. All edges whose endpoints are in the provided set of
	 * nodes are included. The components in the subgraph are identical to
	 * those in the original.
	 *
	 * @param nodes The collection of nodes in the desired subgraph.
	 * @return The induced subgraph.
	 */
	public Graph inducedSubgraph(Collection<Node> nodes) {
		Set<Node> subsetNodes = new HashSet<>(nodes);
		Map<UOPair<Node>, List<Edge>> subsetEdges = new HashMap<>();

		// Add edges included in subgraph
		edges.forEach((pair, edgeList) -> {
			if (nodes.contains(pair.getFirst()) && nodes.contains(pair.getSecond())) {
				subsetEdges.put(pair, edgeList);
			}
		});

		return new Graph(constraints, subsetNodes, subsetEdges);
	}

}