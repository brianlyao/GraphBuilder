

public class Complex {
	
	private static final double E = 1e-10;
	
	private double real;
	private double imag;
	
	public Complex(double r){
		real = r;
		imag = 0;
	}
	
	public Complex(double r, double i){
		real = r;
		imag = i;
	}
	
	public Complex(double len, double rads, boolean isPolar){
		real = len * Math.cos(rads);
		imag = len * Math.sin(rads);
	}
	
	public double getReal(){
		return real;
	}
	
	public double getImag(){
		return imag;
	}
	
	public void setReal(double r){
		real = r;
	}
	
	public void setImag(double i){
		imag = i;
	}
	
	public boolean isZero(){
		return Math.abs(real) < E && Math.abs(imag) < E;
	}
	
	public boolean isReal(){
		return Math.abs(imag) < E;
	}
	
	public double abs(){
		return Math.sqrt(real * real + imag * imag);
	}
	
	public Complex scale(double n){
		return new Complex(real * n, imag * n);
	}
	
	public Complex conjugate(){
		return new Complex(real, - imag);
	}
	
	public Complex neg(){
		return new Complex(- real, - imag);
	}
	
	public Complex unit(){
		double mod = this.abs();
		return new Complex(real / mod, imag / mod);
	}
	
	public Complex add(Complex c){
		return new Complex(real + c.real, imag + c.imag);
	}
	
	public Complex subtract(Complex c){
		return new Complex(real - c.real, imag - c.imag);
	}
	
	public Complex multiply(Complex c){
		return new Complex(real * c.real - imag * c.imag, real * c.imag + imag * c.real);
	}
	
	public Complex divide(Complex c){
		return this.multiply(c.conjugate()).scale(1 / (c.real * c.real + c.imag * c.imag));
	}
	
	public static Complex[] rootsOfUnity(int n){
		if(n < 1)
			return null;
		Complex[] roots = new Complex[n];
		for(int i = 0 ; i < n ; i++){
			double ang = (2 * Math.PI * i) / n;
			roots[i] = new Complex(1, ang, true);
		}
		return roots;
	}
	
	public Complex[] nthRoot(int n){
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
	
	public Complex[] sqrt(){
		return nthRoot(2);
	}
	
	public Complex[] cbrt(){
		return nthRoot(3);
	}
	
	public Complex pow(int n){
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
	
	public String toString(){
		if(imag >= 0)
			return real + " + " + imag + "i";
		return real + " - " + (- imag) + "i";
	}
}
