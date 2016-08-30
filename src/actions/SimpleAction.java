package actions;

import javax.swing.AbstractAction;

import context.GraphBuilderContext;

/** An instance is an action which can be performed by the user. */
public abstract class SimpleAction extends AbstractAction {
	
	private static final long serialVersionUID = 2049389041008055018L;
	
	private GraphBuilderContext context;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public SimpleAction(GraphBuilderContext ctxt) {
		context = ctxt;
	}
	
	public GraphBuilderContext getContext() {
		return context;
	}
	
}
