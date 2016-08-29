package uielements;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class MenuBar extends JMenuBar {
	
	private static final long serialVersionUID = -7109662156036502356L;
	
	//Menu bar
	private JMenuBar menuBar;
	private JMenu file;
	private JMenu edit;
	private JMenu tools;
	private JMenu help;
	
	public MenuBar() {
		//Initialize and fill menu bar
		menuBar = new JMenuBar();
		file = new JMenu("File");
		edit = new JMenu("Edit");
		tools = new JMenu("Tools");
		help = new JMenu("Help");
		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(tools);
		menuBar.add(help);
	}
	
}
