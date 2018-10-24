package ui.tooloptions;

import ui.GBFrame;

import javax.swing.*;

/**
 * An abstract class for a bar which displays options for a tools.
 */
public abstract class ToolOptionsBar extends JToolBar {

	private static final long serialVersionUID = 3598910131347326252L;

	private GBFrame gui;

	/**
	 * Create a new options panel.
	 *
	 * @param g The GBFrame in which this panel will be.
	 */
	public ToolOptionsBar(GBFrame g) {
		super();
		gui = g;
	}

	public GBFrame getGUI() {
		return gui;
	}

}
