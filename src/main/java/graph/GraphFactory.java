package graph;

import graph.components.Edge;
import graph.components.Node;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for generating graphs.
 *
 * @author Brian Yao
 */
public final class GraphFactory {

	/**
	 * Generate a complete undirected graph with the provided number of nodes.
	 *
	 * @param numNodes    The number of nodes in our complete graph.
	 * @param startId     The smallest ID of any component in this graph.
	 *                    Consecutive ID's will be used.
	 * @return The complete graph and the next available ID.
	 */
	public static Pair<Graph, Integer> completeGraph(int numNodes, int startId) {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		if (numNodes < 1) {
			throw new IllegalArgumentException("Cannot create a complete graph with <1 node.");
		} else if (numNodes == 1) {
			// Complete graph of 1 node is just a single node
			graph.addNode(new Node(startId++));
		} else {
			List<Node> generatedNodes = new ArrayList<>();
			for (int i = 0; i < numNodes; i++) {
				Node newNode = new Node(startId++);
				generatedNodes.add(newNode);
				graph.addNode(newNode);
			}

			// Add new edges
			for (int i = 0 ; i < generatedNodes.size() - 1 ; i++) {
				for (int j = i + 1 ; j < generatedNodes.size() ; j++) {
					graph.addEdge(new Edge(startId++, generatedNodes.get(i), generatedNodes.get(j), false));
				}
			}
		}

		return new Pair<>(graph, startId);
	}

}
