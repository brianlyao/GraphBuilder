package components;

import java.awt.Color;

import structures.OrderedPair;
import components.display.EdgeData;
import context.GraphBuilderContext;

/**
 * An instance represents an edge component of a graph.
 * 
 * @author Brian Yao
 */
public abstract class Edge extends GraphComponent {
	
	public static final String DEFAULT_TEXT = "";
	
	// Endpoint nodes of the edge. For directed edges, c1 is the source, c2 is the sink
	private OrderedPair<Node> endpoints;
	
	// Data for this edge's visual appearance
	private EdgeData data;
	
	// Whether this edge is directed
	private boolean directed;
	
	/** Create a new edge. 
	 * 
	 * @param n1       A node endpoint.
	 * @param n2       Another node endpoint. We draw this edge from c1 to c2.
	 * @param data     The data object associated with this Edge.
	 * @param directed Whether this edge is directed.
	 * @param ctxt     The context (graph) this edge exists in.
	 * @param id       The id this node is assigned.
	 */
	public Edge(Node n1, Node n2, EdgeData data, boolean directed, GraphBuilderContext ctxt, int id) {
		super(ctxt, id);
		this.endpoints = new OrderedPair<Node>(n1, n2);
		this.data = data;
		this.directed = directed;
	}
	
	/**
	 * Get the edge data associated with this edge.
	 * 
	 * @return The EdgeData object.
	 */
	public EdgeData getData() {
		return data;
	}
	
	/**
	 * Set the directed nature of this edge.
	 * 
	 * @param directed true iff we want to set the edge to be directed.
	 */
	public void setDirected(boolean directed) {
		this.directed = directed;
	}
	
	/**
	 * Get whether this edge is directed.
	 * 
	 * @return true if the edge is directed, false otherwise.
	 */
	public boolean isDirected() {
		return directed;
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
	 * Get the endpoints of this edge.
	 * 
	 * @return An array of two nodes containing both endpoints.
	 */
	public OrderedPair<Node> getEndpoints() {
		return endpoints;
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
	
	@Override
	public String toString() {
		Node n1 = endpoints.getFirst();
		Node n2 = endpoints.getSecond();
		Color color = data == null ? null : data.getColor();
		int weight = data == null ? -1 : data.getWeight();
		return String.format("Edge[id=%d, node1id=%d, node2id=%d, color=%s,"
				+ " weight=%d, directed=%s]", getID(), n1.getID(), n2.getID(),
				color, weight, directed);
	}
	
	@Override
	public String toStorageString() {
		Node n1 = endpoints.getFirst();
		Node n2 = endpoints.getSecond();
		return String.format("E:%d,%d,%d,%d,%d,%d,%s", getID(), n1.getID(),
				n2.getID(), data.getColor().getRGB(), data.getWeight(),
				directed ? 1 : 0, data.getText());
	}
	
}
