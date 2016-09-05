package ui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import ui.GUI;
import util.GraphBuilderUtils;

public class MenuBar extends JMenuBar {
	
	private static final long serialVersionUID = -7109662156036502356L;
	
	private GUI gui;
	
	public MenuBar(final GUI g) {
		super();
		
		gui = g;
		
		//Initialize and fill menu bar
		JMenu file = new JMenu("File");
		JMenu edit = new JMenu("Edit");
		JMenu tools = new JMenu("Tools");
		JMenu help = new JMenu("Help");
		
		// Fill "File" menu
		JMenu newFile = new JMenu("New");
		
		// Fill the "New" submenu
		JMenuItem newUndirected = new JMenuItem("Undirected Graph");
		JMenuItem newDirected = new JMenuItem("Directed Graph");
		JMenuItem newMixed = new JMenuItem("Mixed Graph");
		newFile.add(newUndirected);
		newFile.add(newDirected);
		newFile.add(newMixed);
		
		JMenuItem openFile = new JMenuItem("Open");
		openFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphBuilderUtils.openFileProcedure(g.getContext());
			}
			
		});
		
		JMenuItem saveFile = new JMenuItem("Save");
		saveFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphBuilderUtils.saveFileProcedure(g.getContext());
			}
			
		});
		
		JMenuItem saveAsFile = new JMenuItem("Save As");
		saveAsFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphBuilderUtils.saveAsFileProcedure(g.getContext());
			}
			
		});
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GraphBuilderUtils.exitProcedure(g.getContext());
			}
			
		});
			
		file.add(newFile);
		file.add(openFile);
		file.add(saveFile);
		file.add(saveAsFile);
		file.add(exit);
		
		add(file);
		add(edit);
		add(tools);
		add(help);
	}
	
}
