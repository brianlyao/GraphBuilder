package components;

import components.display.SimpleEdgeData;
import context.GraphBuilderContext;

/**
 * An edge whose endpoints are two distinct nodes.
 * 
 * @author Brian
 */
public class SimpleEdge extends Edge {
	
	/**
	 * Copy constructor. Requires the new endpoint nodes for the copied edge.
	 * 
	 * @param se        The simple edge to copy.
	 * @param firstEnd  The new first endpoint of this edge.
	 * @param secondEnd The new second endpoint of this edge.
	 */
	public SimpleEdge(SimpleEdge se, Node firstEnd, Node secondEnd) {
		super(firstEnd, secondEnd, se.getData() == null ? null : new SimpleEdgeData(se.getData()), se.isDirected(), se.getContext(), se.getContext().getNextIdAndInc());
	}
	
	/**
	 * @param n1       The first node endpoint.
	 * @param n2       The second node endpoint.
	 * @param data     The display data associated with this edge.
	 * @param directed Whether this edge is directed.
	 * @param ctxt     The context (graph) this edge exists in.
	 */
	public SimpleEdge(Node n1, Node n2, SimpleEdgeData data, boolean directed, GraphBuilderContext ctxt, int id) {
		super(n1, n2, data, directed, ctxt, id);
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
