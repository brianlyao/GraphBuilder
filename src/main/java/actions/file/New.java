package actions.file;

import actions.SimpleAction;
import context.GBContext;
import util.FileUtils;

import java.awt.event.ActionEvent;

/**
 * The action of creating a new graph.
 *
 * @author Brian Yao
 */
public class New extends SimpleAction {

	private static final long serialVersionUID = 4771301213349828140L;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public New(GBContext ctxt) {
		super(ctxt);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		FileUtils.newFileProcedure(this.getContext());
	}

}
