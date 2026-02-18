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
			
			// we normalize the point so x has smallest non-negative numerical value
			long mod = 1L << e;
			long g = Util.gcd(x, mod);
			long quot = x / g;
			this.x = g % mod;
			this.y = this.x > 0 ? y * Util.mod_inv(quot, mod) % mod : 1;
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
	
	@Override
	public String toString()
	{
		return String.format("(%d:%d)", x, y);
	}
}
