package actions;

import java.awt.event.ActionEvent;

import components.Edge;
import context.GraphBuilderContext;

/** An instance represents the action of removing an edge. */
public class DeleteEdgeAction extends ReversibleAction {

	private static final long serialVersionUID = -8531176175028658490L;
	
	private Edge edge;
	private int position;
	
	/**
	 * @param ctxt The context in which this action occurs
	 * @param e    The edge being deleted.
	 */
	public DeleteEdgeAction(GraphBuilderContext ctxt, Edge e) {
		super(ctxt);
		edge = e;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		position = getContext().removeEdge(edge);
	}

	@Override
	public void undo() {
		getContext().addEdge(edge, position);
	}
	
}
