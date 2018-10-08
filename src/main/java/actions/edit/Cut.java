package actions.edit;

import actions.ReversibleAction;
import context.GBContext;

import java.awt.event.ActionEvent;

/**
 * A cut action on graph components (copy and then delete all selected components).
 *
 * @author Brian
 */
public class Cut extends ReversibleAction {

	private static final long serialVersionUID = 4469871982980822405L;

	private Delete deletion;

	private boolean full;

	/**
	 * @param ctxt The context in which this action occurs.
	 * @param full Whether this cut performs a full subgraph copy or not.
	 */
	public Cut(GBContext ctxt, boolean full) {
		super(ctxt);
		this.full = full;
		deletion = new Delete(this.getContext());
		this.getContext().getClipboard().clear();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// Copy selections to clipboard
		new Copy(this.getContext(), full).perform();
		deletion.perform();
	}

	@Override
	public void undo() {
		deletion.undo();
	}

}
