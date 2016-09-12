package actions.file;

import java.awt.event.ActionEvent;

import actions.SimpleAction;
import util.FileUtils;
import context.GraphBuilderContext;

/** The action of saving a graph to a file. */
public class Save extends SimpleAction {
	
	private static final long serialVersionUID = -5650934166905149191L;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Save(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		FileUtils.saveFileProcedure(getContext());
	}

}
