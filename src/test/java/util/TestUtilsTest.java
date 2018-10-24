package util;

import graph.components.Edge;
import graph.components.Node;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for the TestUtils class.
 *
 * @author Brian Yao
 */
public class TestUtilsTest {

	@Test
	public void newNodesTest() {
		Node[] n = TestUtils.newNodes(20, 4321);
		for (int i = 0 ; i < n.length ; i++) {
			assertEquals(4321 + i, n[i].getId());
		}
	}

	@Test
	public void newEdgesTest() {
		Node[] n = TestUtils.newNodes(10, 0);
		int[][] ends = {{0, 1}, {1, 2}, {5, 4}, {6, 7}, {9, 3}};
		boolean[] dirs = {false, false, true, true, false};
		Edge[] e = TestUtils.newEdges(ends, dirs, n, 5678);

		for (int i = 0 ; i < e.length ; i++) {
			assertEquals(5678 + i, e[i].getId());
			assertEquals(n[ends[i][0]], e[i].getFirstEnd());
			assertEquals(n[ends[i][1]], e[i].getSecondEnd());
			assertEquals(dirs[i], e[i].isDirected());
		}
	}

	@Test
	public void filledBooleanArrayTest() {
		boolean[] b1 = TestUtils.booleans(30, true);
		boolean[] b2 = TestUtils.booleans(40, false);

		for (boolean x : b1) {
			assertTrue(x);
		}
		for (boolean y : b2) {
			assertFalse(y);
		}
	}

}
