package actions.file;

import java.awt.event.ActionEvent;

import actions.SimpleAction;
import util.FileUtils;
import context.GraphBuilderContext;

/**
 * The action of opening a graph from a file.
 * 
 * @author Brian
 */
public class Open extends SimpleAction {
	
	private static final long serialVersionUID = -2252107475208776493L;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Open(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		FileUtils.openFileProcedure(this.getContext());
	}
	
}
