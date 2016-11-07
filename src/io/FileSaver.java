package io;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashSet;

import components.Edge;
import components.Node;
import context.GraphBuilderContext;

/** A utility class for saving graphs to files. */
public class FileSaver {
	
	private static final String EXTENSION = ".gbf";

	/**
	 * Saves the graph specified by the context to the target file.
	 * 
	 * @param context The context containing the graph components we want to save to disk.
	 * @param target  The destination file which will contain the saved data.
	 */
	public static void saveGraph(GraphBuilderContext context, File target) {
		HashSet<Node> nodes = context.getNodes();
		HashSet<Edge> edges = context.getEdges();
		
		try {
			if (!target.getName().endsWith(EXTENSION))
				target = new File(target.getAbsolutePath() + EXTENSION);
			
			// Write all components to the file (create it if it does not exist)
			target.createNewFile();
			PrintWriter pwriter = new PrintWriter(target);
			pwriter.write(context.getNextID() + "\n");
			for (Node n : nodes)
				pwriter.write(n.toStorageString() + "\n");
			for (Edge e : edges)
				pwriter.write(e.toStorageString() + "\n");
			pwriter.close();
			
			// Update the context to indicate it is saved
			context.setCurrentlyLoadedFile(target);
			context.setActionIdOnLastSave(context.getActionHistory().peek().actionId());
			context.setAsSaved();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
