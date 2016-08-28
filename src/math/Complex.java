package math;

/** An instance is a single complex number a + bi. */
public class Complex {
	
	private static final double E = 1e-10; // Epsilon value for floating point errors
	
	private double real;
	private double imag;
	
	/** Create a new complex number with only a real component. */
	public Complex(double r) {
		real = r;
		imag = 0;
	}
	
	/** Create a new complex number with real and imaginary components. */
	public Complex(double r, double i) {
		real = r;
		imag = i;
	}
	
	/** Create a new complex number using polar coordinates. */
	public Complex(double len, double rads, boolean isPolar) {
		real = len * Math.cos(rads);
		imag = len * Math.sin(rads);
	}
	
	/** The real component value. */
	public double getReal() {
		return real;
	}
	
	/** The imaginary component value. */
	public double getImag() {
		return imag;
	}
	
	public void setReal(double r) {
		real = r;
	}
	
	public void setImag(double i) {
		imag = i;
	}
	
	/** True if this complex number is equal to zero. */
	public boolean isZero() {
		return Math.abs(real) < E && Math.abs(imag) < E;
	}
	
	/** True if this complex number is real. */
	public boolean isReal() {
		return Math.abs(imag) < E;
	}
	
	/** Returns the absolute value (modulus) of this complex number. */
	public double abs() {
		return Math.sqrt(real * real + imag * imag);
	}
	
	/** Return a new complex number, whose components are multiplied by the scaling factor. */
	public Complex scale(double n) {
		return new Complex(real * n, imag * n);
	}
	
	/** Return the conjugate complex number. */
	public Complex conjugate() {
		return new Complex(real, - imag);
	}
	
	/** Return the complex number whose components are negated. */
	public Complex neg() {
		return new Complex(- real, - imag);
	}
	
	/** Return the complex number whose modulus is normalized to 1, but the ratio between the imaginary component and real component is unchanged. */
	public Complex unit() {
		double mod = this.abs();
		return new Complex(real / mod, imag / mod);
	}
	
	/** The sum of two complex numbers. */
	public Complex add(Complex c) {
		return new Complex(real + c.real, imag + c.imag);
	}
	
	/** The difference of two complex numbers. */
	public Complex subtract(Complex c) {
		return new Complex(real - c.real, imag - c.imag);
	}
	
	/** The product of two complex numbers. */
	public Complex multiply(Complex c) {
		return new Complex(real * c.real - imag * c.imag, real * c.imag + imag * c.real);
	}
	
	/** The quotient of two complex numbers. */
	public Complex divide(Complex c) {
		return this.multiply(c.conjugate()).scale(1 / (c.real * c.real + c.imag * c.imag));
	}
	
	/** The nth roots of unity. */
	public static Complex[] rootsOfUnity(int n) {
		if(n < 1)
			return null;
		Complex[] roots = new Complex[n];
		for(int i = 0 ; i < n ; i++) {
			double ang = (2 * Math.PI * i) / n;
			roots[i] = new Complex(1, ang, true);
		}
		return roots;
	}
	
	/** All nth roots of this complex number. */
	public Complex[] nthRoot(int n) {
		if(n < 1)
			throw new IllegalArgumentException("A complex number cannot be raised to a root less than 1: " + n);
		double theta = Math.atan(imag / real);
		if(real < 0)
			theta += Math.PI;
		Complex[] roots = new Complex[n];
		double scale = Math.pow(this.abs(), 1.0 / n);
		for(int i = 0 ; i < n ; i++)
			roots[i] = new Complex(scale, (theta + 2 * Math.PI * i) / n, true);
		return roots;
	}
	
	/** All square roots of this complex number. */
	public Complex[] sqrt() {
		return nthRoot(2);
	}
	
	/** All cube roots of this complex number. */
	public Complex[] cbrt() {
		return nthRoot(3);
	}
	
	/** Return this complex number raised to an integer power. */
	public Complex pow(int n) {
		if(n < 0)
			return new Complex(1).divide(this.pow(-n));
		if(isReal())
			return new Complex(Math.pow(real, n), 0);
		if(n == 0)
			return new Complex(1);
		double newMod = Math.pow(abs(), n);
		double theta = Math.atan(imag / real);
		if(real < 0)
			theta += Math.PI;
		return new Complex(newMod, n * theta, true);
	}
	
	public String toString() {
		if(imag >= 0)
			return real + " + " + imag + "i";
		return real + " - " + (- imag) + "i";
	}
	
}
