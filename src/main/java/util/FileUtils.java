package util;

import context.GBContext;
import graph.components.gb.GBNode;
import io.FileLoader;
import io.FileSaver;
import main.GBMain;
import ui.GBFrame;
import ui.dialogs.NewGraphDialog;

import javax.swing.*;
import java.io.File;

/**
 * A utility class with procedures related to file IO, save state, and interface elements
 * which ensure that the user is notified of potentially lost changes.
 *
 * @author Brian
 */
public class FileUtils {

	public static final String NODE_PREFIX = "N:";
	public static final String EDGE_PREFIX = "E:";

	/**
	 * Checks if the contents in the given context are up to date with what
	 * is currently on disk. If not, a dialog will appear, prompting the user
	 * to either save or discard the changes.
	 *
	 * @param context The current context.
	 */
	public static void checkUnsaved(GBContext context) {
		GBFrame gui = context.getGUI();
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
	 * @param context The context before creating a new file.
	 */
	public static void newFileProcedure(GBContext context) {
		GBFrame gui = context.getGUI();
		Integer constraints = NewGraphDialog.getConstraints(gui);
		if (constraints != null) {
			GBContext newContext = new GBContext(constraints);
			FileUtils.checkUnsaved(gui.getContext());
			gui.updateContext(newContext);
		}
	}

	/**
	 * Perform the procedure for opening a file.
	 *
	 * @param context The current context.
	 */
	public static void openFileProcedure(GBContext context) {
		GBFrame gui = context.getGUI();
		JFileChooser fc = gui.getFileChooser();

		checkUnsaved(context);

		int response = fc.showOpenDialog(gui);
		if (response == JFileChooser.APPROVE_OPTION) {
			File toOpen = fc.getSelectedFile();
			GBContext newContext = FileLoader.loadGraph(toOpen);
			gui.updateContext(newContext);
			for (GBNode n : newContext.getGbNodes()) {
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
	public static void saveAsFileProcedure(GBContext context) {
		GBFrame gui = context.getGUI();
		JFileChooser fc = gui.getFileChooser();
		int response = fc.showSaveDialog(gui);
		if (response == JFileChooser.APPROVE_OPTION) {
			File toSave = fc.getSelectedFile();
			FileSaver.saveGraph(context, toSave);
			gui.setTitle(GBFrame.DEFAULT_TITLE + " - " + FileUtils.getBaseName(toSave));
		}
	}

	/**
	 * Perform the procedure for saving to a file.
	 *
	 * @param context The current context.
	 */
	public static void saveFileProcedure(GBContext context) {
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
	public static void exitProcedure(GBContext context) {
		GBFrame gui = context.getGUI();
		if (context.isUnsaved()) {
			int resp = JOptionPane.showConfirmDialog(gui, "You have unsaved changes. Do you want to save them before " +
				"exiting GraphBuilder?", "Exit: Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
			if (resp == JOptionPane.YES_OPTION) {
				saveFileProcedure(context);
			} else if (resp == JOptionPane.NO_OPTION) {
				GBMain.exit();
			}
		} else {
			GBMain.exit();
		}
	}

	/**
	 * @param gui The GBFrame to get the title of.
	 * @return the title of the GUI
	 */
	public static String getGuiTitle(GBFrame gui) {
		if (gui.getContext().existsOnDisk()) {
			return GBFrame.DEFAULT_TITLE + " - " + getBaseName(gui.getContext().getCurrentlyLoadedFile());
		} else {
			return GBFrame.DEFAULT_TITLE + " - " + GBFrame.DEFAULT_FILENAME;
		}
	}

	/**
	 * Get the base filename of a file (no extension).
	 *
	 * @param f The file to get the base name of.
	 * @return The name of the file without the extension.
	 */
	private static String getBaseName(File f) {
		String fn = f.getName();
		int lastDot = fn.lastIndexOf('.');
		if (lastDot >= 0) {
			return fn.substring(0, lastDot);
		}
		return fn;
	}

}
