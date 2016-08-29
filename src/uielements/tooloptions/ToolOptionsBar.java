package uielements.tooloptions;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.swing.JToolBar;

import uielements.GUI;

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
	
	protected static void fillImage(BufferedImage b, Color c) {
		for(int i = 0 ; i < b.getWidth() ; i++)
			for(int j = 0 ; j < b.getHeight() ; j++)
				b.setRGB(i, j, c.getRGB());
	}
	
}
