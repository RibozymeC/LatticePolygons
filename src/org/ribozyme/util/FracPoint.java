package org.ribozyme.util;

public record FracPoint(Fraction x, Fraction y)
{
	public FracPoint(Point p)
	{
		this(Fraction.valueOf(p.x()), Fraction.valueOf(p.y()));
	}
}
