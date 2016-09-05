package components;

import java.awt.Color;

import context.GraphBuilderContext;
import ui.Editor;

/** An instance is an edge of the graph. Visually represented by a line, quadratic bezier curve, or loop. */
public abstract class Edge extends GraphComponent {
	
	private static final long serialVersionUID = -6840630886505529490L;
	
	private Editor editor; // The editor in which this edge is displayed
	
	// Endpoint nodes of the edge. For directed edges, c1 is the source, c2 is the sink
	private Node n1;
	private Node n2;
	
	private Color color; // Edge color
	private int weight; // Weight (thickness) of the line
	private String text; // Text to display next to the edge
	
	private boolean directed; // Whether this edge is directed
	
	/** Create a new edge. 
	 * 
	 * @param n1       A node.
	 * @param n2       Another node. We draw this edge from c1 to c2.
	 * @param c        The edge's visual color.
	 * @param w        The edge's visual weight (thickness).
	 * @param ctxt     The context (graph) this edge exists in.
	 * @param directed Whether this edge is directed.
	 * @param id       The id this node is assigned.
	 */
	public Edge(Node n1, Node n2, Color c, int w, boolean directed, GraphBuilderContext ctxt, int id) {
		super(ctxt, id);
		this.n1 = n1;
		this.n2 = n2;
		this.color = c;
		this.weight = w;
		this.directed = directed;
		this.editor = ctxt.getGUI().getEditor();
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param e The edge we want to copy.
	 */
	public Edge(Edge e) {
		super(e.getContext());
		this.n1 = e.n1;
		this.n2 = e.n2;
		this.color = e.color;
		this.weight = e.weight;
		this.editor = e.editor;
		this.directed = e.directed;
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
	
	/**
	 * Get the display color of this edge.
	 * 
	 * @return The Color object containing the edge's color.
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Get the display weight (thickness) of this edge.
	 * 
	 * @return The integer weight of this edge.
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * Get whether this edge is directed.
	 * 
	 * @return true if the edge is directed, false otherwise.
	 */
	public boolean isDirected() {
		return directed;
	}
	
	public String toString() {
		return String.format("Edge[id=%d, node1id=%d, node2id=%d, color=%s, weight=%d, directed=%s]", getID(), n1.getID(), n2.getID(), color, weight, directed);
	}
	
	public String toStorageString() {
		return String.format("E:%d,%d,%d,%d,%d,%d", getID(), n1.getID(), n2.getID(), color.getRGB(), weight, directed ? 1 : 0);
	}
	
}
