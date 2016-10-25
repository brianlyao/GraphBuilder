package ui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import components.display.NodePanel;
import actions.DeleteNodeAction;

public class NodeRightClickMenu {
	
	public static void show(final NodePanel n, final int x, final int y) {
		JPopupMenu menu = new JPopupMenu();
		JMenuItem properties = new JMenuItem("View/Edit Properties");
		JMenuItem copy = new JMenuItem("Copy");
		JMenuItem delete = new JMenuItem("Delete");
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new DeleteNodeAction(n.getNode().getContext(), n.getNode()).actionPerformed(null);
			}
			
		});
		menu.add(properties);
		menu.addSeparator();
		menu.add(copy);
		menu.addSeparator();
		menu.add(delete);
		menu.show(n, x, y);
	}
	
}
