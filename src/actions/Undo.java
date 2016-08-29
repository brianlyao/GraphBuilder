package actions;

import context.GraphBuilderContext;

/** The generic action for undoing the most recent (reversible) action. */
public class Undo extends Action {
	
	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Undo(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void perform() {
		getContext().undoAction();
	}
	
}
