package actions.edit;

import actions.SimpleAction;
import context.GBContext;

import java.awt.event.ActionEvent;

/**
 * Action for pushing a new paste action (used by key bindings).
 *
 * @author Brian
 */
public class PushPaste extends SimpleAction {

	private static final long serialVersionUID = 4179382598790615640L;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public PushPaste(GBContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.getContext().getClipboard().isEmpty()) {
			Paste pasteAction = new Paste(this.getContext());
			pasteAction.perform();
			this.getContext().pushReversibleAction(pasteAction, true, false);
		}
	}

}