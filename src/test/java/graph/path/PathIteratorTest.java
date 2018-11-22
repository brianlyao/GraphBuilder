package graph.path;

import graph.components.Edge;
import graph.components.Node;
import org.javatuples.Pair;
import org.junit.jupiter.api.Test;
import util.TestUtils;

import java.util.Arrays;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for testing the PathIterator class.
 *
 * @author Brian Yao
 */
public class PathIteratorTest {

	@Test
	public void testEmptyPath() {
		Path path = new Path(new Node());
		PathIterator iterator1 = path.iterator(true);
		PathIterator iterator2 = path.iterator(false);

		assertTrue(iterator1.hasNext());
		assertTrue(iterator2.hasNext());

		assertNull(iterator1.next().getValue1());
		assertNull(iterator2.next().getValue1());

		assertFalse(iterator1.hasNext());
		assertFalse(iterator2.hasNext());
	}

	@Test
	public void testOutgoing() {
		Node[] n = TestUtils.newNodes(6, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}},
									  TestUtils.booleans(5, true), n, n.length);

		Path path = new Path(Arrays.asList(n), Arrays.asList(e));
		PathIterator iterator = path.iterator(true);

		IntStream.range(0, n.length).forEach(i -> {
			assertTrue(iterator.hasNext());
			Pair<Node, Edge> nextPair = iterator.next();
			assertEquals(n[i], nextPair.getValue0());
			if (i < e.length) {
				assertEquals(e[i], nextPair.getValue1());
			} else {
				assertNull(nextPair.getValue1());
			}
		});

		assertFalse(iterator.hasNext());
	}

	@Test
	public void testIncoming() {
		Node[] n = TestUtils.newNodes(6, 0);
		Edge[] e = TestUtils.newEdges(new int[][] {{0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}},
									  TestUtils.booleans(5, true), n, n.length);

		Path path = new Path(Arrays.asList(n), Arrays.asList(e));
		PathIterator iterator = path.iterator(false);

		IntStream.range(0, n.length).forEach(i -> {
			assertTrue(iterator.hasNext());
			Pair<Node, Edge> nextPair = iterator.next();
			assertEquals(n[i], nextPair.getValue0());
			if (i == 0) {
				assertNull(nextPair.getValue1());
			} else {
				assertEquals(e[i - 1], nextPair.getValue1());
			}
		});

		assertFalse(iterator.hasNext());
	}

}
