package actions.edit;

import java.awt.event.ActionEvent;

import actions.SimpleAction;
import context.GraphBuilderContext;

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
	public PushPaste(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!this.getContext().getClipboard().isEmpty()) {
			Paste pasteAction = new Paste(this.getContext());
			pasteAction.actionPerformed(null);
			this.getContext().pushReversibleAction(pasteAction, true, false);
		}
	}
	
}