package structures;

import java.util.function.Function;

/**
 * A generic unordered pair structure.
 *
 * @param <T> The type of datum this pair holds two of.
 * @author Brian Yao
 */
public class UOPair<T> {

	private T first;
	private T second;

	/**
	 * Construct an unordered pair with the given contents.
	 *
	 * @param first  The "first" element.
	 * @param second The "second element.
	 */
	public UOPair(T first, T second) {
		if (first == null || second == null) {
			throw new IllegalArgumentException("Elements of a UOPair must be non-null.");
		}
		this.first = first;
		this.second = second;
	}

	/**
	 * @return one of the elements of this pair.
	 */
	public T getFirst() {
		return first;
	}

	/**
	 * @return the other element of this pair.
	 */
	public T getSecond() {
		return second;
	}

	/**
	 * Get a new pair consisting of the results of applying the given function
	 * to the elements of this pair.
	 *
	 * @param mapper The mapping function.
	 * @param <R> The data type being mapped to.
	 * @return The new pair of mapped values.
	 */
	public <R> UOPair<R> map(Function<? super T, ? extends R> mapper) {
		return new UOPair<>(mapper.apply(first), mapper.apply(second));
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof UOPair)) {
			return false;
		}
		UOPair<T> other = (UOPair<T>) o;
		return (first.equals(other.first) && second.equals(other.second)) ||
			(first.equals(other.second) && second.equals(other.first));
	}

	@Override
	public int hashCode() {
		return first.hashCode() * second.hashCode();
	}

	@Override
	public String toString() {
		return String.format("{%s, %s}", first, second);
	}

}
