package ui.menus;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import clipboard.Clipboard;
import context.GraphBuilderContext;
import actions.edit.Paste;
import ui.Editor;

/**
 * The right click menu which appears when the user right clicks on the editor.
 * 
 * @author Brian
 */
public class EditorRightClickMenu {

	/**
	 * Display the menu on the provided editor at the specified location.
	 * 
	 * @param editor The editor to display the menu over.
	 * @param x      The x-coordinate at which to display the menu.
	 * @param y      The y-coordinate at which to display the menu.
	 */
	public static void show(final Editor editor, final int x, final int y) {
		final GraphBuilderContext ctxt = editor.getContext();
		JPopupMenu menu = new JPopupMenu();
		JMenuItem paste = new JMenuItem("Paste");
		final Clipboard currentClipboard = ctxt.getClipboard();
		paste.setEnabled(!currentClipboard.isEmpty());
		paste.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Paste pasteAtPoint = new Paste(ctxt, new Point(x, y));
				pasteAtPoint.actionPerformed(null);
				ctxt.pushReversibleAction(pasteAtPoint, true, false);
			}
			
		});
		menu.add(paste);
		menu.show(editor, x, y);
	}
	
}
