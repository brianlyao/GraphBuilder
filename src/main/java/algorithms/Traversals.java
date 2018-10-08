package algorithms;

import graph.components.Node;

import java.util.*;

/**
 * A class containing implementations of graph traversals.
 *
 * @author Brian Yao
 */
public class Traversals {

	/**
	 * Performs a depth first search starting from the provided node. Returns
	 * the set of all nodes reached during this traversal. If the followDirected
	 * parameter is set to true, then we do not traverse directed edges in the
	 * direction opposing the edge's direction.
	 *
	 * @param start          The starting node of the traversal.
	 * @param followDirected True if we don't want to go against the direction
	 *                       of directed edges during this traversal.
	 * @return The set of all nodes reached during the DFS.
	 */
	public static Set<Node> depthFirstSearch(Node start, boolean followDirected) {
		Set<Node> visited = new HashSet<>();
		Stack<Node> toVisit = new Stack<>();
		toVisit.push(start);

		while (!toVisit.isEmpty()) {
			Node visiting = toVisit.pop();
			for (Node neighbor : visiting.getNeighbors(followDirected)) {
				if (!visited.contains(neighbor)) {
					toVisit.push(neighbor);
				}
			}
			visited.add(visiting);
		}

		return visited;
	}

	/**
	 * Performs a breadth first search starting from the provided node. Returns
	 * the set of all nodes reached during this traversal. If the followDirected
	 * parameter is set to true, then we do not traverse directed edges in the
	 * direction opposing the edge's direction.
	 *
	 * @param start          The starting node of the traversal.
	 * @param followDirected True if we don't want to go against the direction
	 *                       of directed edges during this traversal.
	 * @return The set of all nodes reached during the BFS.
	 */
	public static Set<Node> breadthFirstSearch(Node start, boolean followDirected) {
		Set<Node> visited = new HashSet<>();
		Queue<Node> toVisit = new LinkedList<>();
		toVisit.add(start);

		while (!toVisit.isEmpty()) {
			Node visiting = toVisit.poll();
			for (Node neighbor : visiting.getNeighbors(followDirected)) {
				if (!visited.contains(neighbor)) {
					toVisit.add(neighbor);
				}
			}
			visited.add(visiting);
		}

		return visited;
	}

	/**
	 * Performs multiple DFS's, using each of the nodes in startingNodes as the
	 * start of a separate traversal.
	 *
	 * @param startingNodes  The collection of starting nodes.
	 * @param followDirected True if we don't want to go against the direction
	 *                       of directed edges during this traversal.
	 * @return The set of all nodes traversed.
	 */
	public static Set<Node> depthFirstSearchAll(Collection<Node> startingNodes, boolean followDirected) {
		Set<Node> visited = new HashSet<>();
		for (Node start : startingNodes) {
			if (!visited.contains(start)) {
				visited.addAll(depthFirstSearch(start, followDirected));
			}
		}
		return visited;
	}

	/**
	 * Performs multiple BFS's, using each of the nodes in startingNodes as the
	 * start of a separate traversal.
	 *
	 * @param startingNodes  The collection of starting nodes.
	 * @param followDirected True if we don't want to go against the direction
	 *                       of directed edges during this traversal.
	 * @return The set of all nodes traversed.
	 */
	public static Set<Node> breadthFirstSearchAll(Collection<Node> startingNodes, boolean followDirected) {
		Set<Node> visited = new HashSet<>();
		for (Node start : startingNodes) {
			if (!visited.contains(start)) {
				visited.addAll(breadthFirstSearch(start, followDirected));
			}
		}
		return visited;
	}

}
