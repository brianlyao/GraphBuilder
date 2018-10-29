package actions;

import context.GBContext;
import graph.components.gb.GBEdge;

import java.awt.event.ActionEvent;
import java.util.List;

/**
 * An instance represents the action of placing a new node on the editor panel.
 *
 * @author Brian Yao
 */
public class PlaceEdgeAction extends ReversibleAction {

	private static final long serialVersionUID = -8798668276389320870L;

	private GBEdge edge;
	private int position;

	/**
	 * @param ctxt The context in which this action occurs
	 * @param e    The edge being placed.
	 * @param i    The position of the edge relative to other edges sharing the same endpoints.
	 */
	public PlaceEdgeAction(GBContext ctxt, GBEdge e, int i) {
		super(ctxt);
		this.edge = e;
		this.position = i;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<GBEdge> edges = this.getContext().getEdgesBetweenNodes(edge.getUoEndpoints());
		if (edges != null && position > edges.size()) {
			throw new IllegalArgumentException("Cannot place an edge at index " + position);
		}
		this.getContext().addEdge(edge, position);
		this.getContext().getGUI().getEditor().repaint();
	}

	@Override
	public void undo() {
		position = this.getContext().removeEdge(edge);
		this.getContext().getGUI().getEditor().repaint();
	}

}
