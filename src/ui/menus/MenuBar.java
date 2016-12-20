package ui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import components.GraphComponent;
import components.Node;
import actions.edit.Copy;
import actions.edit.PushCut;
import actions.edit.PushDelete;
import actions.edit.PushDuplicate;
import actions.edit.PushPaste;
import actions.edit.Redo;
import actions.edit.Undo;
import ui.GUI;
import util.FileUtils;

public class MenuBar extends JMenuBar {
	
	private static final long serialVersionUID = -7109662156036502356L;
	
	private GUI gui;
	
	private JMenu file;
	private JMenu edit;
	private JMenu view;
	private JMenu tools;
	private JMenu help;
	
	private JMenuItem undo;
	private JMenuItem redo;
	private JMenuItem copy;
	private JMenuItem copyFull;
	private JMenuItem duplicate;
	private JMenuItem duplicateFull;
	private JMenuItem paste;
	private JMenuItem cut;
	private JMenuItem cutFull;
	private JMenuItem delete;
	
	public MenuBar(final GUI g) {
		super();
		
		gui = g;
		
		//Initialize and fill menu bar
		file = new JMenu("File");
		edit = new JMenu("Edit");
		view = new JMenu("View");
		tools = new JMenu("Tools");
		help = new JMenu("Help");
		
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
				FileUtils.openFileProcedure(g.getContext());
			}
			
		});
		
		JMenuItem saveFile = new JMenuItem("Save");
		saveFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FileUtils.saveFileProcedure(g.getContext());
			}
			
		});
		
		JMenuItem saveAsFile = new JMenuItem("Save As");
		saveAsFile.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FileUtils.saveAsFileProcedure(g.getContext());
			}
			
		});
		
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				FileUtils.exitProcedure(g.getContext());
			}
			
		});
			
		file.add(newFile);
		file.add(openFile);
		file.add(saveFile);
		file.add(saveAsFile);
		file.add(exit);
		
		undo = new JMenuItem("Undo");
		undo.setEnabled(false);
		undo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Undo(gui.getContext()).actionPerformed(null);
			}
			
		});
		
		redo = new JMenuItem("Redo");
		redo.setEnabled(false);
		redo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Redo(gui.getContext()).actionPerformed(null);
			}
			
		});
		
		copy = new JMenuItem("Copy");
		copy.setEnabled(false);
		copy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Copy(gui.getContext(), false).actionPerformed(null);
			}
			
		});
		
		copyFull = new JMenuItem("Copy full subgraph");
		copyFull.setEnabled(false);
		copyFull.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Copy(gui.getContext(), true).actionPerformed(null);
			}
			
		});
		
		duplicate = new JMenuItem("Duplicate");
		duplicate.setEnabled(false);
		duplicate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new PushDuplicate(gui.getContext(), false).actionPerformed(null);
			}
			
		});
		
		duplicateFull = new JMenuItem("Duplicate full subgraph");
		duplicateFull.setEnabled(false);
		duplicateFull.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new PushDuplicate(gui.getContext(), true).actionPerformed(null);
			}
			
		});
		
		paste = new JMenuItem("Paste");
		paste.setEnabled(false);
		paste.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new PushPaste(gui.getContext()).actionPerformed(null);
			}
			
		});
		
		cut = new JMenuItem("Cut");
		cut.setEnabled(false);
		cut.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new PushCut(gui.getContext(), false).actionPerformed(null);
			}
			
		});
		
		cutFull = new JMenuItem("Cut full subgraph");
		cutFull.setEnabled(false);
		cutFull.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new PushCut(gui.getContext(), true).actionPerformed(null);
			}
			
		});
		
		delete = new JMenuItem("Delete");
		delete.setEnabled(false);
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new PushDelete(gui.getContext()).actionPerformed(null);
			}
			
		});
		
		duplicate = new JMenuItem("Duplicate");
		duplicate.setEnabled(false);
		
		edit.add(undo);
		edit.add(redo);
		edit.addSeparator();
		edit.add(copy);
		edit.add(copyFull);
		edit.add(duplicate);
		edit.add(duplicateFull);
		edit.add(paste);
		edit.add(cut);
		edit.add(cutFull);
		edit.add(delete);
		
		// Fill the view menu
		JMenuItem grid = new JMenuItem("Grid");
		grid.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				gui.getGridSettingsDialog().showDialog();
			}
			
		});
		
		view.add(grid);
		
		add(file);
		add(edit);
		add(view);
		add(tools);
		add(help);
	}
	
	/**
	 * Sets the enabled state of the undo menu item.
	 * 
	 * @param enabled Whether undo is possible.
	 */
	public void setUndoEnabled(boolean enabled) {
		undo.setEnabled(enabled);
	}
	
	/**
	 * Sets the enabled state of the redo menu item.
	 * 
	 * @param enabled Whether redo is possible.
	 */
	public void setRedoEnabled(boolean enabled) {
		redo.setEnabled(enabled);
	}
	
	/**
	 * Update the enabled/disabled state of menu items depending on empty/non-empty selection.
	 */
	public void updateWithSelection() {
		HashSet<GraphComponent> selections = gui.getEditor().getSelections();
		boolean somethingSelected = !selections.isEmpty();
		copy.setEnabled(somethingSelected);
		cut.setEnabled(somethingSelected);
		delete.setEnabled(somethingSelected);
		duplicate.setEnabled(somethingSelected);
		
		boolean nodeSelected = false;
		for (GraphComponent gc : gui.getEditor().getSelections()) {
			if (gc instanceof Node) {
				nodeSelected = true;
				break;
			}
		}
		copyFull.setEnabled(nodeSelected);
		duplicateFull.setEnabled(nodeSelected);
		cutFull.setEnabled(nodeSelected);
	}
	
	/**
	 * Update the enabled/disabled state of menu items depending on empty/non-empty clipboard.
	 */
	public void updateWithCopy() {
		paste.setEnabled(!gui.getContext().getClipboard().isEmpty());
	}
	
}
