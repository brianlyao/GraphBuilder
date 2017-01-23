package algorithms;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import util.StructureUtils;
import components.Edge;
import components.Node;
import graph.Graph;

public class CycleAlgorithms {

	private static final boolean DEBUG = false;
	
	/**
	 * Check whether a particular graph is acyclic (contains no cycle). This is
	 * applicable to undirected, directed, and mixed graphs. The definition of
	 * acyclic for a mixed graph is: if there is no way to change undirected
	 * edges into directed edges such that there is a directed cycle formed as
	 * a result, then the graph is acyclic.
	 * 
	 * @param graph The graph to classify.
	 * @return true iff the graph contains no cycles.
	 */
	public static boolean isAcyclic(Graph graph) {
		Set<Node> unvisited = new HashSet<>(graph.getNodes());
		Set<Node> visited = new HashSet<>();
		Set<Node> visiting = new HashSet<>();
		Map<Node, Node> parents = new HashMap<>();
		
		// Perform a depth-first search on every connected component
		while (!unvisited.isEmpty()) {
			// Choose a starting node with indegree 0
			Node start = StructureUtils.randomElement(unvisited);
			parents.put(start, null);
			
			// Perform depth first search
			boolean componentAcyclic = visit(start, unvisited, visiting, visited, parents);
			if (!componentAcyclic) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * A recursive helper function used to carry out the DFS needed for the
	 * isAcyclic algorithm.
	 * 
	 * @param n         The node being visited.
	 * @param unvisited The set of unvisited nodes.
	 * @param visiting  The set of nodes being visited (neighbors being visited).
	 * @param visited   The set of nodes which are fully visited.
	 * @param parents   The map from a node to its "parent" node which was
	 *                  visited first.
	 * @return true iff no cycles were detected.
	 */
	private static boolean visit(Node n, Set<Node> unvisited, Set<Node> visiting, Set<Node> visited, Map<Node, Node> parents) {
		if (DEBUG) {
			System.out.println("Visiting " + n.getID());
		}
		if (!n.getSelfEdges().isEmpty()) {
			// Self edge is a cycle
			return false;
		}

		unvisited.remove(n);
		visiting.add(n);
		for (Map.Entry<Node, Set<Edge>> edgeEntry : n.getNeighboringEdges(false).entrySet()) {
			Node neighbor = edgeEntry.getKey();
			if (!parents.containsKey(neighbor)) {
				parents.put(neighbor, n);
			}
			
			int undirected = 0;
			int outgoing = 0;
			int incoming = 0;
			for (Edge adjEdge : edgeEntry.getValue()) {
				if (!adjEdge.isDirected()) {
					undirected++;
				} else if (adjEdge.getEndpoints().getFirst() == n) {
					outgoing++;
				} else {
					incoming++;
				}
			}
			
			if (undirected > 1 || (undirected > 0 && (outgoing > 0 || incoming > 0)) || (outgoing > 0 && incoming > 0)) {
				// Cycle between two nodes
				if (DEBUG) {
					System.out.printf("Encountered cycle between pair %d and %d\n", n.getID(), neighbor.getID());
				}
				return false;
			}
			
			if (visiting.contains(neighbor) && parents.get(n) != neighbor) {
				// Encountered a cycle
				if (DEBUG) {
					System.out.printf("Encountered cycle on edge from %d to %d\n", n.getID(), neighbor.getID());
				}
				return false;
			}
			
			if (!visiting.contains(neighbor) && !visited.contains(neighbor)) {
				// Only visit neighbor if it's unvisited
				if (DEBUG) {
					System.out.printf("About to visit %d from %d\n", neighbor.getID(), n.getID());
				}
				boolean acyclic = visit(neighbor, unvisited, visiting, visited, parents);
				if (!acyclic) {
					// Encountered a cycle
					return false;
				}
			}
		}
		
		// Move node from visiting to visited
		visiting.remove(n);
		visited.add(n);
		return true;
	}
	
}
