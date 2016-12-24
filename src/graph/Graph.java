package graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import structures.UnorderedNodePair;
import components.Edge;
import components.Node;
import components.SelfEdge;
import components.WeightedEdge;

/**
 * A high-level abstract class for a graph data structure.
 * 
 * @author Brian
 */
public class Graph {

	private int restrictions;
	
	private Set<Node> nodes;
	private Map<UnorderedNodePair, Set<Edge>> edges;
	private Map<UnorderedNodePair, Map<Edge, Integer>> edgeOrders;
	
	/**
	 * Create an empty graph.
	 */
	public Graph(int restrictions) {
		this.restrictions = restrictions;
		nodes = new HashSet<Node>();
		edges = new HashMap<UnorderedNodePair, Set<Edge>>();
		edgeOrders = new HashMap<UnorderedNodePair, Map<Edge, Integer>>();
	}
	
	/**
	 * Check if the specified restriction is placed on this graph.
	 * 
	 * @param restriction The restriction bit mask.
	 * @return true iff this graph is under the provided restriction.
	 */
	public boolean hasRestriction(int restriction) {
		return (restrictions & restriction) == restriction;
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
	public Map<UnorderedNodePair, Set<Edge>> removeNode(Node n) {
		Map<UnorderedNodePair, Set<Edge>> removedEdgeMap = new HashMap<>();
		for (Node neighbor : n.getNeighbors(false)) {
			UnorderedNodePair key = new UnorderedNodePair(neighbor, n);
			if (edges.get(key) != null) {
				removedEdgeMap.put(key, edges.get(key));
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
		boolean isWeighted = e instanceof WeightedEdge<?>;
		boolean violatesUndirected = this.hasRestriction(GraphRestriction.UNDIRECTED) && e.isDirected();
		boolean violatesDirected = this.hasRestriction(GraphRestriction.DIRECTED) && !e.isDirected();
		boolean violatesUnweighted = this.hasRestriction(GraphRestriction.UNWEIGHTED) && isWeighted;
		boolean violatesIntWeighted = this.hasRestriction(GraphRestriction.INTEGER_WEIGHTED)
			&& (!isWeighted || (isWeighted && !(((WeightedEdge<?>) e).getWeight() instanceof Integer)));
		boolean violatesDblWeighted = this.hasRestriction(GraphRestriction.DOUBLE_WEIGHTED)
			&& (!isWeighted || (isWeighted && !(((WeightedEdge<?>) e).getWeight() instanceof Double)));
		boolean violatesLoopsAllowed = !this.hasRestriction(GraphRestriction.LOOPS_ALLOWED) && e instanceof SelfEdge;
		
		if (violatesUndirected || violatesDirected || violatesUnweighted || violatesIntWeighted || violatesDblWeighted
			|| violatesLoopsAllowed) {
			// If any restriction is violated
			return false;
		}
		
		UnorderedNodePair key = new UnorderedNodePair(e);
		boolean simple = this.hasRestriction(GraphRestriction.SIMPLE);
		boolean multigraph = this.hasRestriction(GraphRestriction.MULTIGRAPH);
		if (!this.getEdges().containsKey(key)) {
			// If the key is not already in the map
			if (simple) { 
				this.getEdges().put(key, Collections.singleton(e));
			} else if (multigraph) {
				this.getEdges().put(key, new HashSet<Edge>());
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
			for (Map.Entry<UnorderedNodePair, Map<Edge, Integer>> entry : edgeOrders.entrySet()) {
				int existingIndex = entry.getValue().get(e);
				if (existingIndex >= insertionIndex) {
					// Increment all indices greater than or equal to the insertion index
					edgeOrders.get(entry.getKey()).put(e, existingIndex + 1);
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Remove the specified edge from this graph.
	 * 
	 * @param e The edge to remove.
	 */
	public boolean removeEdge(Edge e) {
		UnorderedNodePair key = new UnorderedNodePair(e);
		if (edges.get(key) != null) {
			Set<Edge> pairEdges = edges.get(key);
			boolean removed = pairEdges.remove(e);
			if (pairEdges.isEmpty()) {
				edges.remove(key);
			} else if (this.hasRestriction(GraphRestriction.MULTIGRAPH)) {
				// Perform multigraph procedures
				Map<Edge, Integer> orders = edgeOrders.get(key);
				Integer removedIndex = orders.remove(e);
				for (Map.Entry<Edge, Integer> orderEntry : orders.entrySet()) {
					if (orderEntry.getValue().compareTo(removedIndex) > 0) {
						orders.put(orderEntry.getKey(), orderEntry.getValue() - 1);
					}
				}
			}
			return removed;
		}
		return false;
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
	public Map<UnorderedNodePair, Set<Edge>> getEdges() {
		return edges;
	}
	
	/**
	 * Obtain the induced subgraph from this graph given the set of nodes in
	 * the subgraph. All edges within the provided set of nodes are included.
	 * 
	 * @param nodes The set of nodes in the desired subgraph.
	 * @return The induced subgraph.
	 */
	public Graph inducedSubgraph(Set<Node> nodes) {
		Graph subgraph = new Graph(restrictions);
		for (Node n : nodes) {
			subgraph.addNode(n);
		}
		for (Map.Entry<UnorderedNodePair, Set<Edge>> edgeEntry : edges.entrySet()) {
			Node first = edgeEntry.getKey().getFirst();
			Node second = edgeEntry.getKey().getSecond();
			if (nodes.contains(first) && nodes.contains(second)) {
				// Add all relevant edges
				Map<Edge, Integer> edgeOrder = edgeOrders.get(edgeEntry.getKey());
				for (Edge e : edgeEntry.getValue()) {
					subgraph.addEdge(e, edgeOrder.get(e));
				}
			}
		}
		return subgraph;
	}
	
}
