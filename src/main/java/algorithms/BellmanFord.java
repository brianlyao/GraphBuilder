package algorithms;

import exception.NegativeCycleException;
import graph.Graph;
import graph.components.Edge;
import graph.components.Node;
import graph.path.Path;
import util.GraphUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 * An implementation of the Bellman-Ford algorithm for finding single-source
 * shortest paths.
 *
 * @author Brian Yao
 */
public final class BellmanFord {

	/**
	 * An implementation of the Bellman-Ford algorithm, which finds the
	 * shortest path from the start node to all other nodes in the graph.
	 *
	 * @param graph       The graph to find the shortest path in.
	 * @param start       The start node of the path.
	 * @param destination The destination node of the path.
	 * @return The shortest path found by the Bellman-Ford algorithm.
	 * @throws NegativeCycleException if the graph contains a negative cycle
	 *                                so that the shortest path does not exist.
	 */
	public static Path execute(Graph graph, Node start, Node destination) {
		Set<Node> reachableFromStart = validateBellmanFordInput(graph, start, destination);

		if (!reachableFromStart.contains(destination)) {
			return null;
		}

		// Initialize memoization array and keep track of next edge in the
		// shortest path given a node
		Map<Node, Double> distances = new HashMap<>();
		Map<Node, Edge> next = new HashMap<>();
		graph.getNodes().forEach(node -> distances.put(node, Double.POSITIVE_INFINITY));
		distances.put(destination, 0.);

		// Compute shortest path distances from start, |V| - 1 iterations
		IntStream.range(1, graph.getNodes().size()).forEach($ ->
			forEachEdge(graph, distances, (node, edge) -> {
				Node neighbor = edge.getOtherEndpoint(node);
				next.put(node, edge);
				distances.put(node, edge.getNumericWeight() + distances.get(neighbor));
			})
		);

		// Check for negative cycles
		forEachEdge(graph, distances, (node, $) -> {
			// If distances would be updated, there must be a negative cycle
			// with a path to the destination node. The path from the node
			// being iterated to the destination node must contain a negative
			// cycle.
			if (reachableFromStart.contains(node)) {
				// The negative cycle is reachable from the start node, so
				// there is no shortest path.
				throw new NegativeCycleException("The shortest path does not exist as the graph contains a " +
													 "negative cycle reachable from the start node and which " +
													 "can reach the destination node.", next, node);
			}
		});

		// Construct shortest path from start to destination
		Path shortestPath = new Path(start);
		Node currentNode = start;
		while (next.get(currentNode) != null) {
			Edge toNextNode = next.get(currentNode);
			currentNode = toNextNode.getOtherEndpoint(currentNode);
			shortestPath.appendNode(currentNode, toNextNode);
		}

		return shortestPath;
	}

	/**
	 * Helper method to iterate through nodes and perform the specified
	 * operation when the "distances" value is updated for that node.
	 *
	 * The onUpdate consumer takes two pieces of data: the node which
	 * is currently being iterated on, and the edge to the node's neighbor
	 * being iterated on.
	 *
	 * @param graph     The graph to iterate through.
	 * @param distances The memoization map containing distance values to
	 *                  the destination.
	 * @param onUpdate  The BiConsumer action to perform when the distance
	 *                  value to a node's neighbor would replace the current
	 *                  distance value of that node.
	 */
	private static void forEachEdge(Graph graph, Map<Node, Double> distances,
									BiConsumer<Node, Edge> onUpdate) {
		for (Node node : graph.getNodes()) {
			for (Node neighbor : graph.getAdjListOf(node).getNeighbors(true)) {
				// Use the minimum weight edge between node and neighbor
				Edge minWeightEdge = GraphUtils.minWeightEdge(graph, node, neighbor, true);

				// Compute the new value to put in the memo map
				Double currentDist = distances.get(node);
				Double neighborDist = minWeightEdge.getNumericWeight() + distances.get(neighbor);
				if (neighborDist < currentDist) {
					onUpdate.accept(node, minWeightEdge);
				}
			}
		}
	}

	/**
	 * Helper function for validating the input of the Bellman-Ford algorithm.
	 *
	 * @param graph       Input graph.
	 * @param start       Input start node.
	 * @param destination Input destination node.
	 * @return the set of nodes reachable from the start node.
	 */
	private static Set<Node> validateBellmanFordInput(Graph graph, Node start, Node destination) {
		if (!graph.containsNode(start) || !graph.containsNode(destination)) {
			throw new IllegalArgumentException("Start and destination nodes must belong to the provided graph.");
		}

		// Check if there are any negative undirected edges which could be on
		// a path from the start to the destination
		Set<Node> reachableFromStart = DFS.explore(graph, start, true);
		Set<Edge> negativeEdges = new HashSet<>();
		graph.getEdgeSet().forEach(edge -> {
			if (!edge.isDirected() &&
				reachableFromStart.contains(edge.getFirstEnd()) &&
				edge.getNumericWeight() < 0. &&
				BFS.connected(graph, edge.getFirstEnd(), destination, true)) {
				negativeEdges.add(edge);
			}
		});

		if (!negativeEdges.isEmpty()) {
			throw new NegativeCycleException("Contains negative 2-cycle(s) (undirected edges with negative " +
												 "weights) reachable from the start that can reach the " +
												 "destination. Bellman-Ford cannot be executed.", negativeEdges);
		}

		return reachableFromStart;
	}

}
