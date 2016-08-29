package keybindings;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import context.GraphBuilderContext;
import tool.Tool;
import actions.*;

public class KeyboardShortcutActions {
	
	private GraphBuilderContext context;
	
	private HashMap<KeyboardShortcut, Action> shortcutToAction;
	
	public KeyboardShortcutActions(GraphBuilderContext ctxt) {
		
		context = ctxt;
		
		// Fill mapping from shortcut key to the appropriate tool
		shortcutToAction = new HashMap<>();
		shortcutToAction.put(new KeyboardShortcut(KeyEvent.VK_S), new SwitchToolAction(ctxt, Tool.SELECT));
		shortcutToAction.put(new KeyboardShortcut(KeyEvent.VK_E), new SwitchToolAction(ctxt, Tool.EDGE_SELECT));
		shortcutToAction.put(new KeyboardShortcut(KeyEvent.VK_C), new SwitchToolAction(ctxt, Tool.NODE));
		shortcutToAction.put(new KeyboardShortcut(KeyEvent.VK_A), new SwitchToolAction(ctxt, Tool.DIRECTED_EDGE));
		shortcutToAction.put(new KeyboardShortcut(KeyEvent.VK_L), new SwitchToolAction(ctxt, Tool.EDGE));
		shortcutToAction.put(new KeyboardShortcut(KeyEvent.VK_P), new SwitchToolAction(ctxt, Tool.PAN));
		
		// Add more complicated shortcuts
		shortcutToAction.put(new KeyboardShortcut(true, false, false, KeyEvent.VK_Z), new Undo(ctxt));
		shortcutToAction.put(new KeyboardShortcut(true, false, false, KeyEvent.VK_Y), new Redo(ctxt));
		
	}
	
	/** 
	 * Get the action corresponding to the specified keyboard shortcut.
	 * 
	 * @param shortcut The KeyboardShortcut we want to look up.
	 * @return The action corresponding to the keyboard shortcut (or null if there is none).
	 */
	public Action getAction(KeyboardShortcut shortcut) {
		return shortcutToAction.get(shortcut);
	}
	
}
