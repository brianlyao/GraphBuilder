package structures.function;

import java.util.function.Function;

/**
 * Represents a function that accepts four arguments and produces a result.
 * This is the four-arity specialization of {@link Function}.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(Object, Object, Object, Object)}}.
 *
 * @param <S> the type of the first argument to the function
 * @param <T> the type of the second argument to the function
 * @param <U> the type of the third argument to the function
 * @param <V> the type of the fourth argument to the function
 * @param <W> the type of the result of the function
 *
 * @see Function
 */
@FunctionalInterface
public interface QuadFunction<S, T, U, V, W> {

	/**
	 * Applies this function to the given arguments.
	 *
	 * @param s the first function argument
	 * @param t the second function argument
	 * @param u the third function argument
	 * @param v the fourth function argument
	 * @return the function result
	 */
	W apply(S s, T t, U u, V v);

}