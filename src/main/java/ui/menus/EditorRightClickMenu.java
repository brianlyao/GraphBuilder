package ui.menus;

import actions.edit.Paste;
import clipboard.Clipboard;
import context.GBContext;
import ui.Editor;

import javax.swing.*;
import java.awt.*;

/**
 * The right click menu which appears when the user right clicks on the editor.
 *
 * @author Brian Yao
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
		final GBContext ctxt = editor.getContext();
		JPopupMenu menu = new JPopupMenu();
		JMenuItem paste = new JMenuItem("Paste");
		final Clipboard currentClipboard = ctxt.getClipboard();
		paste.setEnabled(!currentClipboard.isEmpty());
		paste.addActionListener($ -> {
			Paste pasteAtPoint = new Paste(ctxt, new Point(x, y));
			pasteAtPoint.perform();
			ctxt.pushReversibleAction(pasteAtPoint, true, false);
		});

		menu.add(paste);
		menu.show(editor, x, y);
	}

}
