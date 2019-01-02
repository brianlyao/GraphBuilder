package graph.path;

import graph.components.Edge;
import graph.components.Node;
import org.junit.jupiter.api.Test;
import util.TestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for testing the Cycle class.
 *
 * @author Brian Yao
 */
public class CycleTest {

	@Test
	public void testInitialization1() {
		Node[] n = TestUtils.newNodes(4, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{2, 0}, {0, 3}, {3, 1}, {1, 2}}, TestUtils.booleans(4, false),
									  n, n.length);

		Path path = new Path(List.of(n[2], n[0], n[3], n[1], n[2]), Arrays.asList(e));

		assertDoesNotThrow(() -> new Cycle(path));

		Path path2 = new Path(List.of(n[2], n[0], n[3], n[1]), List.of(e[0], e[1], e[2]));

		assertThrows(IllegalArgumentException.class, () -> new Cycle(path2));
	}

	@Test
	public void testInitialization2() {
		Node[] n = TestUtils.newNodes(4, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 0}}, TestUtils.booleans(4, false),
									  n, n.length);

		assertDoesNotThrow(() -> new Cycle(Arrays.asList(n), Arrays.asList(e)));

		Edge temp = e[2];
		e[2] = e[3];
		e[3] = temp;

		assertThrows(IllegalArgumentException.class, () -> new Cycle(Arrays.asList(n), Arrays.asList(e)));
	}

	@Test
	public void testEquals() {
		Node[] n = TestUtils.newNodes(4, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{2, 0}, {0, 3}, {3, 1}, {1, 2}}, TestUtils.booleans(4, false),
									   n, n.length);

		Cycle cycle1 = new Cycle(List.of(n[2], n[0], n[3], n[1]), Arrays.asList(e));
		Cycle cycle2 = new Cycle(List.of(n[3], n[1], n[2], n[0]), List.of(e[2], e[3], e[0], e[1]));

		assertEquals(cycle1, cycle2);
	}

}
