package org.ribozyme.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ProjPoint(long x, long y, int p, int e)
{
	public ProjPoint(int p)
	{
		this(0, 0, p, 0);
	}
	
	public ProjPoint(long x, long y, int p, int e)
	{
		this.e = e;
		this.p = p;
		
		if(e > 0) {
			long mod = Util.pow(p, e);
			
			if(x % p == 0 && y % p == 0)
				throw new IllegalArgumentException(String.format("(%d:%d) is not a valid projective point mod %d!", x, y, mod));
			
			// we normalize the point so x or y (y if y is unit, otherwise x) is 1
			if(y % p != 0) {
				this.x = x * Util.mod_inv(y, mod) % mod;
				this.y = 1;
			}
			else {
				this.x = 1;
				this.y = y * Util.mod_inv(x, mod) % mod;
			}
		}
		else {
			this.x = 0;
			this.y = 0;
		}
	}
	
	public long mod()
	{
		return Util.pow(p, e);
	}
	
	public List<ProjPoint> children()
	{
		Set<ProjPoint> children = new HashSet<>();
		
		long mod = mod();
		
		for(int i = 0; i < p; i++)
			for(int j = (e==0&&i==0?1:0); j < p; j++) {
				children.add(new ProjPoint(x + i * mod, y + j * mod, p, e + 1));
			}
		
		return List.copyOf(children);
	}
	
	/**
	 * maps a point from standard basis coordinates into lattice coordinates
	 */
	public Point mapToLattice(Point P)
	{
		if(e == 0)
			return P;
		
		if(y == 1)
			return new Point(mod() * P.y(), P.x() - x * P.y());
		
		if(x == 1)
			return new Point(mod() * P.x(), P.y() - y * P.x());
		
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
	public FracPoint mapFromLattice(Point P)
	{
		FracPoint fracp = new FracPoint(P);
		
		if(e == 0)
			return fracp;
		
		long mod = mod();
		
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
		return String.format("%d:%d", x, y);
	}
}
