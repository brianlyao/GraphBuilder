package actions;

import context.GBContext;
import graph.components.gb.GBNode;

import java.awt.event.ActionEvent;

/**
 * An instance represents the action of placing a new node on the editor panel.
 *
 * @author Brian
 */
public class PlaceNodeAction extends ReversibleAction {

	private static final long serialVersionUID = -4739767659367563555L;

	// The node being placed
	private GBNode node;

	/**
	 * @param ctxt The context in which this action occurs.
	 * @param n    The node which was just placed.
	 */
	public PlaceNodeAction(GBContext ctxt, GBNode n) {
		super(ctxt);
		this.node = n;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.getContext().addNode(node);
	}

	@Override
	public void undo() {
		this.getContext().removeNode(node);
	}

}
