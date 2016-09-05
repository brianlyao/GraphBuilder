package actions;

import java.awt.event.ActionEvent;

import components.Node;
import context.GraphBuilderContext;

/** An instance represents the action of placing a new node on the editor panel. */
public class PlaceNodeAction extends ReversibleAction {

	private static final long serialVersionUID = -4739767659367563555L;
	
	private Node node;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param n    The node which was just placed.
	 */
	public PlaceNodeAction(GraphBuilderContext ctxt, Node n) {
		super(ctxt);
		node = n;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		getContext().addNode(node);
		addSelfToHistory();
		getContext().updateSaveState();
	}
	
	@Override
	public void undo() {
		getContext().removeNode(node);
		addSelfToUndoHistory();
		getContext().updateSaveState();
	}
	
}
