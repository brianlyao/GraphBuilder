package graph;

import java.awt.Point;
import java.util.List;

import ui.GUI;
import components.Edge;
import components.Node;
import components.SelfEdge;
import components.SimpleEdge;
import components.display.EdgeData;
import components.display.NodePanel;
import components.display.SelfEdgeData;
import components.display.SimpleEdgeData;

/**
 * A class for generating the displayed UI elements for each graph component.
 * This includes the circular panels for nodes and the data for drawing edges.
 * 
 * @author Brian Yao
 */
public class GraphDisplayFactory {
	
	private static final double SIDE_LENGTH = 100;

	/**
	 * Generates node panels with the default appearance such that all nodes
	 * are arranged with radial symmetry (in a circle). Generates default edge
	 * data for all edges.
	 * 
	 * @param gui   The GUI the panels will be displayed on.
	 * @param graph The graph whose components to generate data for.
	 */
	public static void generateRadialNodePanels(GUI gui, Graph graph) {
		Point center = gui.getEditorCenter();
		
		int k = 0;
		double centralAngle = 2 * Math.PI / graph.getNodes().size();
		double radius = (SIDE_LENGTH / 2.0) / Math.sin(centralAngle / 2.0);
		for (Node node : graph.getNodes()) {
			int x = center.x + (int) (radius * Math.cos(centralAngle * k));
			int y = center.y + (int) (radius * Math.sin(centralAngle * k));
			NodePanel panel = new NodePanel(x, y, Node.DEFAULT_RADIUS, Node.DEFAULT_TEXT,
					Node.DEFAULT_FILL, Node.DEFAULT_BC, Node.DEFAULT_TC, node.getContext());
			node.setNodePanel(panel);
			panel.setNode(node);
			k++;
		}
		
		for (List<Edge> edgeList : graph.getEdges().values()) {
			for (Edge inList : edgeList) {
				if (inList instanceof SimpleEdge) {
					EdgeData edgeData = new SimpleEdgeData(Edge.DEFAULT_COLOR,
							Edge.DEFAULT_WEIGHT, Edge.DEFAULT_TEXT);
					inList.setData(edgeData);
				} else if (inList instanceof SelfEdge) {
					EdgeData edgeData = new SelfEdgeData(Edge.DEFAULT_COLOR,
							Edge.DEFAULT_WEIGHT, Edge.DEFAULT_TEXT, 0.0);
					inList.setData(edgeData);
				}
			}
		}
	}
	
}
