package actions;

import java.awt.event.ActionEvent;

import components.Edge;
import context.GraphBuilderContext;

/** An instance represents the action of placing a new node on the editor panel. */
public class PlaceEdgeAction extends ReversibleAction {

	private static final long serialVersionUID = -8798668276389320870L;
	
	private Edge edge;
	private int position;
	
	/**
	 * @param ctxt The context in which this action occurs
	 * @param e    The edge being placed.
	 * @param i    The position of the edge relative to other edges sharing the same endpoints.
	 */
	public PlaceEdgeAction(GraphBuilderContext ctxt, Edge e, int i) {
		super(ctxt);
		edge = e;
		position = i;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		getContext().addEdge(edge, position);
		getContext().getGUI().getEditor().repaint();
	}

	@Override
	public void undo() {
		position = getContext().removeEdge(edge);
		getContext().getGUI().getEditor().repaint();
	}
	
}
