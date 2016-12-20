package ui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import components.GraphComponent;
import components.Node;
import components.display.NodePanel;
import context.GraphBuilderContext;
import actions.edit.Copy;
import actions.edit.Duplicate;
import actions.edit.PushCut;
import actions.edit.PushDelete;

/**
 * The right click menu which appears when the user right clicks on a node.
 * 
 * @author Brian
 */
public class NodeRightClickMenu {
	
	/**
	 * Display the menu on the provided node at the specified location.
	 * 
	 * @param n The node panel to display the menu on.
	 * @param x      The x-coordinate at which to display the menu.
	 * @param y      The y-coordinate at which to display the menu.
	 */
	public static void show(final NodePanel n, final int x, final int y) {
		final GraphBuilderContext ctxt = n.getNode().getContext();
		JPopupMenu menu = new JPopupMenu();
		boolean selectionsNotEmpty = !ctxt.getGUI().getEditor().getSelections().isEmpty();
		JMenuItem properties = new JMenuItem("View/Edit Properties");
		
		JMenuItem copy = new JMenuItem("Copy");
		copy.setEnabled(selectionsNotEmpty);
		copy.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Copy(ctxt, false).actionPerformed(null);
			}
			
		});
		
		JMenuItem copyFull = new JMenuItem("Copy full subgraph");
		boolean nodeIsSelected = false;
		for (GraphComponent gc : ctxt.getGUI().getEditor().getSelections()) {
			if (gc instanceof Node) {
				nodeIsSelected = true;
				break;
			}
		}
		copyFull.setEnabled(nodeIsSelected);
		copyFull.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new Copy(ctxt, true).actionPerformed(null);
			}
			
		});
		
		JMenuItem duplicate = new JMenuItem("Duplicate");
		duplicate.setEnabled(selectionsNotEmpty);
		duplicate.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Duplicate duplicateAction = new Duplicate(ctxt, false);
				duplicateAction.actionPerformed(null);
				ctxt.pushReversibleAction(duplicateAction, true, false);
			}
			
		});
		
		JMenuItem duplicateFull = new JMenuItem("Duplicate full subgraph");
		duplicateFull.setEnabled(nodeIsSelected);
		duplicateFull.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Duplicate duplicateFullAction = new Duplicate(ctxt, true);
				duplicateFullAction.actionPerformed(null);
				ctxt.pushReversibleAction(duplicateFullAction, true, false);
			}
			
		});
		
		JMenuItem cut = new JMenuItem("Cut");
		cut.setEnabled(selectionsNotEmpty);
		cut.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new PushCut(ctxt, false).actionPerformed(null);
			}
			
		});
		
		JMenuItem cutFull = new JMenuItem("Cut full subgraph");
		cutFull.setEnabled(nodeIsSelected);
		cutFull.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new PushCut(ctxt, true).actionPerformed(null);
			}
			
		});
		
		JMenuItem delete = new JMenuItem("Delete");
		delete.setEnabled(selectionsNotEmpty);
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new PushDelete(ctxt).actionPerformed(null);
			}
			
		});
		
		menu.add(properties);
		menu.addSeparator();
		menu.add(copy);
		menu.add(copyFull);
		menu.add(duplicate);
		menu.add(duplicateFull);
		menu.add(cut);
		menu.add(cutFull);
		menu.add(delete);
		menu.show(n, x, y);
	}
	
}
