package actions.edit;

import actions.SimpleAction;
import context.GBContext;

import java.awt.event.ActionEvent;

/**
 * Action for pushing a new duplicate action (used by key bindings).
 *
 * @author Brian
 */
public class PushDuplicate extends SimpleAction {

	private static final long serialVersionUID = -6263632025936834310L;

	private boolean full;

	/**
	 * @param ctxt The context in which this action occurs.
	 * @param full Whether the duplicate is a full subgraph duplicate.
	 */
	public PushDuplicate(GBContext ctxt, boolean full) {
		super(ctxt);
		this.full = full;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.getContext().getGUI().getEditor().getData().selectionsEmpty()) {
			Duplicate duplicateAction = new Duplicate(this.getContext(), full);
			duplicateAction.perform();
			this.getContext().pushReversibleAction(duplicateAction, true, false);
		}
	}

}
