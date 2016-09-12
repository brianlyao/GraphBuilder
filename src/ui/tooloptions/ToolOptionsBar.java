package ui.tooloptions;

import javax.swing.JToolBar;

import ui.GUI;

/** An abstract class for a bar which displays options for a tools. */
public abstract class ToolOptionsBar extends JToolBar {
	
	private static final long serialVersionUID = 3598910131347326252L;

	private GUI gui;
	
	/**
	 * Create a new options panel.
	 * 
	 * @param g The GUI in which this panel will be.
	 */
	public ToolOptionsBar(GUI g) {
		super();
		gui = g;
	}
	
	public GUI getGUI() {
		return gui;
	}
	
}
