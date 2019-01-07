package algorithms;

import graph.Graph;
import graph.GraphConstraint;
import graph.components.Node;
import org.junit.jupiter.api.Test;
import util.SearchTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit test cases for DFS.
 *
 * @author Brian Yao
 */
public class DFSTest {

	@Test
	public void testExploreUndirected() {
		SearchTemplates.testExploreUndirected(DFS::explore);
	}

	@Test
	public void testExploreDirected() {
		SearchTemplates.testExploreDirected(DFS::explore);
	}

	@Test
	public void testExploreAll() {
		SearchTemplates.testExploreAll(DFS::exploreAll);
	}

	@Test
	public void testConnected() {
		SearchTemplates.testConnected(DFS::connected);
	}

	@Test
	public void testSearchStartEqualsTarget() {
		int constraints = GraphConstraint.SIMPLE | GraphConstraint.UNDIRECTED | GraphConstraint.UNWEIGHTED;
		Graph graph = new Graph(constraints);
		Node n = new Node(0);

		graph.addNode(n);

		assertEquals("Path[0]", String.valueOf(DFS.search(graph, n, n, true)));
		assertEquals("Path[0]", String.valueOf(DFS.search(graph, n, n, false)));
	}

}
