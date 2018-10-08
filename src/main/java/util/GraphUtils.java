package util;

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

}
