package algorithms;

import graph.Graph;
import graph.components.Edge;
import graph.components.Node;
import structures.UnionFind;
import util.GraphUtils;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * An implementation of Kruskal's algorithm for finding minimum spanning trees.
 *
 * @author Brian Yao
 */
public final class Kruskal {

	/**
	 * Given a graph, returns a minimum spanning tree of it. If the given
	 * graph is not a connected graph, then the result is a graph containing
	 * minimum spanning trees for each connected component
	 *
	 * @param graph The graph to find a spanning tree of.
	 * @return a minimum spanning tree of the provided graph.
	 */
	public static Graph execute(Graph graph) {
		Graph spanningTree = new Graph(graph.getConstraints());
		spanningTree.addNodes(graph.getNodes());

		// Use a union-find data structure for efficiency
		UnionFind<Node> unionFind = new UnionFind<>(graph.getNodes());

		// Sort edges by weight
		PriorityQueue<Edge> edges = new PriorityQueue<>(Comparator.comparingDouble(Edge::getNumericWeight));
		graph.getEdges().keySet().forEach(
			pair -> edges.add(GraphUtils.minWeightEdge(graph, pair.getFirst(), pair.getSecond(), false))
		);

		// Add edge if its ends do not belong in the same connected component
		while (!edges.isEmpty()) {
			Edge edge = edges.poll();
			Node set1 = unionFind.find(edge.getFirstEnd());
			Node set2 = unionFind.find(edge.getSecondEnd());

			if (set1 != set2) {
				spanningTree.addEdge(edge);
				unionFind.union(set1, set2);
			}
		}

		return spanningTree;
	}

}
