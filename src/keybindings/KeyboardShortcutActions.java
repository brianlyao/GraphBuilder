package keybindings;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import context.GraphBuilderContext;
import tool.Tool;
import ui.GUI;
import actions.*;

/** A class containing all the keystroke-to-action mappings. */
public class KeyboardShortcutActions {
	
	private GUI gui; // The GUI we want the keyboard shortcuts to work on
	
	public KeyboardShortcutActions(GUI g) {
		
		GraphBuilderContext ctxt = g.getContext();
		
		ActionMap actionMap = g.getRootPane().getActionMap();
		
		// Actions for switching to tools
		actionMap.put("select", new SwitchToolAction(ctxt, Tool.SELECT));
		actionMap.put("edge select", new SwitchToolAction(ctxt, Tool.EDGE_SELECT));
		actionMap.put("node", new SwitchToolAction(ctxt, Tool.NODE));
		actionMap.put("dir edge", new SwitchToolAction(ctxt, Tool.DIRECTED_EDGE));
		actionMap.put("edge", new SwitchToolAction(ctxt, Tool.EDGE));
		actionMap.put("pan", new SwitchToolAction(ctxt, Tool.PAN));
		
		// Actions for undoing and redoing
		actionMap.put("undo", new Undo(ctxt));
		actionMap.put("redo", new Redo(ctxt));
		
		// Actions for file io
		actionMap.put("open", new Open(ctxt));
		actionMap.put("save", new Save(ctxt));
		actionMap.put("save as", new SaveAs(ctxt));
		
		InputMap inputMap = g.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		// Keystrokes for switching to tools
		inputMap.put(KeyStroke.getKeyStroke("typed s"), "select");
		inputMap.put(KeyStroke.getKeyStroke("typed r"), "edge select");
		inputMap.put(KeyStroke.getKeyStroke("typed n"), "node");
		inputMap.put(KeyStroke.getKeyStroke("typed d"), "dir edge");
		inputMap.put(KeyStroke.getKeyStroke("typed e"), "edge");
		inputMap.put(KeyStroke.getKeyStroke("typed p"), "pan");
		
		// Keystrokes for undoing and redoing
		inputMap.put(KeyStroke.getKeyStroke("control Z"), "undo");
		inputMap.put(KeyStroke.getKeyStroke("control Y"), "redo");
		
		// Keystrokes for file io
		inputMap.put(KeyStroke.getKeyStroke("control O"), "open");
		inputMap.put(KeyStroke.getKeyStroke("control S"), "save");
		inputMap.put(KeyStroke.getKeyStroke("control shift S"), "save as");
		
	}
	
}
