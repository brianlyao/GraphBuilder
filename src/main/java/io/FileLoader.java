package io;

import context.GBContext;
import graph.components.Node;
import graph.components.display.NodePanel;
import graph.components.gb.GBComponent;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import logger.Logger;
import util.ExceptionUtils;
import util.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for loading graphs from files.
 *
 * @author Brian
 */
public class FileLoader {

	private static final int NUM_NODE_FIELDS = 8;
	private static final int NUM_EDGE_FIELDS = 8;

	/**
	 * Load the specified file into a context object. The file is assumed to
	 * exist since it will be chosen using a JFileChooser.
	 *
	 * @param graphFile The file containing the graph we want to load.
	 * @return The new context object containing the graph in the file.
	 */
	public static GBContext loadGraph(File graphFile) {
		// Read the file, parse the graph's components, and add them to our context
		try {
			BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(graphFile)));

			int idPool = Integer.parseInt(fileReader.readLine());
			int constraints = Integer.parseInt(fileReader.readLine());
			GBContext loadedContext = new GBContext(constraints);

			// Read the file line by line (each line contains one component)
			String line;
			while ((line = fileReader.readLine()) != null) {
				if (line.startsWith(FileUtils.NODE_PREFIX)) {
					loadedContext.add(readNode(loadedContext, line));
				} else if (line.startsWith(FileUtils.EDGE_PREFIX)) {
					loadedContext.add(readEdge(loadedContext, line));
				}
			}
			fileReader.close();

			// Set the ID pool afterward, to start where it left off
			loadedContext.setNextId(idPool);

			// Replace the old context with the one we just loaded
			loadedContext.setCurrentlyLoadedFile(graphFile);
			loadedContext.setAsSaved();

			return loadedContext;
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "GraphBuilder was unable to open the selected file. " +
				"Make sure it is a valid GraphBuilder file. If this error is unexpected, hand the following " +
				"information to a developer:\n\n" + ExceptionUtils.exceptionToString(e),
										  "Unable to Open File", JOptionPane.ERROR_MESSAGE);
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
	private static GBNode readNode(GBContext context, String nodeStr) {
		String[] vals = nodeStr.substring(2).split(",", NUM_NODE_FIELDS); // 2 is the length of "N:"
		int id = Integer.parseInt(vals[0]);
		int x = Integer.parseInt(vals[1]);
		int y = Integer.parseInt(vals[2]);
		int radius = Integer.parseInt(vals[3]);
		String text = vals[4];
		Color fillColor = new Color(Integer.parseInt(vals[5]));
		Color borderColor = new Color(Integer.parseInt(vals[6]));
		Color textColor = new Color(Integer.parseInt(vals[7]));

		NodePanel panel = new NodePanel(x, y, radius);
		panel.setText(text);
		panel.setFillColor(fillColor);
		panel.setBorderColor(borderColor);
		panel.setTextColor(textColor);

		GBNode loadedNode = new GBNode(new Node(), context, panel);
		loadedNode.setId(id);
		return loadedNode;
	}

	/**
	 * Parses an Edge from its String form.
	 *
	 * @param context The context this Edge will be added to.
	 * @param edgeStr The String to parse.
	 * @return The parsed Edge object.
	 */
	private static GBEdge readEdge(GBContext context, String edgeStr) {
		String[] vals = edgeStr.substring(2).split(",", NUM_EDGE_FIELDS); // 2 is the length of "E:"
		int id = Integer.parseInt(vals[0]);
		int idnode1 = Integer.parseInt(vals[1]);
		int idnode2 = Integer.parseInt(vals[2]);
		Color color = new Color(Integer.parseInt(vals[3]));
		int weight = Integer.parseInt(vals[4]);
		boolean directed = Integer.parseInt(vals[5]) == 1;
		String text = vals[6];

		// Get the endpoints of the edge, assuming they have already been added to the context
		GBNode node1 = (GBNode) context.getFromId(idnode1);
		GBNode node2 = (GBNode) context.getFromId(idnode2);

		GBEdge loadedEdge = new GBEdge(node1, node2, directed);
		if (idnode1 == idnode2) {
			loadedEdge.setAngle(Double.parseDouble(vals[7]));
		}
		loadedEdge.setColor(color);
		loadedEdge.setWeight(weight);
		loadedEdge.setText(text);
		loadedEdge.setId(id);

		return loadedEdge;
	}

}
