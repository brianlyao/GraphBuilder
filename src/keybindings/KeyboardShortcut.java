package keybindings;

/** An instance is a keyboard shortcut, which is used to carry out some action. */
public class KeyboardShortcut {
	
	// Indicates whether the button needs to be pressed down prior to the final key
	private boolean ctrlNeeded;
	private boolean shiftNeeded;
	private boolean altNeeded;
	
	private int keyCode; // The final keystroke which completes the shortcut
	
	/**
	 * @param ctrl  Whether this shortcut requires the control key to be pressed down.
	 * @param shift Whether this shortcut requires the shift key to be pressed down.
	 * @param alt   Whether this shortcut requires the alt key to be pressed down.
	 * @param key   The virtual key code of the final key in the sequence.
	 */
	public KeyboardShortcut(boolean ctrl, boolean shift, boolean alt, int key) {
		ctrlNeeded = ctrl;
		shiftNeeded = shift;
		altNeeded = alt;
		keyCode = key;
	}
	
	public KeyboardShortcut(int key) {
		this(false, false, false, key);
	}
	
	public boolean[] getReqs() {
		return new boolean[] {ctrlNeeded, shiftNeeded, altNeeded};
	}
	
	public int getFinalKeyCode() {
		return keyCode;
	}
	
	public int hashCode() {
		return ("" + ctrlNeeded + shiftNeeded + altNeeded + keyCode).hashCode();
	}
	
	public boolean equals(Object obj) {
		KeyboardShortcut other = (KeyboardShortcut) obj;
		return keyCode == other.keyCode && ctrlNeeded == other.ctrlNeeded && shiftNeeded == other.shiftNeeded && altNeeded == other.altNeeded;
	}
	
	public String toString() {
		return String.format("%s%s%s%s", ctrlNeeded ? "ctrl + " : "", shiftNeeded ? "shift + " : "", altNeeded ? "alt + " : "", keyCode);
	}
	
}
