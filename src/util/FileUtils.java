package util;

import io.FileLoader;
import io.FileSaver;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import components.Node;

import main.GraphBuilderMain;
import ui.GUI;
import ui.dialogs.NewGraphDialog;
import context.GraphBuilderContext;

/**
 * A utility class with procedures related to file IO, save state, and interface elements
 * which ensure that the user is notified of potentially lost changes.
 * 
 * @author Brian
 */
public class FileUtils {

	/**
	 * Checks if the contents in the given context are up to date with what
	 * is currently on disk. If not, a dialog will appear, prompting the user
	 * to either save or discard the changes.
	 * 
	 * @param context The current context.
	 */
	public static void checkUnsaved(GraphBuilderContext context) {
		GUI gui = context.getGUI();
		if (context.isUnsaved()) {
			int resp = JOptionPane.showConfirmDialog(gui, "You have unsaved changes. Do you want to save them first?", "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if (resp == JOptionPane.YES_OPTION) {
				saveFileProcedure(context);
			}
		}
	}
	
	/**
	 * The procedure for creating a new file. This clears the editor and switches
	 * to a newly created context for the new file.
	 * 
	 * @param context
	 */
	public static void newFileProcedure(GraphBuilderContext context) {
		GUI gui = context.getGUI();
		Integer constraints = NewGraphDialog.getConstraints(gui);
		if (constraints != null) {
			GraphBuilderContext newContext = new GraphBuilderContext(constraints.intValue());
			FileUtils.checkUnsaved(gui.getContext());
			gui.updateContext(newContext);
		}
	}
	
	/**
	 * Perform the procedure for opening a file.
	 * 
	 * @param context The current context.
	 */
	public static void openFileProcedure(GraphBuilderContext context) {
		GUI gui = context.getGUI();
		JFileChooser fc = gui.getFileChooser();
		
		checkUnsaved(context);
		
		int response = fc.showOpenDialog(gui);
		if (response == JFileChooser.APPROVE_OPTION) {
			File toOpen = fc.getSelectedFile();
			GraphBuilderContext newContext = FileLoader.loadGraph(toOpen);
			gui.updateContext(newContext);
			for (Node n : newContext.getNodes()) {
				gui.getEditor().add(n.getNodePanel());
			}
			gui.getEditor().repaint();
			gui.getEditor().revalidate();
		}
	}
	
	/**
	 * Perform the procedure for "saving as" in a new file.
	 * 
	 * @param context The current context.
	 */
	public static void saveAsFileProcedure(GraphBuilderContext context) {
		GUI gui = context.getGUI();
		JFileChooser fc = gui.getFileChooser();
		int response = fc.showSaveDialog(gui);
		if (response == JFileChooser.APPROVE_OPTION) {
			File toSave = fc.getSelectedFile();
			FileSaver.saveGraph(context, toSave);
			gui.setTitle(GUI.DEFAULT_TITLE + " - " + FileUtils.getBaseName(toSave));
		}
	}
	
	/**
	 * Perform the procedure for saving to a file. 
	 * 
	 * @param context The current context.
	 */
	public static void saveFileProcedure(GraphBuilderContext context) {
		if (!context.existsOnDisk()) {
			saveAsFileProcedure(context);
		} else if (context.isUnsaved()) {
			FileSaver.saveGraph(context, context.getCurrentlyLoadedFile());
		}
	}
	
	/**
	 * Perform the procedure for exiting the program.
	 * 
	 * @param context The current context.
	 */
	public static void exitProcedure(GraphBuilderContext context) {
		GUI gui = context.getGUI();
		if (context.isUnsaved()) {
			int resp = JOptionPane.showConfirmDialog(gui, "You have unsaved changes. Do you want to save them before exiting GraphBuilder?", "Exit: Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if (resp == JOptionPane.YES_OPTION) {
				saveFileProcedure(context);
			} else if (resp == JOptionPane.NO_OPTION) {
				GraphBuilderMain.exit();
			}
		} else {
			GraphBuilderMain.exit();
		}
	}
	
	/**
	 * Get the base filename of a file (no extension).
	 * 
	 * @param f The file to get the base name of.
	 * @return The name of the file without the extension.
	 */
	public static String getBaseName(File f) {
		if (f == null) {
			return "null";
		}
		String fn = f.getName();
		int lastDot = fn.lastIndexOf('.');
		if (lastDot >= 0) {
			return fn.substring(0, lastDot);
		}
		return fn;
	}
	
}
