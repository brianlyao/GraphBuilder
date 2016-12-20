package actions.edit;

import java.awt.event.ActionEvent;

import actions.ReversibleAction;
import actions.SimpleAction;
import context.GraphBuilderContext;

/**
 * The generic action for redoing the most recent (reversible) undone action.
 * 
 * @author Brian
 */
public class Redo extends SimpleAction {
	
	private static final long serialVersionUID = -7254167000031398300L;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Redo(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!getContext().getUndoHistory().isEmpty()) {
			ReversibleAction action = getContext().getUndoHistory().pop();
			action.actionPerformed(null);
			getContext().pushReversibleAction(action, true, true);
		}
	}
	
}