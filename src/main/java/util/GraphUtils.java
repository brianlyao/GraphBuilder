package util;

import graph.Graph;
import graph.components.Edge;
import graph.components.Node;
import graph.components.WeightedEdge;

import java.util.Collections;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A class of utility functions used for graph-related operations.
 *
 * @author Brian Yao
 */
public class GraphUtils {

	/**
	 * Gets the minimum weight edge with the specified endpoints. If there
	 * are multiple edges with minimum weight, the choice is unspecified.
	 *
	 * @param graph          The graph to get the minimum weight edge from.
	 * @param n1             First endpoint.
	 * @param n2             Second endpoint.
	 * @param followDirected True if we want to ignore directed edges from n2
	 *                       to n1, false otherwise.
	 * @return The edge between n1 and n2 with minimum weight.
	 * @throws NoSuchElementException if there are no edges between n1 and n2.
	 */
	public static Edge minWeightEdge(Graph graph, Node n1, Node n2, boolean followDirected) {
		Set<Edge> withNeighbor = graph.getAdjListOf(n1).getEdgesToNeighbor(n2, followDirected);
		if (withNeighbor == null) {
			throw new NoSuchElementException(String.format("There are no edges between nodes %s and %s.", n1, n2));
		}

		return Collections.min(withNeighbor, Comparator.comparingDouble(Edge::getNumericWeight));
	}

	/**
	 * Gets an arbitrary edge with the specified endpoints.
	 *
	 * @param graph          The graph to get the minimum weight edge from.
	 * @param n1             First endpoint.
	 * @param n2             Second endpoint.
	 * @param followDirected True if we want to ignore directed edges from n2
	 *                       to n1, false otherwise.
	 * @return The edge between n1 and n2 with minimum weight.
	 * @throws NoSuchElementException if there are no edges between n1 and n2.
	 */
	public static Edge arbitraryEdge(Graph graph, Node n1, Node n2, boolean followDirected) {
		Set<Edge> withNeighbor = graph.getAdjListOf(n1).getEdgesToNeighbor(n2, followDirected);
		if (withNeighbor == null) {
			throw new NoSuchElementException(String.format("There are no edges between nodes %s and %s.", n1, n2));
		}

		return StructureUtils.arbitraryElement(withNeighbor);
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
			for (Node neighbor : graph.getAdjListOf(n).getNeighbors(true)) {
				adjList.append(neighbor.getId());
				adjList.append(',');
			}
			adjList.replace(adjList.length() - 1, 1, "\n");
		}

		return adjList.toString();
	}

}
