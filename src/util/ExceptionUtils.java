package util;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import ui.GUI;

/**
 * A utility class with exception-related procedures.
 * 
 * @author Brian
 */
public class ExceptionUtils {

	public static void displayException(GUI parent, Exception e) {
		// Convert the stack trace to a string
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		String stackTrace = stringWriter.toString();
		
		// Display the exception
		JOptionPane.showMessageDialog(parent, "The following exception has just occurred. Please bring this to the attention of the developer(s) immediately.\n\n" + stackTrace, "An Exception Occurred", JOptionPane.ERROR_MESSAGE);
	}
	
}
