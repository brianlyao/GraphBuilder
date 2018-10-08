package actions.file;

import actions.SimpleAction;
import context.GBContext;
import util.FileUtils;

import java.awt.event.ActionEvent;

/**
 * The action of saving a graph to a file.
 *
 * @author Brian
 */
public class Save extends SimpleAction {

	private static final long serialVersionUID = -5650934166905149191L;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public Save(GBContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		FileUtils.saveFileProcedure(this.getContext());
	}

}
