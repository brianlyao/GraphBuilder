package structures;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test cases for the Union-Find structure.
 *
 * @author Brian Yao
 */
public class UnionFindTest {

	@Test
	public void testInitialization() {
		List<Integer> data = IntStream.range(0, 10).boxed().collect(Collectors.toList());
		UnionFind<Integer> unionFind = new UnionFind<>(data);

		for (Integer num : data) {
			assertEquals(num, unionFind.find(num));
		}
	}

	@Test
	public void testUnion() {
		List<Integer> data = IntStream.range(0, 10).boxed().collect(Collectors.toList());
		UnionFind<Integer> unionFind = new UnionFind<>(data);

		unionFind.union(3, 8);
		unionFind.union(0, 4);

		assertTrue(Set.of(3, 8).contains(unionFind.find(3)));
		assertEquals(unionFind.find(3), unionFind.find(8));
		assertTrue(Set.of(0, 4).contains(unionFind.find(0)));
		assertEquals(unionFind.find(0), unionFind.find(4));

		unionFind.union(unionFind.find(3), unionFind.find(0));

		assertEquals(unionFind.find(0), unionFind.find(3));
		assertEquals(unionFind.find(3), unionFind.find(4));
		assertEquals(unionFind.find(4), unionFind.find(8));
		assertTrue(Set.of(0, 3, 4, 8).contains(unionFind.find(0)));

		unionFind.union(unionFind.find(0), unionFind.find(2));

		assertEquals(unionFind.find(0), unionFind.find(2));
		assertTrue(Set.of(0, 2, 3, 4, 8).contains(unionFind.find(2)));
	}

}
