package components;

import java.awt.Color;

import components.display.SelfEdgeData;

import context.GraphBuilderContext;

/** An edge whose endpoints are the same node. */
public class SelfEdge extends Edge {
	
	/**
	 * @param node     The node which is the start and end node of this self-edge.
	 * @param c        The edge's visual color.
	 * @param w        The edge's visual weight (thickness).
	 * @param ctxt     The context (graph) this edge exists in.
	 * @param directed Whether this edge is directed.
	 * @param id       The id this node is assigned.
	 */
	public SelfEdge(Node node, Color c, int w, double offsetAngle, boolean directed, GraphBuilderContext ctxt, int id) {
		super(node, node, new SelfEdgeData(c, w, offsetAngle), directed, ctxt, id);
	}
	
	/**
	 * Get the data for this self edge's display.
	 * 
	 * @return The SelfEdgeData object corresponding to this self edge.
	 */
	@Override
	public SelfEdgeData getData() {
		return (SelfEdgeData) super.getData();
	}
	
	@Override
	public String toStorageString() {
		return super.toStorageString() + "," + this.getData().getOffsetAngle();
	}

}
