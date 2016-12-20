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
	 * Copy constructor.
	 *
	 * @param action The reversible action to copy.
	 */
	public ReversibleAction(ReversibleAction action) {
		super(action.getContext());
	}
	
	/** Abstract method for undoing this particular action. */
	public abstract void undo();
	
}
