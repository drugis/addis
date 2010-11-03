package org.drugis.addis.mocks;

import java.util.Random;

import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.NormalSummary;

public class MockNormalSummary extends NormalSummary {

	private double d_mean;
	private double d_stdDev;
	private static final Random RANDOM = new Random();

	public MockNormalSummary(MCMCResults results, Parameter parameter) {
		super(results, parameter);
		createResults();
	}

	@Override
	public void resultsEvent(MCMCResultsEvent event) {
		createResults();
	}

	private void createResults() {
		if (!isDefined()) return;
		d_mean = RANDOM.nextDouble() * 4 - 2;
		d_stdDev = RANDOM.nextDouble() * 1.5;
	}
	
	public void fireChange() {
		firePropertyChange(PROPERTY_MEAN, null, d_mean);
	}
	
	@Override
	public double getMean() {
		return d_mean;
	}
	
	@Override
	public double getStandardDeviation() {
		return d_stdDev;
	}
}
