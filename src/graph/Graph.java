package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import structures.UnorderedNodePair;
import components.Edge;
import components.Node;

/**
 * A class for a graph data structure.
 * 
 * @author Brian
 */
public class Graph {

	private int constraints;
	
	private Set<Node> nodes;
	private Map<UnorderedNodePair, List<Edge>> edges;
	
	/**
	 * Create an empty graph with the provided constraints.
	 */
	public Graph(int constraints) {
		this.constraints = constraints;
		nodes = new HashSet<Node>();
		edges = new HashMap<UnorderedNodePair, List<Edge>>();
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
	 * Add the provided node to this graph.
	 * 
	 * @param n The node to add.
	 */
	public void addNode(Node n) {
		nodes.add(n);
	}
	
	/**
	 * Remove the specified node from the graph, and return the map of edges
	 * which were connected to the specified node which were removed as a result.
	 * 
	 * @param n The node to remove.
	 * @return The edges removed as a result of the node's removal.
	 */
	public Map<UnorderedNodePair, List<Edge>> removeNode(Node n) {
		Map<UnorderedNodePair, List<Edge>> removedEdgeMap = new HashMap<>();
		for (Node neighbor : n.getNeighbors(false)) {
			UnorderedNodePair key = new UnorderedNodePair(neighbor, n);
			if (edges.get(key) != null) {
				List<Edge> removedEdgeList = edges.remove(key);
				removedEdgeMap.put(key, removedEdgeList);
			}
		}
		return removedEdgeMap;
	}
	
	/**
	 * Add the specified edge to this graph.
	 * 
	 * @param e    The edge to add.
	 * @param data Additional data needed to add the edge.
	 */
	public boolean addEdge(Edge e, Object data) {
		UnorderedNodePair key = new UnorderedNodePair(e);
		boolean simple = this.hasConstraint(GraphConstraint.SIMPLE);
		boolean multigraph = this.hasConstraint(GraphConstraint.MULTIGRAPH);
		if (!edges.containsKey(key)) {
			// If the key is not already in the map
			if (simple) { 
				edges.put(key, Collections.singletonList(e));
			} else if (multigraph) {
				edges.put(key, new ArrayList<Edge>());
			}
		} else if (simple) {
			// Simple graph restriction violated
			return false;
		}
		
		if (multigraph) {
			// Multigraph-specific procedure
			if (edges.get(key).contains(e)) {
				// If the edge already exists
				return false;
			}
			
			Integer insertionIndex = (Integer) data;
			edges.get(key).add(insertionIndex, e);
		}
		
		return true;
	}
	
	/**
	 * Remove the specified edge from this graph.
	 * 
	 * @param e The edge to remove.
	 * @return The index (among other edges between the same pair of nodes as that which
	 *         was removed) of the edge which was removed. This is always 0 for simple
	 *         graphs.
	 */
	public int removeEdge(Edge e) {
		UnorderedNodePair key = new UnorderedNodePair(e);
		if (edges.get(key) != null && edges.get(key).contains(e)) {
			// If the edge e is in the graph...
			List<Edge> pairEdges = edges.get(key);
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
		}
		return -1;
	}
	
	/**
	 * Get the constraints set on this graph.
	 * 
	 * @return The integer bit mask of constraints set on this graph.
	 */
	public int getConstraints() {
		return constraints;
	}
	
	/**
	 * Get the set of all nodes in this graph.
	 * 
	 * @return The full set of nodes.
	 */
	public Set<Node> getNodes() {
		return nodes;
	}
	
	/**
	 * Get the map of all edges in this graph.
	 * 
	 * @return The full map of edges.
	 */
	public Map<UnorderedNodePair, List<Edge>> getEdges() {
		return edges;
	}
	
	/**
	 * Get a set of all edges in the graph.
	 * 
	 * @return The set of edges in this graph.
	 */
	public Set<Edge> edgeSet() {
		Set<Edge> edgeSet = new HashSet<>();
		for (Map.Entry<UnorderedNodePair, List<Edge>> edgeEntry : edges.entrySet()) {
			edgeSet.addAll(edgeEntry.getValue());
		}
		return edgeSet;
	}
	
	/**
	 * Obtain the induced subgraph from this graph given the set of nodes in
	 * the subgraph. All edges within the provided set of nodes are included.
	 * 
	 * @param nodes The set of nodes in the desired subgraph.
	 * @return The induced subgraph.
	 */
	public Graph inducedSubgraph(Set<Node> nodes) {
		Graph subgraph = new Graph(constraints);
		for (Node n : nodes) {
			subgraph.addNode(n);
		}
		for (Map.Entry<UnorderedNodePair, List<Edge>> edgeEntry : edges.entrySet()) {
			Node first = edgeEntry.getKey().getFirst();
			Node second = edgeEntry.getKey().getSecond();
			if (nodes.contains(first) && nodes.contains(second)) {
				// Add all relevant edges
				for (Edge e : edgeEntry.getValue()) {
					subgraph.addEdge(e, null);
				}
			}
		}
		return subgraph;
	}
	
}
