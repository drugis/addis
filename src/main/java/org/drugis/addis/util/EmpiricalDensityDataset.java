package org.drugis.addis.util;

import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.SummaryUtil;
import org.jfree.data.xy.AbstractXYDataset;

public class EmpiricalDensityDataset extends AbstractXYDataset {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9156379642630541775L;
	private static Percentile s_p = new Percentile();
	private double[] d_densities;
	private double [] d_normDensities;

	public EmpiricalDensityDataset(MCMCResults r, Parameter p, int nCategories) {
		double[] allChainsLastHalfSamples = SummaryUtil.getAllChainsLastHalfSamples(r, p);
		double bottom = s_p.evaluate(allChainsLastHalfSamples, 2.5);
		double top = s_p.evaluate(allChainsLastHalfSamples, 97.5);
		double interval = (top - bottom) / nCategories;
		d_densities = new double[nCategories];
		int idx = 0;
		for (int i = 0; i < allChainsLastHalfSamples.length; ++i) {
			double sample = allChainsLastHalfSamples[i];
			if (sample >= bottom && sample <= top) {
				idx = (int) ((sample - bottom) / interval);
				++d_densities[idx];
			}
		}
		d_normDensities = new double[nCategories];
		for (int i = 0; i < nCategories; ++i) {
			d_normDensities[i] = d_densities[i] / allChainsLastHalfSamples.length / interval; 
		}
	}

	@Override
	public int getSeriesCount() {
		return 0;
	}

	@Override
	public Comparable<Integer> getSeriesKey(int arg0) {
		return 0;
	}

	public int getItemCount(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Number getX(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Number getY(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] getDensities() {
		return d_densities;
	}

	public double[] getDoubleDensities() {
		return d_densities;
	}

	public double[] getNormDensities() {
		return d_normDensities;
	}
	
}
