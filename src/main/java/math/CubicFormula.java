package math;

/**
 * A class containing an implementation of the "cubic formula", which obtains
 * all three complex roots of a third degree polynomial.
 *
 * @author Brian
 */
public class CubicFormula {

	/**
	 * Compute the roots of a cubic polynomial equation of the form
	 * ax^3 + bx^2 + cx + d = 0. There will be exactly 3 complex roots,
	 * returned in an array.
	 *
	 * @param a The real coefficient of the x^3 term.
	 * @param b The real coefficient of the x^2 term.
	 * @param c The real coefficient of the x term.
	 * @param d The constant term.
	 * @return All three roots of the cubic equation.
	 */
	public static Complex[] getRoots(double a, double b, double c, double d) {
		Complex[] roots = new Complex[3];
		Complex disc0 = new Complex(b * b - 3 * a * c);
		Complex disc1 = new Complex(2 * b * b * b - 9 * a * b * c + 27 * a * a * d);
		Complex incbrt = null;
		if (disc0.isZero()) {
			incbrt = disc1;
			if (disc1.getReal() < 0) {
				incbrt = incbrt.neg();
			}
		} else {
			incbrt = disc1.add(disc1.pow(2).subtract(disc0.pow(3).scale(4)).sqrt()[0]).scale(0.5);
		}
		Complex[] cbrts = incbrt.cbrt();
		Complex B = new Complex(b);
		for (int i = 0; i < 3; i++) {
			roots[i] = B.add(cbrts[i]).add(disc0.divide(cbrts[i])).scale(-1 / (3 * a));
		}

		return roots;
	}

}
