package org.drugis.addis.entities.relativeeffect;

public interface Distribution {
	/**
	 * Get the p-probability quantile.
	 * @param p probability in [0, 1]
	 * @return the quantile.
	 */
	public double getQuantile(double p);
	
	/**
	 * Get the axis type (does it make sense to plot this on a normal or log scale?
	 */
	public AxisType getAxisType();
}
