package actions;

import javax.swing.AbstractAction;

import context.GraphBuilderContext;

/** An instance is an action which can be performed by the user. */
public abstract class SimpleAction extends AbstractAction {
	
	private static final long serialVersionUID = 2049389041008055018L;
	
	private static int actionIdPool = 0;
	
	private GraphBuilderContext context;
	private int id;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public SimpleAction(GraphBuilderContext ctxt) {
		context = ctxt;
		id = actionIdPool++;
	}
	
	/**
	 * Get the context this action occurred in.
	 * 
	 * @return The context object.
	 */
	public GraphBuilderContext getContext() {
		return context;
	}
	
	/**
	 * Get the id of this action.
	 * 
	 * @return The integer id.
	 */
	public int actionId() {
		return id;
	}
	
}
