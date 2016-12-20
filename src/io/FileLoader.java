package io;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import components.Edge;
import components.Node;
import components.SelfEdge;
import components.SimpleEdge;
import context.GraphBuilderContext;

/**
 * A utility class for loading graphs from files.
 * 
 * @author Brian
 */
public class FileLoader {
	
	private static final int NUM_NODE_FIELDS = 8;
	private static final int NUM_EDGE_FIELDS = 7;
	
	/**
	 * Load the specified file into the gui specified by the context before the load.
	 * 
	 * @param oldContext The context in place before the load.
	 * @param graphFile  The file containing the graph we want to load.
	 */
	public static void loadGraph(GraphBuilderContext oldContext, File graphFile) {
		GraphBuilderContext loadedContext = new GraphBuilderContext(oldContext.getGUI());
		
		// Read the file, parse the graph's components, and add them to our context
		try {
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(graphFile)));
			
			int idPool = Integer.parseInt(fileReader.readLine());
			
			ArrayList<String> nodeStrs = new ArrayList<>();
			ArrayList<String> edgeStrs = new ArrayList<>();
			
			// Read the file line by line (each line contains one component)
			String line;
			while ((line = fileReader.readLine()) != null) {
				if (line.startsWith("N:"))
					nodeStrs.add(line);
				else if (line.startsWith("E:"))
					edgeStrs.add(line);
			}
			
			// First parse all nodes and add them to the context
			for (String nodeStr : nodeStrs)
				loadedContext.addNode(readNode(loadedContext, nodeStr));
			
			// Then parse and add all edges so they can refer to the nodes
			for (String edgeStr : edgeStrs)
				loadedContext.addEdge(readEdge(loadedContext, edgeStr), -1);
			
			// Set the ID pool afterward, to start where it left off
			loadedContext.setNextID(idPool);
			
			fileReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Replace the old context with the one we just loaded
		loadedContext.setCurrentlyLoadedFile(graphFile);
		loadedContext.setAsSaved();
		loadedContext.getGUI().updateContext(loadedContext);
	}
	
	/**
	 * Parses a Node from its String form.
	 * 
	 * @param context The context this Node will be added to.
	 * @param nodeStr The String to parse.
	 * @return The parsed Node object.
	 */
	private static Node readNode(GraphBuilderContext context, String nodeStr) {
		String[] vals = nodeStr.substring(2).split(",", NUM_NODE_FIELDS); // 2 is the length of "N:"
		int id = Integer.parseInt(vals[0]);
		int x = Integer.parseInt(vals[1]);
		int y = Integer.parseInt(vals[2]);
		int radius = Integer.parseInt(vals[3]);
		String text = vals[4];
		Color fillColor = new Color(Integer.parseInt(vals[5]));
		Color borderColor = new Color(Integer.parseInt(vals[6]));
		Color textColor = new Color(Integer.parseInt(vals[7]));
		Node restored = new Node(x, y, radius, text, fillColor, borderColor, textColor, context, id);
		return restored;
	}
	
	/**
	 * Parses an Edge from its String form.
	 * 
	 * @param context The context this Edge will be added to.
	 * @param edgeStr The String to parse.
	 * @return The parsed Edge object.
	 */
	private static Edge readEdge(GraphBuilderContext context, String edgeStr) {
		String[] vals = edgeStr.substring(2).split(",", NUM_EDGE_FIELDS); // 2 is the length of "E:"
		int id = Integer.parseInt(vals[0]);
		int idnode1 = Integer.parseInt(vals[1]);
		int idnode2 = Integer.parseInt(vals[2]);
		Color color = new Color(Integer.parseInt(vals[3]));
		int weight = Integer.parseInt(vals[4]);
		boolean directed = Integer.parseInt(vals[5]) == 1;
		String text = vals[6];
		
		// Get the endpoints of the edge
		Node node1 = (Node) context.getIdMap().get(idnode1);
		if (idnode1 == idnode2) {
			// Restoring self edge
			double offsetAngle = Double.parseDouble(vals[6]); // Extra field
			Edge newSelfEdge = new SelfEdge(node1, color, weight, text, offsetAngle, directed, context, id);
			newSelfEdge.setID(id);
			return newSelfEdge;
		}
		Node node2 = (Node) context.getIdMap().get(idnode2);
		
		// Restoring simple edge
		Edge newSimpleEdge = new SimpleEdge(node1, node2, color, weight, text, directed, context, id);
		newSimpleEdge.setID(id);
		return newSimpleEdge;
	}
	
}
