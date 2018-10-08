package util;

import ui.GUI;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A utility class with exception-related procedures.
 *
 * @author Brian
 */
public class ExceptionUtils {

	/**
	 * Display an exception with message and stacktrace using a JOptionPane.
	 *
	 * @param parent The parent component.
	 * @param e      The exception to display.
	 */
	public static void displayException(GUI parent, Exception e) {
		// Convert the stack trace to a string
		String stackTrace = exceptionToString(e);

		// Display the exception
		JOptionPane.showMessageDialog(parent, "The following exception has just occurred. " +
			"Please bring this to the attention of the developer(s) immediately.\n\n" + stackTrace,
									  "An Error Has Occurred", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Convert an exception's stack trace into a single String.
	 *
	 * @param e The exception to convert.
	 * @return The string representation of the exception.
	 */
	public static String exceptionToString(Exception e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

}
