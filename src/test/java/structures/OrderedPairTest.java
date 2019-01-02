package structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit test cases for the OrderedPair structure.
 *
 * @author Brian Yao
 */
public class OrderedPairTest {

	@Test
	public void testOrderMatters() {
		OrderedPair<Integer> pair1 = new OrderedPair<>(0, 1);
		OrderedPair<Integer> pair2 = new OrderedPair<>(1, 0);

		assertNotEquals(pair2, pair1);

		OrderedPair<Integer> pair3 = new OrderedPair<>(0, 1);

		assertEquals(pair3, pair1);
	}

	@Test
	public void testGetters() {
		OrderedPair<Integer> pair = new OrderedPair<>(0, 1);

		assertEquals(Integer.valueOf(0), pair.getFirst());
		assertEquals(Integer.valueOf(1), pair.getSecond());
	}

}
