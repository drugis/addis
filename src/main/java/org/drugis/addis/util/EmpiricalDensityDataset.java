package org.drugis.addis.util;

import org.apache.commons.math.stat.descriptive.rank.Percentile;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.MCMCResultsListener;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.SummaryUtil;
import org.jfree.data.xy.AbstractXYDataset;

public class EmpiricalDensityDataset extends AbstractXYDataset {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9156379642630541775L;
	private static Percentile s_p = new Percentile();
	private int[] d_counts;
	private double[] d_densities;
	private double d_interval;
	private double d_bottom;
	private Parameter d_parameter;
	private final MCMCResults d_results;
	private final int d_nBins;

	public EmpiricalDensityDataset(MCMCResults r, Parameter p, int nBins) {
		d_results = r;
		d_parameter = p;
		d_nBins = nBins;
		d_densities = new double[nBins];
		
		r.addResultsListener(new MCMCResultsListener() {
			public void resultsEvent(MCMCResultsEvent event) {
				calculateDensity();
				fireDatasetChanged();
			}
		});
		
		if (d_results.getNumberOfSamples() > 0) {
			calculateDensity();
		}
	}

	private void calculateDensity() {
		double[] allChainsLastHalfSamples = SummaryUtil.getAllChainsLastHalfSamples(d_results, d_parameter);
		d_bottom = s_p.evaluate(allChainsLastHalfSamples, 2.5);
		double top = s_p.evaluate(allChainsLastHalfSamples, 97.5);
		d_interval = (top - d_bottom) / d_nBins;
		d_counts = new int[d_nBins];
		int idx = 0;
		for (int i = 0; i < allChainsLastHalfSamples.length; ++i) {
			double sample = allChainsLastHalfSamples[i];
			if (sample >= d_bottom && sample < top) {
				idx = (int) ((sample - d_bottom) / d_interval);
				++d_counts[idx];
			}
		}
		d_densities = new double[d_nBins];
		double factor = allChainsLastHalfSamples.length * d_interval;
		for (int i = 0; i < d_nBins; ++i) {
			d_densities[i] = d_counts[i] / factor; 
		}
	}

	@Override
	public int getSeriesCount() {
		return 1;
	}

	@Override
	public Comparable<String> getSeriesKey(int series) {
		return d_parameter.getName();
	}

	public Double getX(int series, int bin) {
		if( series != 0 ) throw new IndexOutOfBoundsException();
		return (0.5 + bin) * d_interval + d_bottom; 
	}

	public Double getY(int series, int bin) {
		if( series != 0 ) throw new IndexOutOfBoundsException();
		return d_densities[bin];
	}

	public int[] getCounts() {
		return d_counts;
	}

	public double[] getDensities() {
		return d_densities;
	}

	public int getItemCount(int series) {
		return d_nBins;
	}
	
}
