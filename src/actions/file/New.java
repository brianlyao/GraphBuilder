package actions.file;

import java.awt.event.ActionEvent;

import util.FileUtils;
import context.GraphBuilderContext;
import actions.SimpleAction;

/**
 * The action of creating a new graph.
 * 
 * @author Brian
 */
public class New extends SimpleAction {

	private static final long serialVersionUID = 4771301213349828140L;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public New(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		FileUtils.newFileProcedure(this.getContext());
	}
	
}
