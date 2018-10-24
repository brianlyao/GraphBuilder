package util;

import graph.Graph;
import graph.components.Edge;
import graph.components.Node;
import graph.components.WeightedEdge;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A class of utility functions used for graph-related operations.
 *
 * @author Brian Yao
 */
public class GraphUtils {

	/**
	 * Gets the minimum weight edge out of all edges whose endpoints are the
	 * two specified nodes. If there are multiple edges with minimum weight,
	 * the choice is unspecified.
	 *
	 * @param n1             First endpoint.
	 * @param n2             Second endpoint.
	 * @param followDirected True if we want to ignore directed edges from n2
	 *                       to n1, false otherwise.
	 * @return The edge between n1 and n2 with minimum weight.
	 */
	public static Edge minWeightEdge(Node n1, Node n2, boolean followDirected) {
		Set<Edge> withNeighbor = n1.getEdgesToNeighbor(n2, followDirected);
		if (withNeighbor == null) {
			throw new NoSuchElementException(String.format("There are no edges"
															   + " between nodes %s and %s.", n1, n2));
		}

		Edge minWeightEdge = null;
		double minWeight = Double.POSITIVE_INFINITY;
		for (Edge edge : withNeighbor) {
			double edgeWeight = edge.getNumericWeight();
			if (edgeWeight < minWeight) {
				minWeight = edgeWeight;
				minWeightEdge = edge;
			}
		}

		return minWeightEdge;
	}

	/**
	 * Gets an adjacency list representation of the given graph in a multi-line
	 * string. The format for each node is:
	 * <p>
	 * nodeId:neighbor0,neighbor1,neighbor2,...
	 * <p>
	 * where each value is an integer ID. If there are no neighbors, there is
	 * no colon.
	 *
	 * @param graph The graph to obtain an adjacency list representation of.
	 * @return The adjacency list for the given graph.
	 */
	public String asAdjacencyList(Graph graph) {
		StringBuilder adjList = new StringBuilder();
		for (Node n : graph.getNodes()) {
			adjList.append(n.getId());
			adjList.append(':');
			for (Node neighbor : n.getNeighbors(true)) {
				adjList.append(neighbor.getId());
				adjList.append(',');
			}
			adjList.replace(adjList.length() - 1, 1, "\n");
		}

		return adjList.toString();
	}

}
