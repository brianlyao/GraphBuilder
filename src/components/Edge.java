package components;

import components.display.EdgeData;

import context.GraphBuilderContext;

/** An instance is an edge of the graph. Visually represented by a line, quadratic bezier curve, or loop. */
public abstract class Edge extends GraphComponent {
	
	// Endpoint nodes of the edge. For directed edges, c1 is the source, c2 is the sink
	private Node n1;
	private Node n2;
	
	// Data for this edge's visual appearance
	private EdgeData data;
	
	// Whether this edge is directed
	private boolean directed;
	
	/** Create a new edge. 
	 * 
	 * @param n1       A node.
	 * @param n2       Another node. We draw this edge from c1 to c2.
	 * @param ctxt     The context (graph) this edge exists in.
	 * @param directed Whether this edge is directed.
	 * @param id       The id this node is assigned.
	 */
	public Edge(Node n1, Node n2, EdgeData data, boolean directed, GraphBuilderContext ctxt, int id) {
		super(ctxt, id);
		this.n1 = n1;
		this.n2 = n2;
		this.data = data;
		this.directed = directed;
	}
	
	public EdgeData getData() {
		return data;
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
		return c == n1 || c == n2;
	}
	
	/** 
	 * Get the endpoints of this edge.
	 * 
	 * @return An array of two nodes containing both endpoints.
	 */
	public Node[] getEndpoints() {
		return new Node[] {n1, n2};
	}
	
	public String toString() {
		return String.format("Edge[id=%d, node1id=%d, node2id=%d, color=%s, weight=%d, directed=%s]", getID(), n1.getID(), n2.getID(), data.getColor(), data.getWeight(), directed);
	}
	
	public String toStorageString() {
		return String.format("E:%d,%d,%d,%d,%d,%d", getID(), n1.getID(), n2.getID(), data.getColor().getRGB(), data.getWeight(), directed ? 1 : 0);
	}
	
}
