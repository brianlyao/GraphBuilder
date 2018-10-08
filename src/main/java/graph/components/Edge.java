package graph.components;

import graph.components.gb.GBEdge;
import lombok.Getter;
import lombok.Setter;
import structures.OrderedPair;

/**
 * An instance represents an edge component of a graph.
 *
 * @author Brian Yao
 */
public class Edge extends GraphComponent {

	// Endpoint nodes of the edge. For directed edges, n1 is the source, n2 is the sink
	@Getter
	private OrderedPair<Node> endpoints;

	// Whether this edge is directed
	@Getter @Setter
	private boolean directed;

	@Getter @Setter
	private GBEdge gbEdge;

	/**
	 * Create a new edge.
	 *
	 * @param n1       A node endpoint.
	 * @param n2       Another node endpoint. We draw this edge from c1 to c2.
	 * @param directed Whether this edge is directed.
	 */
	public Edge(Node n1, Node n2, boolean directed) {
		this.endpoints = new OrderedPair<>(n1, n2);
		this.directed = directed;
	}

	/**
	 * @return true iff this edge's two endpoints are the same.
	 */
	public boolean isSelfEdge() {
		return this.getFirstEnd() == this.getSecondEnd();
	}

	/**
	 * Returns whether the specified node is one of this edge's endpoints.
	 *
	 * @param c The node we want to check to see if it is one of this edge's endpoints.
	 * @return true if c is an endpoint of this edge, and false otherwise.
	 */
	public boolean hasEndpoint(Node c) {
		return c == endpoints.getFirst() || c == endpoints.getSecond();
	}

	/**
	 * @return the first endpoint (source) of this edge.
	 */
	public Node getFirstEnd() {
		return endpoints.getFirst();
	}

	/**
	 * @return the second endpoint (sink) of this edge.
	 */
	public Node getSecondEnd() {
		return endpoints.getSecond();
	}

	/**
	 * Given a single endpoint of this edge, get the other endpoint node.
	 *
	 * @param endpoint The provided endpoint of this edge.
	 * @return The other endpoint of this edge.
	 * @throws IllegalArgumentException if the provided endpoint is not an endpoint of this edge.
	 */
	public Node getOtherEndpoint(Node endpoint) {
		if (!hasEndpoint(endpoint)) {
			throw new IllegalArgumentException("This edge does not have this node as an endpoint: " + endpoint);
		}
		if (endpoints.getFirst() == endpoint) {
			return endpoints.getSecond();
		}
		return endpoints.getFirst();
	}

	/**
	 * Gets the numeric weight assigned to this edge. If this edge is not an
	 * instance of WeightedEdge, then the weight is 1.0 by default.
	 *
	 * @return the numeric weight if this edge is weighted, 1.0 otherwise.
	 */
	public double getNumericWeight() {
		if (this instanceof WeightedEdge) {
			return ((WeightedEdge) this).getWeight();
		} else {
			return 1.0;
		}
	}

	@Override
	public String toString() {
		return String.format("(%d,%d)", this.getFirstEnd().getId(), this.getSecondEnd().getId());
	}

}
