package algorithms.util;

import graph.components.Edge;
import graph.components.Node;
import graph.path.Path;
import util.GraphUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Shared code used in BFS and DFS.
 *
 * @author Brian Yao
 */
public class Search {

	/**
	 * Search from the start node to the target node, and return a path
	 * connecting them if one exists.
	 *
	 * @param start           The node at which the search starts.
	 * @param target          The target node.
	 * @param followDirected  false if we want to ignore edge direction.
	 * @param searchAlgorithm A procedure which performs the search. The input
	 *                        is a map from a node to its "parent" in the
	 *                        search tree. The output is true if the target
	 *                        was found, false otherwise.
	 * @return A path from start to target, or null if none exists.
	 */
	public static Path search(Node start, Node target, boolean followDirected,
							  Function<Map<Node, Node>, Boolean> searchAlgorithm) {
		if (start == target) {
			return new Path(start);
		}

		// Map nodes to its "parent node" in the search tree
		Map<Node, Node> parentMap = new HashMap<>();
		boolean foundTarget = searchAlgorithm.apply(parentMap);

		if (!foundTarget) {
			// No connecting path exists
			return null;
		}

		// Construct the path connecting start and target
		Path connectingPath = new Path(target);
		Node currentNode = target;
		while (currentNode != start) {
			Node previousNode = parentMap.get(currentNode);
			Edge toCurrent = GraphUtils.arbitraryEdge(previousNode, currentNode, followDirected);

			connectingPath.prependNode(previousNode, toCurrent);
			currentNode = previousNode;
		}

		return connectingPath;
	}

}
