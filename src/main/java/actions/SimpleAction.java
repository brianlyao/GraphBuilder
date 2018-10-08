package actions;

import context.GBContext;
import lombok.Getter;

import javax.swing.*;

/**
 * An abstract class for any action which can be performed by the user.
 *
 * @author Brian
 */
public abstract class SimpleAction extends AbstractAction {

	private static final long serialVersionUID = 2049389041008055018L;

	private static int actionIdPool = 0;

	@Getter
	private GBContext context;
	private int id;

	/**
	 * @param ctxt The context in which this action occurs.
	 */
	public SimpleAction(GBContext ctxt) {
		context = ctxt;
		id = actionIdPool++;
	}

	/**
	 * Alias for the inherited actionPerformed method.
	 */
	public void perform() {
		this.actionPerformed(null);
	}

	/**
	 * Get the id of this action.
	 *
	 * @return The integer id.
	 */
	public int actionId() {
		return id;
	}

}
