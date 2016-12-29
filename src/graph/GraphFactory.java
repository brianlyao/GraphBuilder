package graph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import components.Node;
import components.SimpleEdge;

/**
 * A class for generating graphs.
 * 
 * @author Brian
 */
public class GraphFactory {

	/**
	 * Generate a symmetric complete graph with the provided number of nodes. The
	 * graph is shaped like a regular polygon for 3 or more nodes.
	 * 
	 * @param numNodes      The number of nodes in our complete graph.
	 * @param center        The center of the graph.
	 * @param sideLength    The distance between adjacent nodes.
	 * @param referenceNode A reference node; generated nodes will have the same appearance.
	 * @param referenceEdge A reference edge; generated edges will have the same appearance.
	 * @return The complete graph.
	 */
	public static Graph completeGraph(int numNodes, Point center, int sideLength, Node referenceNode, SimpleEdge referenceEdge) {
		Graph graph = new Graph(GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED | GraphConstraint.SIMPLE);
		if (numNodes < 1) {
			return graph;
		} else if (numNodes == 1) {
			// Complete graph of 1 node is just a single node
			Node single = new Node(referenceNode);
			single.getNodePanel().setCenter(center);
			graph.addNode(single);
		} else {
			List<Node> generatedNodes = new ArrayList<>();
			double centralAngle = 2 * Math.PI / numNodes;
			double radius = (sideLength / 2.0) / Math.sin(centralAngle / 2.0);
			
			// Add new nodes
			for (int i = 0 ; i < numNodes ; i++) {
				Node newNode = new Node(referenceNode);
				int newCenterX = (int) (radius * Math.cos(centralAngle * i));
				int newCenterY = (int) (radius * Math.sin(centralAngle * i));
				newNode.getNodePanel().setCenter(newCenterX, newCenterY);
				generatedNodes.add(newNode);
				graph.addNode(newNode);
			}
			
			// Add new edges
			for (int i = 0 ; i < generatedNodes.size() - 1 ; i++) {
				for (int j = i + 1 ; j < generatedNodes.size() ; j++) {
					graph.addEdge(new SimpleEdge(referenceEdge, generatedNodes.get(i), generatedNodes.get(j)), null);
				}
			}
		}
		return graph;
	}
	
}
