package actions;

import java.awt.event.ActionEvent;

import components.Node;

import context.GraphBuilderContext;

/** An instance represents the action of deleting an existing node from the editor panel. */
public class DeleteNodeAction extends ReversibleAction {

	private static final long serialVersionUID = -745312635068655475L;
	
	private Node node;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param n    The node which was deleted.
	 */
	public DeleteNodeAction(GraphBuilderContext ctxt, Node n) {
		super(ctxt);
		node = n;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		getContext().getGUI().getEditor().removeNode(node);
		addSelfToHistory();
	}
	
	@Override
	public void undo() {
		getContext().getGUI().getEditor().addNode(node);
		addSelfToUndoHistory();
	}
	
}
