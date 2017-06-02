package io;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import logger.Logger;
import util.ExceptionUtils;
import components.Edge;
import components.Node;
import components.SelfEdge;
import components.SimpleEdge;
import components.display.NodePanel;
import components.display.SelfEdgeData;
import components.display.SimpleEdgeData;
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
	 * Load the specified file into a context object.
	 * 
	 * @param graphFile  The file containing the graph we want to load.
	 * @return The new context object containing the graph in the file.
	 */
	public static GraphBuilderContext loadGraph(File graphFile) {	
		// Read the file, parse the graph's components, and add them to our context
		try {
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(graphFile)));
			
			int idPool = Integer.parseInt(fileReader.readLine());
			int constraints = Integer.parseInt(fileReader.readLine());
			GraphBuilderContext loadedContext = new GraphBuilderContext(constraints);
			
			ArrayList<String> nodeStrs = new ArrayList<>();
			ArrayList<String> edgeStrs = new ArrayList<>();
			
			// Read the file line by line (each line contains one component)
			String line;
			while ((line = fileReader.readLine()) != null) {
				if (line.startsWith("N:")) {
					nodeStrs.add(line);
				} else if (line.startsWith("E:")) {
					edgeStrs.add(line);
				}
			}
			fileReader.close();
			
			// First parse all nodes and add them to the context
			for (String nodeStr : nodeStrs) {
				loadedContext.addNode(readNode(loadedContext, nodeStr));
			}
			
			// Then parse and add all edges so they can refer to the nodes
			for (String edgeStr : edgeStrs) {
				loadedContext.addEdge(readEdge(loadedContext, edgeStr), -1);
			}
			
			// Set the ID pool afterward, to start where it left off
			loadedContext.setNextId(idPool);
			
			// Replace the old context with the one we just loaded
			loadedContext.setCurrentlyLoadedFile(graphFile);
			loadedContext.setAsSaved();
			
			return loadedContext;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "GraphBuilder was unable to open the selected file. Make sure it is a valid GraphBuilder file. For more details on this exact error, check the log.", "Unable to Open File", JOptionPane.ERROR_MESSAGE);
			Logger.writeEntry(Logger.ERROR, ExceptionUtils.exceptionToString(e));
		}
		
		return null;
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
		
		NodePanel panel = new NodePanel(x, y, radius, text, fillColor, borderColor, textColor, context);
		Node restored = new Node(context, id, panel);
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
			SelfEdgeData selfData = new SelfEdgeData(color, weight, text, offsetAngle);
			Edge newSelfEdge = new SelfEdge(node1, selfData, directed, context, id);
			newSelfEdge.setID(id);
			return newSelfEdge;
		}
		Node node2 = (Node) context.getIdMap().get(idnode2);
		
		// Restoring simple edge
		SimpleEdgeData simpleData = new SimpleEdgeData(color, weight, text);
		Edge newSimpleEdge = new SimpleEdge(node1, node2, simpleData, directed, context, id);
		newSimpleEdge.setID(id);
		return newSimpleEdge;
	}
	
}
