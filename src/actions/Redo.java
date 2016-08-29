package actions;

import context.GraphBuilderContext;

/** The generic action for redoing the most recent (reversible) undone action. */
public class Redo extends Action {
	
	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Redo(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void perform() {
		getContext().redoAction();
	}
	
}