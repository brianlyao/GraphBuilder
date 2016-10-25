package actions;

import java.awt.Point;
import java.awt.event.ActionEvent;

import components.display.NodePanel;
import context.GraphBuilderContext;

/** An instance represents the action of moving a node on the editor panel.*/
public class MoveNodeAction extends ReversibleAction {

	private static final long serialVersionUID = 5561193877847969890L;
	
	private NodePanel nodePanel;
	private Point fromPoint;
	private Point toPoint;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param n    The node which was moved.
	 * @param from The point the node was moved from.
	 * @param to   The point the node was moved to.
	 */
	public MoveNodeAction(GraphBuilderContext ctxt, NodePanel np, Point from, Point to) {
		super(ctxt);
		nodePanel = np;
		fromPoint = from;
		toPoint = to;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		nodePanel.setCoords(toPoint);
		getContext().getGUI().getEditor().repaint();
		addSelfToHistory();
		getContext().updateSaveState();
	}
	
	@Override
	public void undo() {
		nodePanel.setCoords(fromPoint);
		getContext().getGUI().getEditor().repaint();
		addSelfToUndoHistory();
		getContext().updateSaveState();
	}
	
	public String toString() {
		return String.format("MoveNodeAction[from=%s, to=%s]", fromPoint, toPoint);
	}
	
}
