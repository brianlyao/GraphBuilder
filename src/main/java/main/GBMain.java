package main;

import context.GBContext;
import io.FileLoader;
import logger.Logger;
import ui.GUI;
import ui.dialogs.NewGraphDialog;
import ui.dialogs.StartupDialog;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/**
 * The main top-level class, where the interface is initialized.
 *
 * @author Brian
 */
public class GBMain {

	private static final String LOG_FILE = "log.txt";

	public static void main(String[] args) {
		try {
			Logger.setLogFile(LOG_FILE);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Unable to initialize logger. This means that any crashes/problems will not be logged! Please ensure that GraphBuilder has sufficient permissions to write files.", "Logger Initialization Failed", JOptionPane.ERROR_MESSAGE);
		}

		Logger.writeEntry(Logger.INFO, "Starting GraphBuilder.");

		int choice;
		GBContext startingContext = null;
		while (startingContext == null) {
			choice = StartupDialog.getStartupOption();

			// Determine course of action according to starting option
			switch (choice) {
				case StartupDialog.NEW_FILE_CHOICE:
					Integer constraints = NewGraphDialog.getConstraints(null);
					if (constraints != null) {
						startingContext = new GBContext(constraints);
					}
					break;
				case StartupDialog.OPEN_FILE_CHOICE:
					JFileChooser fc = new JFileChooser();
					int response = fc.showOpenDialog(null);
					if (response == JFileChooser.APPROVE_OPTION) {
						File toOpen = fc.getSelectedFile();
						startingContext = FileLoader.loadGraph(toOpen);
					}
					break;
				default:
					exit();
			}
		}

		new GUI(startingContext);
	}

	/**
	 * Method for cleanly exiting the program.
	 */
	public static void exit() {
		Logger.flush();
		System.gc();
		System.exit(0);
	}

}
