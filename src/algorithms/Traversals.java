package algorithms;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import components.Node;

/**
 * A class containing implementations of graph traversals.
 * 
 * @author Brian
 */
public class Traversals {

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
	
	public static Set<Node> traverseConnectedComponents(Collection<Node> startingNodes, boolean followDirected) {
		Set<Node> visited = new HashSet<>();
		for (Node start : startingNodes) {
			if (!visited.contains(start)) {
				visited.addAll(breadthFirstSearch(start, followDirected));
			}
		}
		return visited;
	}
	
}
