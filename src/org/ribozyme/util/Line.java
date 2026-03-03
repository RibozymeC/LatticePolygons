package org.ribozyme.util;

import java.util.HashSet;
import java.util.Set;

public record Line(Point a, Point b)
{
	public long latticePoints(ProjPoint lattice)
	{
		long dx = a.x() - b.x(), dy = a.y() - b.y();
		long g = Util.gcd(dx, dy);
		dx /= g;
		dy /= g;
		
		return g * Util.gcd(dy * lattice.x() - dx * lattice.y(), lattice.mod());
	}
	
	public boolean contains(ProjPoint lattice)
	{
		long dx = a.x() - b.x(), dy = a.y() - b.y();
		long g = Util.gcd(dx, dy);
		dx /= g;
		dy /= g;
		
		return (dy * lattice.x() - dx * lattice.y()) % lattice.mod() == 0;
	}
	
	public Set<Point> points()
	{
		long dx = a.x() - b.x();
		long dy = a.y() - b.y();
		long g = Util.gcd(dx, dy);
		
		if(g == 0)
			return Set.of(a);
		
		Set<Point> points = new HashSet<>();
		
		for(int i = 0; i <= g; i++) {
			points.add(new Point(b.x() + i * dx / g, b.y() + i * dy / g));
		}
		
		return points;
	}
}
