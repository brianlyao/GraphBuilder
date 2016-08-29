package actions;

import context.GraphBuilderContext;

/** An instance is an action which can be performed by the user. */
public abstract class Action {
	
	private GraphBuilderContext context;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Action(GraphBuilderContext ctxt) {
		context = ctxt;
	}
	
	public GraphBuilderContext getContext() {
		return context;
	}
	
	public abstract void perform();
	
}
