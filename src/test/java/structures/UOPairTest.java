package structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

		assertEquals(Integer.valueOf(0), pair.getFirst());
		assertEquals(Integer.valueOf(1), pair.getSecond());
	}

	@Test
	public void mapTest() {
		UOPair<Integer> pair = new UOPair<>(3, 7);
		UOPair<Integer> pair2 = pair.map(n -> n * n);
		UOPair<String> pair3 = pair.map(String::valueOf);

		assertEquals(Integer.valueOf(9), pair2.getFirst());
		assertEquals(Integer.valueOf(49), pair2.getSecond());
		assertEquals("3", pair3.getFirst());
		assertEquals("7", pair3.getSecond());
	}

}
