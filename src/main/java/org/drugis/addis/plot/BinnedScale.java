package org.drugis.addis.plot;

/**
 * A BinnedScale maps a real value x to a bin n in some integer range [nMin, nMax].
 * If x would map outside [nMin, nMax], out-of-bounds is returned.
 */
public interface BinnedScale {
	public static class Bin {
		public boolean outOfBoundsMin;
		public boolean outOfBoundsMax;
		public Integer bin;
	}
	
	public Bin getBin(double x);
	
	public int getMin();
	public int getMax();
}
