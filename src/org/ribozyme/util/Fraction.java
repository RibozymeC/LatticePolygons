package org.ribozyme.util;

public record Fraction(long num, long denom) implements Comparable<Fraction>
{
	public static final Fraction ZERO = valueOf(0);
	public static final Fraction ONE = valueOf(1);
	public static final Fraction MONE = valueOf(-1);
	public static final Fraction TWO = valueOf(2);
	public static final Fraction HALF = ONE.half();
	
	public Fraction(long num, long denom)
	{
		if(denom == 0)
			throw new ArithmeticException("Division by zero!");
		if(denom < 0) {
			num = -num;
			denom = -denom;
		}
		long g = Util.gcd(num, denom);
		this.num = num / g;
		this.denom = denom / g;
	}
	
	public Fraction(Fraction f)
	{
		this(f.num, f.denom);
	}
	
	public static Fraction valueOf(long n)
	{
		return new Fraction(n, 1);
	}
	
//mathematical operations
	
	/**
	 * @param f
	 * @return this + f
	 */
	public Fraction add(Fraction f)
	{
		return new Fraction(num * f.denom + f.num * denom, denom * f.denom);
	}
	
	/**
	 * @param f
	 * @return this - f
	 */
	public Fraction subtract(Fraction f)
	{
		return new Fraction(num * f.denom - f.num * denom, denom * f.denom);
	}
	
	public Fraction negate()
	{
		return new Fraction(-num, denom);
	}
	
	public Fraction half()
	{
		return new Fraction(num, 2 * denom);
	}
	
	public Fraction doublef()
	{
		return new Fraction(2 * num, denom);
	}
	
	public Fraction reciproc()
	{
		if(num == 0) throw new ArithmeticException();
		return new Fraction(denom, num);
	}
	
	public Fraction multiply(Fraction f)
	{
		return new Fraction(num * f.num, denom * f.denom);
	}
	
	public Fraction divide(Fraction f)
	{
		return new Fraction(num * f.denom, denom * f.num);
	}
	
	public Fraction square()
	{		
		return new Fraction(num * num, denom * denom);
	}
	
	public Fraction multiply(long n, long d)
	{
		return new Fraction(num * n, denom * d);
	}
	
	public double asDouble()
	{
		return (double)num / (double)denom;
	}
	
	public boolean isZero()
	{
		return num == 0;
	}
	
	public boolean isInteger()
	{
		return denom == 1;
	}
	
	public boolean isPositive()
	{
		return num > 0;
	}
	
	public long floor()
	{
		return Math.floorDiv(num, denom);
	}
	
	public long round()
	{
		return Math.floorDiv(2 * num + denom, 2 * denom);
	}
	
	public long ceil()
	{
		return Math.floorDiv(num + denom - 1, denom);
	}
	
	public Fraction abs()
	{
		if(num >= 0)
			return this;
		else
			return this.negate();
	}
	
//standard methods

	@Override
	public int compareTo(Fraction r)
	{
		return Long.compare(this.subtract(r).num, 0);
	}
	
	@Override
	public String toString()
	{
		return denom == 1 ? String.format("%d", num) : String.format("%d/%d", num, denom);
	}
}