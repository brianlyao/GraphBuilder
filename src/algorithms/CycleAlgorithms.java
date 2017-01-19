package algorithms;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import util.StructureUtils;
import components.Node;
import graph.Graph;

public class CycleAlgorithms {

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
		Set<Node> graphNodes = new HashSet<>(graph.getNodes());
		
		// Perform a depth-first search on every connected component
		while (!graphNodes.isEmpty()) {
			Node start = StructureUtils.randomElement(graphNodes);
			Set<Node> visited = new HashSet<>();
			Stack<Node> toVisit = new Stack<>();
			toVisit.push(start);
			
			Node visiting = null;
			while (!toVisit.isEmpty()) {
				visiting = toVisit.pop();
				for (Node neighbor : visiting.getNeighbors(true)) {
					if (visited.contains(neighbor)) {
						// If we are re-visiting a node, then we have encountered a cycle
						return false;
					} else {
						toVisit.push(neighbor);
					}
				}
				visited.add(visiting);
			}
			
			graphNodes.removeAll(visited);
		}
		
		return true;
	}
	
}
