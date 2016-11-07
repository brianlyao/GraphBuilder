package actions;

import java.awt.event.ActionEvent;

import context.GraphBuilderContext;

/** The generic action for undoing the most recent (reversible) action. */
public class Undo extends SimpleAction {
	
	private static final long serialVersionUID = -776076209662033767L;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Undo(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!getContext().getActionHistory().isEmpty()) {
			ReversibleAction action = getContext().getActionHistory().pop();
			action.undo();
			getContext().pushReversibleUndoAction(action, true);
		}
	}
	
}
