package components;

import java.awt.Color;

import components.display.SimpleEdgeData;

import context.GraphBuilderContext;

/** An edge whose endpoints are two distinct nodes. */
public class SimpleEdge extends Edge {
	
	/**
	 * @param n1       The first node endpoint.
	 * @param n2       The second node endpoint.
	 * @param c        The edge's color.
	 * @param w        The edge's weight.
	 * @param directed Whether this edge is directed.
	 * @param ctxt     The context (graph) this edge exists in.
	 * 
	 */
	public SimpleEdge(Node n1, Node n2, Color c, int w, boolean directed, GraphBuilderContext ctxt, int id) {
		super(n1, n2, new SimpleEdgeData(c, w), directed, ctxt, id);
	}
	
	/**
	 * Get the data for this simple edge's display.
	 * 
	 * @return The SimpleEdgeData object corresponding to this simple edge.
	 */
	@Override
	public SimpleEdgeData getData() {
		return (SimpleEdgeData) super.getData();
	}
	
	@Override
	public String toStorageString() {
		return super.toStorageString();
	}

}
