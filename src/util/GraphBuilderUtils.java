package util;

import io.FileLoader;
import io.FileSaver;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import ui.GUI;
import context.GraphBuilderContext;

/** A class of utility methods. */
public class GraphBuilderUtils {

	/**
	 * Perform the procedure for opening a file.
	 * 
	 * @param context The current context.
	 */
	public static void openFileProcedure(GraphBuilderContext context) {
		GUI gui = context.getGUI();
		JFileChooser fc = gui.getFileChooser();
		
		if(context.isUnsaved()) {
			int resp = JOptionPane.showConfirmDialog(gui, "You have unsaved changes. Do you want to save them before opening another file?", "Open: Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if(resp == JOptionPane.YES_OPTION)
				saveFileProcedure(context);
			else if(resp == JOptionPane.CANCEL_OPTION)
				return;
		}
		
		int response = fc.showOpenDialog(gui);
		if(response == JFileChooser.APPROVE_OPTION) {
			File toOpen = fc.getSelectedFile();
			FileLoader.loadGraph(context, toOpen);
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
		if(response == JFileChooser.APPROVE_OPTION) {
			File toSave = fc.getSelectedFile();
			FileSaver.saveGraph(context, toSave);
			gui.setTitle(GUI.DEFAULT_TITLE + " - " + GraphBuilderUtils.getBaseName(toSave));
		}
	}
	
	/**
	 * Perform the procedure for saving to a file. 
	 * 
	 * @param context The current context.
	 */
	public static void saveFileProcedure(GraphBuilderContext context) {
		if(!context.existsOnDisk())
			saveAsFileProcedure(context);
		else if(context.isUnsaved())
			FileSaver.saveGraph(context, context.getCurrentlyLoadedFile());
	}
	
	/**
	 * Perform the procedure for exiting the program.
	 * 
	 * @param context The current context.
	 */
	public static void exitProcedure(GraphBuilderContext context) {
		GUI gui = context.getGUI();
		if(context.isUnsaved()) {
			int resp = JOptionPane.showConfirmDialog(gui, "You have unsaved changes. Do you want to save them before exiting GraphBuilder?", "Exit: Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if(resp == JOptionPane.YES_OPTION)
				saveFileProcedure(context);
			else if(resp == JOptionPane.NO_OPTION)
				System.exit(0);
		} else {
			System.exit(0);
		}
	}
	
	/**
	 * Get the base filename of a file (no extension).
	 * 
	 * @param f The file to get the base name of.
	 * @return The name of the file without the extension.
	 */
	public static String getBaseName(File f) {
		if(f == null)
			return "null";
		String fn = f.getName();
		int lastDot = fn.lastIndexOf('.');
		if(lastDot >= 0)
			return fn.substring(0, lastDot);
		return fn;
	}
	
}
