package graph.path;

import graph.components.Edge;
import graph.components.Node;
import graph.components.WeightedEdge;
import org.junit.jupiter.api.Test;
import util.TestUtils;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for testing the Path class.
 *
 * @author Brian Yao
 */
public class PathTest {

	@Test
	public void testInitialization() {
		Path path1 = new Path(new Node(2));

		assertEquals("Path[2]", path1.toString());
		assertEquals(1, path1.getNodes().size());
		assertEquals(0, path1.getEdges().size());

		Node[] n = TestUtils.newNodes(6, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}},
									  TestUtils.booleans(5, true), n, n.length);

		Path path2 = new Path(Arrays.asList(n), Arrays.asList(e));

		assertEquals("Path[0-(6)->1-(7)->2-(8)->3-(9)->4-(10)->5]", path2.toString());
		assertEquals(6, path2.getNodes().size());
		assertEquals(5, path2.getEdges().size());
	}

	@Test
	public void testCopyConstructor() {
		Node[] n = TestUtils.newNodes(4, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}}, TestUtils.booleans(3, false), n, 0);

		Path path = new Path(Arrays.asList(n), Arrays.asList(e));

		Path copy = new Path(path);
		assertEquals(path.getNodes(), copy.getNodes());
		assertEquals(path.getEdges(), copy.getEdges());
		assertEquals(path.toString(), copy.toString());
	}

	@Test
	public void testAppendPrependUndirected() {
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}}, TestUtils.booleans(4, false),
									  n, n.length);

		Path path = new Path(n[2]);

		assertThrows(IllegalArgumentException.class, () -> path.appendNode(n[3], e[0]));
		path.appendNode(n[3], e[2]);

		assertEquals("Path[2-(7)->3]", path.toString());

		assertThrows(IllegalArgumentException.class, () -> path.prependNode(n[1], e[0]));
		path.prependNode(n[1], e[1]);

		assertEquals("Path[1-(6)->2-(7)->3]", path.toString());

		path.prependNode(n[0], e[0]);
		path.appendNode(n[4], e[3]);
		assertEquals("Path[0-(5)->1-(6)->2-(7)->3-(8)->4]", path.toString());
	}

	@Test
	public void testAppendPrependDirected() {
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}}, TestUtils.booleans(4, true),
									  n, n.length);

		Path path = new Path(n[2]);

		assertThrows(IllegalArgumentException.class, () -> path.appendNode(n[1], e[1]));
		path.prependNode(n[1], e[1]);

		assertEquals("Path[1-(6)->2]", path.toString());

		assertThrows(IllegalArgumentException.class, () -> path.prependNode(n[3], e[2]));
		path.appendNode(n[3], e[2]);

		path.prependNode(n[0], e[0]);
		path.appendNode(n[4], e[3]);
		assertEquals("Path[0-(5)->1-(6)->2-(7)->3-(8)->4]", path.toString());
	}

	@Test
	public void testLength() {
		Node[] n = TestUtils.newNodes(5, 0);
		double[] weights = {2.5, 3.6, 1.1, 1.3};
		double epsilon = 1e-15;

		Path path = new Path(n[0]);

		double sum = 0.;
		assertEquals(sum, path.length(), epsilon);
		assertEquals(0, path.edgeLength());

		for (int i = 1 ; i < n.length ; i++) {
			path.appendNode(n[i], new WeightedEdge(n[i - 1], n[i], true, weights[i - 1]));

			sum += weights[i - 1];
			assertEquals(sum, path.length(), epsilon);
			assertEquals(i, path.edgeLength());
		}
	}

	@Test
	public void testCycle() {
		Node[] n = TestUtils.newNodes(5, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 0}, {1, 2}, {2, 3}, {1, 3}},
									  new boolean[] {true, false, true, false}, n, 0);

		Path path1 = new Path(n[0]);

		assertFalse(path1.isCycle());
		path1.appendNode(n[0], e[0]);
		assertTrue(path1.isCycle());

		Path path2 = new Path(n[2]);
		path2.prependNode(n[1], e[1]);
		assertFalse(path2.isCycle());
		path2.appendNode(n[3], e[2]);
		assertFalse(path2.isCycle());
		path2.appendNode(n[1], e[3]);
		assertTrue(path2.isCycle());
	}

}
