package actions;

import java.awt.Point;

import components.Node;
import context.GraphBuilderContext;

/** An instance represents the action of moving a node on the editor panel.*/
public class MoveNodeAction extends ReversibleAction {

	private Node node;
	private Point fromPoint;
	private Point toPoint;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param n    The node which was moved.
	 * @param from The point the node was moved from.
	 * @param to   The point the node was moved to.
	 */
	public MoveNodeAction(GraphBuilderContext ctxt, Node n, Point from, Point to) {
		super(ctxt);
		node = n;
		fromPoint = from;
		toPoint = to;
	}
	
	public void perform() {
		node.setLocation(toPoint);
	}
	
	public void undo() {
		node.setLocation(fromPoint);
	}
	
	public String toString() {
		return String.format("MoveNodeAction[from=%s, to=%s]", fromPoint, toPoint);
	}
	
}
