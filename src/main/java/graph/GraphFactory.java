package graph;

import graph.components.Edge;
import graph.components.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for generating graphs.
 *
 * @author Brian Yao
 */
public final class GraphFactory {

	/**
	 * Generate a symmetric complete graph with the provided number of nodes. The
	 * graph is shaped like a regular polygon for 3 or more nodes.
	 *
	 * @param numNodes    The number of nodes in our complete graph.
	 * @param constraints The constraints on the graph. Should allow undirected
	 *                    edges.
	 * @return The complete graph.
	 */
	public static Graph completeGraph(int numNodes, int constraints) {
		Graph graph = new Graph(constraints);
		if (numNodes < 1) {
			throw new IllegalArgumentException("Cannot create a complete graph with <1 node.");
		} else if (numNodes == 1) {
			// Complete graph of 1 node is just a single node
			graph.addNode(new Node());
		} else {
			List<Node> generatedNodes = new ArrayList<>();
			for (int i = 0; i < numNodes; i++) {
				Node newNode = new Node();
				generatedNodes.add(newNode);
				graph.addNode(newNode);
			}

			// Add new edges
			for (int i = 0; i < generatedNodes.size() - 1; i++) {
				for (int j = i + 1; j < generatedNodes.size(); j++) {
					graph.addEdge(new Edge(generatedNodes.get(i), generatedNodes.get(j), false));
				}
			}
		}

		return graph;
	}

}
