package graph;

import graph.components.Node;
import graph.components.display.NodePanel;
import graph.components.gb.GBEdge;
import graph.components.gb.GBNode;
import org.javatuples.Pair;
import ui.GUI;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class for generating the displayed UI elements for each graph component.
 * This includes the circular panels for nodes and the data for drawing edges.
 *
 * @author Brian Yao
 */
public class GraphDisplayFactory {

	private static final double SIDE_LENGTH = 100;
	private static final double OFFSET = 0.01;

	/**
	 * Generates node panels with the default appearance such that all nodes
	 * are arranged with radial symmetry (in a circle). Generates default edge
	 * data for all edges.
	 *
	 * @param gui   The GUI the panels will be displayed on.
	 * @param graph The graph whose components to generate data for.
	 */
	public static Pair<Set<GBNode>, Set<GBEdge>> generateRadialNodePanels(GUI gui, Graph graph) {
		Point center = gui.getEditorCenter();

		Set<GBNode> gbNodes = new HashSet<>();
		Set<GBEdge> gbEdges = new HashSet<>();

		int k = 0;
		double centralAngle = 2 * Math.PI / graph.getNodes().size();
		double radius = (SIDE_LENGTH / 2.0) / Math.sin(centralAngle / 2.0);
		for (Node node : graph.getNodes()) {
			// Generate nodes and add them
			int x = center.x + (int) (radius * Math.cos(OFFSET + centralAngle * k));
			int y = center.y + (int) (radius * Math.sin(OFFSET + centralAngle * k));

			int nodeRadius = gui.getNodeOptionsBar().getCurrentRadius();
			NodePanel panel = new NodePanel(x, y, nodeRadius);

			gbNodes.add(new GBNode(node, gui.getContext(), panel));
			k++;
		}

		graph.getEdges().values().forEach(list -> gbEdges.addAll(
			list.stream().map(GBEdge::new).collect(Collectors.toList()))
		);

		return new Pair<>(gbNodes, gbEdges);
	}

}
