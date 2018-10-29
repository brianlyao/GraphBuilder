package actions;

import context.GBContext;
import tool.Tool;

import java.awt.event.ActionEvent;

/**
 * An instance represents the procedure of switching to a particular Tool.
 *
 * @author Brian Yao
 */
public class SwitchToolAction extends SimpleAction {

	private static final long serialVersionUID = 2609975848674806840L;

	private Tool toTool;

	/**
	 * @param ctxt The context in which this action occurs.
	 * @param to   The tool being switched to.
	 */
	public SwitchToolAction(GBContext ctxt, Tool to) {
		super(ctxt);
		toTool = to;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		this.getContext().getGUI().updateTool(toTool);
		this.getContext().getGUI().changeToolOptionsBar();
	}

}
