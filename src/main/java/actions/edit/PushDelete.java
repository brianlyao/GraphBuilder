package actions.edit;

import actions.SimpleAction;
import context.GBContext;

import java.awt.event.ActionEvent;

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
	public PushDelete(GBContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.getContext().getGUI().getEditor().getData().selectionsEmpty()) {
			Delete deleteAction = new Delete(this.getContext());
			deleteAction.perform();
			this.getContext().pushReversibleAction(deleteAction, true, false);
		}
	}

}
