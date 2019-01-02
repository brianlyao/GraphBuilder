package algorithms;

import graph.components.Node;
import org.junit.jupiter.api.Test;
import util.SearchTemplates;
import util.ShortestPathTemplates;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit test cases for BFS.
 *
 * @author Brian Yao
 */
public class BFSTest {

	@Test
	public void testExploreUndirected() {
		SearchTemplates.testExploreUndirected(BFS::explore);
	}

	@Test
	public void testExploreDirected() {
		SearchTemplates.testExploreDirected(BFS::explore);
	}

	@Test
	public void testExploreAll() {
		SearchTemplates.testExploreAll(BFS::exploreAll);
	}

	@Test
	public void testConnected() {
		SearchTemplates.testConnected((start, target) -> fd -> BFS.connected(start, target, fd));
	}

	@Test
	public void testSearchStartEqualsTarget() {
		Node n = new Node(0);

		assertEquals("Path[0]", String.valueOf(BFS.search(n, n, true)));
		assertEquals("Path[0]", String.valueOf(BFS.search(n, n, false)));
	}

	// Shortest path in unweighted graphs

	@Test
	public void testSearchSSUU() {
		ShortestPathTemplates.testSSUU(graph -> (start, dest) -> BFS.search(start, dest, true));
	}

	@Test
	public void testSearchSSUD() {
		ShortestPathTemplates.testSSUD(graph -> (start, dest) -> BFS.search(start, dest, true));
	}

	@Test
	public void testSearchSSUM() {
		ShortestPathTemplates.testSSUM(graph -> (start, dest) -> BFS.search(start, dest, true));
	}

	@Test
	public void testSearchMSUU() {
		ShortestPathTemplates.testMSUU(graph -> (start, dest) -> BFS.search(start, dest, true));
	}

	@Test
	public void testSearchMSUD() {
		ShortestPathTemplates.testMSUD(graph -> (start, dest) -> BFS.search(start, dest, true));
	}

	@Test
	public void testSearchMSUM() {
		ShortestPathTemplates.testMSUM(graph -> (start, dest) -> BFS.search(start, dest, true));
	}

}
