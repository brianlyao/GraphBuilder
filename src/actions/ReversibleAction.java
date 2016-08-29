package actions;

import context.GraphBuilderContext;

/** A reversible form of an action, such as placing a node or drawing an edge. */
public abstract class ReversibleAction extends Action {
	
	/**
	 * Initialize an Action within a context.
	 * 
	 * @param ctxt The context in which this action occurs.
	 */
	public ReversibleAction(GraphBuilderContext ctxt) {
		super(ctxt);
	}
	
	/** Abstract method for undoing this particular action. */
	public abstract void undo();
	
}
