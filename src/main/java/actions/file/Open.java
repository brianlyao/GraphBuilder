package actions.file;

import actions.SimpleAction;
import context.GBContext;
import util.FileUtils;

import java.awt.event.ActionEvent;

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
	public Open(GBContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		FileUtils.openFileProcedure(this.getContext());
	}

}
