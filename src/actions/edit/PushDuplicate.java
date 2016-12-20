package actions.edit;

import java.awt.event.ActionEvent;

import context.GraphBuilderContext;
import actions.SimpleAction;

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
	public PushDuplicate(GraphBuilderContext ctxt, boolean full) {
		super(ctxt);
		this.full = full;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.getContext().getGUI().getEditor().getSelections().isEmpty()) {
			Duplicate duplicateAction = new Duplicate(this.getContext(), full);
			duplicateAction.actionPerformed(null);
			this.getContext().pushReversibleAction(duplicateAction, true);
		}
	}
	
}
