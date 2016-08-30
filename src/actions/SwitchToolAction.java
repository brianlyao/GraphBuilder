package actions;

import java.awt.event.ActionEvent;

import tool.Tool;
import context.GraphBuilderContext;

/** An instance represents the procedure of switching to a Tool. */
public class SwitchToolAction extends SimpleAction {
	
	private static final long serialVersionUID = 2609975848674806840L;
	
	private Tool toTool;
	
	/**
	 * @param ctxt The context in which this action occurs.
	 * @param to   The tool being switched to.
	 */
	public SwitchToolAction(GraphBuilderContext ctxt, Tool to) {
		super(ctxt);
		toTool = to;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getContext().getGUI().updateTool(toTool);
		getContext().getGUI().changeToolOptionsBar(toTool);
	}
	
}
