package keybindings;

import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import context.GraphBuilderContext;
import tool.Tool;
import ui.GUI;
import actions.*;
import actions.edit.Copy;
import actions.edit.PushCut;
import actions.edit.PushDelete;
import actions.edit.PushDuplicate;
import actions.edit.PushPaste;
import actions.edit.Redo;
import actions.edit.Undo;
import actions.file.Open;
import actions.file.Save;
import actions.file.SaveAs;

/**
 * A class containing all the keystroke-to-action mappings.
 * 
 * @author Brian
 */
public class KeyboardShortcutActions {
	
	/**
	 * Initialize the key bindings in the given GUI.
	 * 
	 * @param g The GUI in which we want to apply key bindings.
	 */
	public static void initialize(GUI g) {
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
		actionMap.put("copy", new Copy(ctxt, false));
		actionMap.put("copyfull", new Copy(ctxt, true));
		actionMap.put("paste", new PushPaste(ctxt));
		actionMap.put("duplicate", new PushDuplicate(ctxt, false));
		actionMap.put("duplicatefull", new PushDuplicate(ctxt, true));
		actionMap.put("cut", new PushCut(ctxt, false));
		actionMap.put("cutfull", new PushCut(ctxt, true));
		actionMap.put("delete", new PushDelete(ctxt));
		
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
		
		// Keystrokes for operations under the edit menu
		inputMap.put(KeyStroke.getKeyStroke("control C"), "copy");
		inputMap.put(KeyStroke.getKeyStroke("control shift C"), "copyfull");
		inputMap.put(KeyStroke.getKeyStroke("control V"), "paste");
		inputMap.put(KeyStroke.getKeyStroke("control D"), "duplicate");
		inputMap.put(KeyStroke.getKeyStroke("control shift D"), "duplicatefull");
		inputMap.put(KeyStroke.getKeyStroke("control X"), "cut");
		inputMap.put(KeyStroke.getKeyStroke("control shift X"), "cutfull");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "delete");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
		
		// Keystrokes for file io
		inputMap.put(KeyStroke.getKeyStroke("control O"), "open");
		inputMap.put(KeyStroke.getKeyStroke("control S"), "save");
		inputMap.put(KeyStroke.getKeyStroke("control shift S"), "save as");
	}

}
