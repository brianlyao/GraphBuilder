package algorithms;

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
		SearchTemplates.testConnected((start, target) -> fd -> DFS.connected(start, target, fd));
	}

	@Test
	public void testSearchStartEqualsTarget() {
		Node n = new Node(0);

		assertEquals("Path[0]", String.valueOf(DFS.search(n, n, true)));
		assertEquals("Path[0]", String.valueOf(DFS.search(n, n, false)));
	}

}
