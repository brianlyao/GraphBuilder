package actions;

import components.Node;
import context.GraphBuilderContext;

/** An instance represents the action of placing a new node on the editor panel. */
public class PlaceNodeAction extends ReversibleAction {

	private Node node;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param n    The node which was just placed.
	 */
	public PlaceNodeAction(GraphBuilderContext ctxt, Node n) {
		super(ctxt);
		node = n;
	}
	
	public void perform() {
		this.getContext().getGUI().getEditor().addNode(node);
	}
	
	public void undo() {
		this.getContext().getGUI().getEditor().removeNode(node);
	}
	
}
