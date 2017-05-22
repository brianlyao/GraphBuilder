package components;

import components.display.SelfEdgeData;

import context.GraphBuilderContext;

/**
 * An edge whose endpoints are the same node.
 * 
 * @author Brian
 */
public class SelfEdge extends Edge {
	
	/**
	 * Copy constructor.
	 * 
	 * @param se      The self-edge to copy.
	 * @param newNode The new endpoint of this self-edge, since a "shallow" copy cannot be maintained.
	 */
	public SelfEdge(SelfEdge se, Node newNode) {
		super(newNode, newNode, new SelfEdgeData(se.getData()), se.isDirected(), se.getContext(), se.getContext().getNextIdAndInc());
	}
	
	/**
	 * Instantiate a self-edge with the given visual properties.
	 * 
	 * @param node     The node which is the start and end node of this self-edge.
	 * @param c        The edge's visual color.
	 * @param w        The edge's visual weight (thickness).
	 * @param ctxt     The context (graph) this edge exists in.
	 * @param directed Whether this edge is directed.
	 * @param id       The id this node is assigned.
	 */
	public SelfEdge(Node node, SelfEdgeData data, boolean directed, GraphBuilderContext ctxt, int id) {
		super(node, node, data, directed, ctxt, id);
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
