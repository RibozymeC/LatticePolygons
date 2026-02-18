package org.ribozyme.util;

public class Util
{
	public static long gcd(long u, long v)
	{
		u = Math.abs(u);
		v = Math.abs(v);

		while(v != 0) {
			long nu = v;
			long nv = u % v;
			u = nu;
			v = nv;
		}
		
		return u;
	}
	
	/** Calculates b^e mod m.*/
	static long mod_exp(long b, long e, long m)
	{
		if(e == 0) return 1;
		
		boolean invert = false;
		if(e < 0){
			invert = true;
			e = -e;
		}
		
		b %= m;
		
		long y = 1;
		while(e > 0 && y != 0)
		{
			if((e & 1) == 1) y = y * b % m;
			b = b * b % m;
			e >>= 1;
		}
		y %= m;
		if(y < 0) y += m;
		
		if(invert) y = mod_inv(y, m);
		
		return y;
	}
	
	static long mod_inv(long a, long mod)
	{
		a %= mod;
		a += mod;
		a %= mod;
		if(a == 0) throw new ArithmeticException("/ by zero");
		long t = 0, nt = 1;    
		long r = mod, nr = a;    
		while(nr != 0){
			long q = r / nr;
			long h = t;
			t = nt;
			nt = h - q * nt;
			h = r;
			r = nr;
			nr = h - q * nr;
		}
		t += mod;
		t %= mod;
		return t;
	}
}
