package io;

import context.GBContext;
import util.ExceptionUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for saving graphs to files.
 *
 * @author Brian
 */
public class FileSaver {

	private static final String EXTENSION = ".gbf";

	/**
	 * Saves the graph specified by the context to the target file.
	 *
	 * @param context The context containing the graph components we want to save to disk.
	 * @param target  The destination file which will contain the saved data.
	 */
	public static void saveGraph(GBContext context, File target) {
		try {
			if (!target.getName().endsWith(EXTENSION)) {
				target = new File(target.getAbsolutePath() + EXTENSION);
			}

			// Write all components to the file (create it if it does not exist)
			target.createNewFile();
			PrintWriter writer = new PrintWriter(target);
			writeln(writer, String.valueOf(context.getNextId())); // Write id pool
			writeln(writer, String.valueOf(context.getGraph().getConstraints())); // Write graph constraints
			context.getGraph().getNodes().forEach(node -> writeln(writer, node.getGbNode().toStorageString()));
			context.getGraph().getEdgeSet().forEach(
				edge -> writeln(writer, edge.getGbEdge().toStorageString())
			);
			writer.close();

			// Update the context to indicate it is saved
			context.setCurrentlyLoadedFile(target);
			context.setActionIdOnLastSave(context.getActionHistory().peek().actionId());
			context.setAsSaved();
		} catch (Exception e) {
			ExceptionUtils.displayException(context.getGUI(), e);
		}
	}

	/**
	 * Utility method for writing a string with a newline character.
	 *
	 * @param writer The writer to write with.
	 * @param string The string to write.
	 */
	private static void writeln(PrintWriter writer, String string) {
		writer.write(string + '\n');
	}

}
