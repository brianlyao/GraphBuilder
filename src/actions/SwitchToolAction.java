package actions;

import tool.Tool;
import context.GraphBuilderContext;

/** An instance represents the procedure of switching to a Tool. */
public class SwitchToolAction extends Action {
	
	private Tool toTool;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param to   The tool being switched to.
	 */
	public SwitchToolAction(GraphBuilderContext ctxt,Tool to) {
		super(ctxt);
		toTool = to;
	}

	public void perform() {
		getContext().getGUI().updateTool(toTool);
		getContext().getGUI().changeToolOptionsBar(toTool);
	}
	
}
