package actions;

import java.awt.event.ActionEvent;

import context.GraphBuilderContext;

/** The generic action for redoing the most recent (reversible) undone action. */
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
		if(!getContext().getUndoHistory().isEmpty())
			getContext().getUndoHistory().pop().actionPerformed(null);
	}
	
}