package org.drugis.addis.treeplot;

/**
 * A BinnedScale maps a real value x to a bin n in some integer range [nMin, nMax].
 * If x would map outside [nMin, nMax], out-of-bounds is returned.
 */
public class BinnedScale {
	public static class Bin {
		public boolean outOfBoundsMin = false;
		public boolean outOfBoundsMax = false;
		public Integer bin = 0;
	}
	
	private int d_min;
	private int d_max;
	private Scale d_scale;

	public BinnedScale(Scale scale, int nMin, int nMax) {
		d_min = nMin;
		d_max = nMax;
		d_scale = scale;
	}
	
	public Bin getBin(double x) {
		Bin b = new Bin();
		b.bin = (int) Math.round(d_scale.getNormalized(x) * (d_max - d_min) + d_min);
		
		if (b.bin > d_max) {
			b.bin = d_max;
			b.outOfBoundsMax = true;
		}
		if (b.bin < d_min) {
			b.bin = d_min;
			b.outOfBoundsMin = true;
		}
		
		return b;
	}
	
	public int getMin() {
		return d_min;
	}
	
	public int getMax() {
		return d_max;
	}
}
