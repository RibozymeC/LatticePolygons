package org.ribozyme.util;

public record ProjPoint(long x, long y, int e)
{
	public ProjPoint()
	{
		this(0, 0, 0);
	}
	
	public ProjPoint(long x, long y, int e)
	{
		this.e = e;
		if(e > 0) {
			if(x % 2 == 0 && y % 2 == 0)
				throw new IllegalArgumentException(String.format("(%d:%d) is not a valid projective point mod %d!", x, y, 1L << e));
			
			// we normalize the point so x or y (y if y is unit, otherwise x) has smallest non-negative numerical value
			long mod = 1L << e;
			if(y % 2 == 1) {
				long quot = y;
				this.x = x * Util.mod_inv(quot, mod) % mod;
				this.y = 1;
			}
			else {
				long g = Util.gcd(x, mod);
				long quot = x / g;
				this.x = g % mod;
				this.y = this.x > 0 ? y * Util.mod_inv(quot, mod) % mod : 1;
			}
		}
		else {
			this.x = 0;
			this.y = 0;
		}
	}
	
	public ProjPoint[] children()
	{
		if(e == 0)
			return new ProjPoint[] {new ProjPoint(1, 0, 1), new ProjPoint(0, 1, 1), new ProjPoint(1, 1, 1)};
		
		ProjPoint[] children = new ProjPoint[2];
		
		children[0] = new ProjPoint(x, y, e + 1);
		if(x % 2 == 0)
			children[1] = new ProjPoint(x + (1L << e), y, e + 1);
		else
			children[1] = new ProjPoint(x, y + (1L << e), e + 1);
		
		return children;
	}
	
	/**
	 * maps a point from standard basis coordinates into lattice coordinates
	 */
	public Point mapToLattice(Point p)
	{
		if(e == 0)
			return p;
		
		if(y == 1)
			return new Point(p.y() << e, p.x() - x * p.y());
		
		if(x == 1)
			return new Point(p.x() << e, p.y() - y * p.x());
		
		throw new RuntimeException("Point " + this + " not normalized!");
	}
	
	/**
	 * maps a line from standard basis coordinates into lattice coordinates
	 */
	public Line mapToLattice(Line l)
	{
		return new Line(mapToLattice(l.a()), mapToLattice(l.b()));
	}
	
	/**
	 * maps a point from lattice coordinates into standard basis coordinates 
	 */
	public FracPoint mapFromLattice(Point p)
	{
		FracPoint fracp = new FracPoint(p);
		long mod = 1L << e;
		
		if(e == 0)
			return fracp;
		
		if(y == 1) {
			return new FracPoint(fracp.x().multiply(x, mod).add(fracp.y()), fracp.x().multiply(1, mod));
		}
		
		if(x == 1) {
			return new FracPoint(fracp.x().multiply(1, mod), fracp.x().multiply(y, mod).add(fracp.y()));
		}
		
		throw new RuntimeException("Point " + this + " not normalized!");
	}
	
	@Override
	public String toString()
	{
		return String.format("(%d:%d)", x, y);
	}
}
