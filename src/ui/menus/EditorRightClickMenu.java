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
	 * Display the menu on the provided editor.
	 * 
	 * @param ctxt
	 * @param editor
	 * @param x
	 * @param y
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
				if (!currentClipboard.isEmpty()) {
					// Only paste if we have anything copied.
					Paste pasteAction = new Paste(ctxt, new Point(x, y));
					pasteAction.actionPerformed(null);
					ctxt.pushReversibleAction(pasteAction, true);
				}
			}
			
		});
		menu.add(paste);
		menu.show(editor, x, y);
	}
	
}
