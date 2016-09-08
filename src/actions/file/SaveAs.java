package actions.file;

import java.awt.event.ActionEvent;

import actions.SimpleAction;
import util.GraphBuilderUtils;
import context.GraphBuilderContext;

/** The action of "saving as" a graph to a file. */
public class SaveAs extends SimpleAction {

	private static final long serialVersionUID = -3695898506602800383L;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public SaveAs(GraphBuilderContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		GraphBuilderUtils.saveAsFileProcedure(getContext());
	}
	
}
