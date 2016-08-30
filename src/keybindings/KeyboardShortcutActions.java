package keybindings;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import context.GraphBuilderContext;
import tool.Tool;
import actions.*;

public class KeyboardShortcutActions {
	
	private GraphBuilderContext context;
	
	public KeyboardShortcutActions(GraphBuilderContext ctxt) {
		
		context = ctxt;
		
		ActionMap actionMap = context.getGUI().getRootPane().getActionMap();
		
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
		
		InputMap inputMap = context.getGUI().getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		
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
		
	}
	
//	/** 
//	 * Get the action corresponding to the specified keyboard shortcut.
//	 * 
//	 * @param shortcut The KeyboardShortcut we want to look up.
//	 * @return The action corresponding to the keyboard shortcut (or null if there is none).
//	 */
//	public BaseAction getAction(KeyboardShortcut shortcut) {
//		return shortcutToAction.get(shortcut);
//	}
//	
}
