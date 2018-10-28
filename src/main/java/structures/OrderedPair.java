package structures;

/**
 * An instance is an ordered pair of two elements of the same type.
 *
 * @param <T> The type of element this pair holds.
 * @author Brian Yao
 */
public class OrderedPair<T> {

	private T first;
	private T second;

	/**
	 * Construct an ordered pair given the first and second.
	 *
	 * @param first  The first element of the pair.
	 * @param second The second element of the pair.
	 */
	public OrderedPair(T first, T second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Get the first element of this ordered pair.
	 *
	 * @return The first element.
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * Get the second element of this ordered pair.
	 *
	 * @return The second element.
	 */
	public T getSecond() {
		return second;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof OrderedPair)) {
			return false;
		}
		OrderedPair<T> other = (OrderedPair<T>) o;
		return first.equals(other.first) && second.equals(other.second);
	}

	@Override
	public String toString() {
		return String.format("(%s, %s)", first, second);
	}

}
