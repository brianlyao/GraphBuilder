package actions;

import context.GraphBuilderContext;

/** A reversible form of an action, such as placing a node or drawing an edge. */
public abstract class ReversibleAction extends SimpleAction {
	
	private static final long serialVersionUID = 7518755420430185279L;

	/**
	 * Initialize an Action within a context.
	 * 
	 * @param ctxt The context in which this action occurs.
	 */
	public ReversibleAction(GraphBuilderContext ctxt) {
		super(ctxt);
	}
	
	/**
	 * Used to add this action to the history.
	 */
	public void addSelfToHistory() {
		getContext().getActionHistory().push(this);
	}
	
	/**
	 * Used to add this action to the undo history;
	 */
	public void addSelfToUndoHistory() {
		getContext().getUndoHistory().push(this);
	}
	
	/** Abstract method for undoing this particular action. */
	public abstract void undo();
	
}
