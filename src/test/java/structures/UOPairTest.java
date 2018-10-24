package structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test cases for the UOPair (unordered pair) structure.
 *
 * @author Brian Yao
 */
public class UOPairTest {

	@Test
	public void orderDoesNotMatterTest() {
		UOPair<Integer> pair1 = new UOPair<>(0, 1);
		UOPair<Integer> pair2 = new UOPair<>(1, 0);

		assertEquals(pair1, pair2);

		UOPair<Integer> pair3 = new UOPair<>(0, 1);

		assertEquals(pair1, pair3);
	}

	@Test
	public void hashCodeTest() {
		UOPair<Integer> pair1 = new UOPair<>(0, 1);
		UOPair<Integer> pair2 = new UOPair<>(1, 0);

		assertEquals(pair1.hashCode(), pair2.hashCode());
	}

	@Test
	public void getterTest() {
		UOPair<Integer> pair = new UOPair<>(0, 1);

		assertTrue(pair.getFirst() == 0);
		assertTrue(pair.getSecond() == 1);
	}

}
