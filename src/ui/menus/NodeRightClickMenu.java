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

public class NodeRightClickMenu {
	
	public static void show(final GraphBuilderContext ctxt, final NodePanel n, final int x, final int y) {
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
				ctxt.pushReversibleAction(duplicateAction, true);
			}
			
		});
		
		JMenuItem duplicateFull = new JMenuItem("Duplicate full subgraph");
		duplicateFull.setEnabled(nodeIsSelected);
		duplicateFull.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Duplicate duplicateFullAction = new Duplicate(ctxt, true);
				duplicateFullAction.actionPerformed(null);
				ctxt.pushReversibleAction(duplicateFullAction, true);
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
