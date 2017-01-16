package actions.edit;

import java.awt.event.ActionEvent;

import actions.SimpleAction;
import context.GraphBuilderContext;

/**
 * Action for pushing a new delete action (used by key bindings).
 * 
 * @author Brian
 */
public class PushDelete extends SimpleAction {

	private static final long serialVersionUID = 7530207189429786375L;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public PushDelete(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.getContext().getGUI().getEditor().selectionsEmpty()) {
			Delete deleteAction = new Delete(this.getContext());
			deleteAction.actionPerformed(null);
			this.getContext().pushReversibleAction(deleteAction, true, false);
		}
	}
	
}
