package keybindings;

import actions.SwitchToolAction;
import actions.edit.*;
import actions.file.New;
import actions.file.Open;
import actions.file.Save;
import actions.file.SaveAs;
import context.GBContext;
import tool.Tool;
import ui.GBFrame;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * A class containing all the keystroke-to-action mappings.
 *
 * @author Brian
 */
public class KeyActions {

	public static final KeyStroke SELECT_TOOL = KeyStroke.getKeyStroke("typed s");
	public static final KeyStroke EDGE_SELECT_TOOL = KeyStroke.getKeyStroke("typed r");
	public static final KeyStroke NODE_TOOL = KeyStroke.getKeyStroke("typed n");
	public static final KeyStroke DIRECTED_EDGE_TOOL = KeyStroke.getKeyStroke("typed d");
	public static final KeyStroke EDGE_TOOL = KeyStroke.getKeyStroke("typed e");
	public static final KeyStroke PAN_TOOL = KeyStroke.getKeyStroke("typed p");
	public static final KeyStroke UNDO = KeyStroke.getKeyStroke("control Z");
	public static final KeyStroke REDO = KeyStroke.getKeyStroke("control Y");
	public static final KeyStroke COPY = KeyStroke.getKeyStroke("control C");
	public static final KeyStroke COPY_FULL = KeyStroke.getKeyStroke("control shift C");
	public static final KeyStroke PASTE = KeyStroke.getKeyStroke("control V");
	public static final KeyStroke DUPLICATE = KeyStroke.getKeyStroke("control D");
	public static final KeyStroke DUPLICATE_FULL = KeyStroke.getKeyStroke("control shift D");
	public static final KeyStroke CUT = KeyStroke.getKeyStroke("control X");
	public static final KeyStroke CUT_FULL = KeyStroke.getKeyStroke("control shift X");
	public static final KeyStroke BACKSPACE = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
	public static final KeyStroke DELETE = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
	public static final KeyStroke NEW = KeyStroke.getKeyStroke("control N");
	public static final KeyStroke OPEN = KeyStroke.getKeyStroke("control O");
	public static final KeyStroke SAVE = KeyStroke.getKeyStroke("control S");
	public static final KeyStroke SAVE_AS = KeyStroke.getKeyStroke("control shift S");
	public static final KeyStroke EXIT = KeyStroke.getKeyStroke("control W");

	/**
	 * Initialize the key bindings in the given GBFrame.
	 *
	 * @param g The GBFrame in which we want to apply key bindings.
	 */
	public static void initialize(GBFrame g) {
		GBContext ctxt = g.getContext();

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
		actionMap.put("new", new New(ctxt));
		actionMap.put("open", new Open(ctxt));
		actionMap.put("save", new Save(ctxt));
		actionMap.put("save as", new SaveAs(ctxt));

		InputMap inputMap = g.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

		// Keystrokes for switching to tools
		inputMap.put(SELECT_TOOL, "select");
		inputMap.put(EDGE_SELECT_TOOL, "edge select");
		inputMap.put(NODE_TOOL, "node");
		inputMap.put(DIRECTED_EDGE_TOOL, "dir edge");
		inputMap.put(EDGE_TOOL, "edge");
		inputMap.put(PAN_TOOL, "pan");

		// Keystrokes for undoing and redoing
		inputMap.put(UNDO, "undo");
		inputMap.put(REDO, "redo");

		// Keystrokes for operations under the edit menu
		inputMap.put(COPY, "copy");
		inputMap.put(COPY_FULL, "copyfull");
		inputMap.put(PASTE, "paste");
		inputMap.put(DUPLICATE, "duplicate");
		inputMap.put(DUPLICATE_FULL, "duplicatefull");
		inputMap.put(CUT, "cut");
		inputMap.put(CUT_FULL, "cutfull");
		inputMap.put(DELETE, "delete");
		inputMap.put(BACKSPACE, "delete");

		// Keystrokes for file io
		inputMap.put(NEW, "new");
		inputMap.put(OPEN, "open");
		inputMap.put(SAVE, "save");
		inputMap.put(SAVE_AS, "save as");
	}

}
