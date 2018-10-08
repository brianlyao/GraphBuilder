package math;

/**
 * An instance is a single complex number a + bi, where a and b are real values, and
 * i is the imaginary constant (square root of -1).
 *
 * @author Brian
 */
public class Complex {

	private static final double E = 1e-14; // Epsilon value for floating point errors

	private double real;
	private double imag;

	/**
	 * Create a new complex number with only a real component. The
	 * imaginary component is zero.
	 *
	 * @param r The real value of this complex number.
	 */
	public Complex(double r) {
		real = r;
		imag = 0.0;
	}

	/**
	 * @param r The real component.
	 * @param i The imaginary component.
	 */
	public Complex(double r, double i) {
		real = r;
		imag = i;
	}

	/**
	 * Create a new complex number using polar coordinates.
	 *
	 * @param len     The radius (modulus) of the number.
	 * @param rads    The angle of the number from the real axis.
	 * @param isPolar A dummy variable to signify this constructor uses polar coordinates.
	 */
	public Complex(double len, double rads, boolean isPolar) {
		real = len * Math.cos(rads);
		imag = len * Math.sin(rads);
	}

	/**
	 * Get the real component.
	 *
	 * @return The real component.
	 */
	public double getReal() {
		return real;
	}

	/**
	 * Get the imaginary component.
	 *
	 * @return The imaginary component.
	 */
	public double getImag() {
		return imag;
	}

	/**
	 * Set the real component.
	 *
	 * @param r The new real component.
	 */
	public void setReal(double r) {
		real = r;
	}

	/**
	 * Set the imaginary component.
	 *
	 * @param i The new imaginary component.
	 */
	public void setImag(double i) {
		imag = i;
	}

	/**
	 * Check if this complex number is equal to zero. This checks if both the
	 * real and imaginary components are very close to zero.
	 *
	 * @return true iff the complex number is zero.
	 */
	public boolean isZero() {
		return Math.abs(real) < E && Math.abs(imag) < E;
	}

	/**
	 * Check if this complex number has an imaginary component equal to zero.
	 *
	 * @return true fif the complex number is real.
	 */
	public boolean isReal() {
		return Math.abs(imag) < E;
	}

	/**
	 * Compute the absolute value (modulus) of this complex number.
	 *
	 * @return the absolute value (modulus) of this complex number.
	 */
	public double abs() {
		return Math.sqrt(real * real + imag * imag);
	}

	/**
	 * Return a new complex number, whose components are multiplied by the scaling factor.
	 *
	 * @param n The constant scaling factor.
	 * @return The scaled complex number.
	 */
	public Complex scale(double n) {
		return new Complex(real * n, imag * n);
	}

	/**
	 * Compute the conjugate of a complex number. The conjugate simply has its
	 * imaginary component negated.
	 *
	 * @return The conjugate of this complex number.
	 */
	public Complex conjugate() {
		return new Complex(real, -imag);
	}

	/**
	 * Negate both components of this complex number.
	 *
	 * @return The negated complex number.
	 */
	public Complex neg() {
		return new Complex(-real, -imag);
	}

	/**
	 * Compute the complex number with the same components as this but with
	 * the modulus scaled such that it is equal to 1.
	 *
	 * @return The normalized complex number.
	 */
	public Complex unit() {
		double mod = this.abs();
		return new Complex(real / mod, imag / mod);
	}

	/**
	 * Compute the sum of two complex numbers.
	 *
	 * @param c The complex number to add to this.
	 * @return The sum.
	 */
	public Complex add(Complex c) {
		return new Complex(real + c.real, imag + c.imag);
	}

	/**
	 * Compute the difference of two complex numbers.
	 *
	 * @param c The complex number to subtract from this.
	 * @return The difference.
	 */
	public Complex subtract(Complex c) {
		return new Complex(real - c.real, imag - c.imag);
	}

	/**
	 * Compute the product of two complex numbers.
	 *
	 * @param c The complex number to multiply to this.
	 * @return The product.
	 */
	public Complex multiply(Complex c) {
		return new Complex(real * c.real - imag * c.imag, real * c.imag + imag * c.real);
	}

	/**
	 * Compute the quotient of two complex numbers.
	 *
	 * @param c The complex number to divide this by.
	 * @return The quotient.
	 */
	public Complex divide(Complex c) {
		return this.multiply(c.conjugate()).scale(1 / (c.real * c.real + c.imag * c.imag));
	}

	/**
	 * Compute the nth roots of unity. These are the complex numbers x where
	 * x^n = 1. There are exactly n complex solutions. These solutions are
	 * returned in an array of length n.
	 *
	 * @param n The degree of the roots of unity.
	 * @return An array of n roots of unity.
	 */
	public static Complex[] rootsOfUnity(int n) {
		if (n < 1) {
			return null;
		}
		Complex[] roots = new Complex[n];
		for (int i = 0; i < n; i++) {
			double ang = (2 * Math.PI * i) / n;
			roots[i] = new Complex(1, ang, true);
		}
		return roots;
	}

	/**
	 * Compute the nth roots of this complex number. The roots are returned
	 * in an array of n complex numbers.
	 *
	 * @param n The degree of the roots we want.
	 * @return The array of all n nth roots of this.
	 */
	public Complex[] nthRoot(int n) {
		if (n < 1) {
			throw new IllegalArgumentException("A complex number cannot be raised to a root less than 1: " + n);
		}
		double theta = Math.atan(imag / real);
		if (real < 0) {
			theta += Math.PI;
		}
		Complex[] roots = new Complex[n];
		double scale = Math.pow(this.abs(), 1.0 / n);
		for (int i = 0; i < n; i++) {
			roots[i] = new Complex(scale, (theta + 2 * Math.PI * i) / n, true);
		}
		return roots;
	}

	/**
	 * Compute the square roots of this complex number.
	 *
	 * @return An array of two complex numbers, containing the square roots of this.
	 */
	public Complex[] sqrt() {
		return nthRoot(2);
	}

	/**
	 * Compute the cube roots of this complex number.
	 *
	 * @return An array of three complex numbers, containing the cube roots of this.
	 */
	public Complex[] cbrt() {
		return nthRoot(3);
	}

	/**
	 * Compute the nth power of a complex number.
	 *
	 * @param n The integer power to raise this to.
	 * @return The resulting complex number upon evaluating the power.
	 */
	public Complex pow(int n) {
		if (n < 0) {
			return new Complex(1).divide(this.pow(-n));
		}
		if (isReal()) {
			return new Complex(Math.pow(real, n), 0);
		}
		if (n == 0) {
			return new Complex(1);
		}
		double newMod = Math.pow(abs(), n);
		double theta = Math.atan(imag / real);
		if (real < 0)
			theta += Math.PI;
		return new Complex(newMod, n * theta, true);
	}

	@Override
	public String toString() {
		if (imag >= 0) {
			return real + " + " + imag + "i";
		}
		return real + " - " + (-imag) + "i";
	}

}
