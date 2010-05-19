package org.drugis.addis.entities.metaanalysis;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Set;

import org.drugis.addis.entities.AbstractDistribution;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.common.Interval;

public class MetaAnalysisRelativeEffect<T extends Measurement> extends AbstractDistribution implements RelativeEffect<T> {
	private double d_mu;
	private int d_totalSampleSize;
	private double d_sigma;
	private RelativeEffect.AxisType d_type;
	
	public MetaAnalysisRelativeEffect(double mu, double sigma, RelativeEffect.AxisType type) {
		this(mu,sigma,0,type);
	}
	
	public MetaAnalysisRelativeEffect(double mu, double sigma, 
			int totalSampleSize, RelativeEffect.AxisType type) {
		assert(sigma >= 0); // FIXME: Breaks a test in mvn, probably due to bad testdata.
		d_mu = mu;
		d_totalSampleSize = totalSampleSize;
		d_sigma = sigma;
		d_type = type;
	}
	
	public RelativeEffect.AxisType getAxisType() {
		return d_type;
	}

	
	public Double getMu() {
		return d_mu;
	}
	
	public Integer getSampleSize() {
		return d_totalSampleSize;
	}

	public String getName() {
		return "Random Effects";
	}		

	public T getSubject() {
		throw new RuntimeException("Cannot get a Subject Measurement from Random Effects (Meta-Analysis)");
	}
	
	public T getBaseline() {
		throw new RuntimeException("Cannot get a Baseline Measurement from Random Effects (Meta-Analysis)");
	}

	public Double getSigma() {
		return d_sigma;
	}
	
	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	public boolean isDefined() {
		return true;
	}	
	
	public String toString() {
		DecimalFormat formatter = new DecimalFormat("##0.000");
		return formatter.format(d_mu) + " (" + 
		       formatter.format(getConfidenceInterval().getLowerBound()) + ", " + 
		       formatter.format(getConfidenceInterval().getUpperBound()) + ")";
	}

	public Interval<Double> getConfidenceInterval() {
		return null;
	}

	@Override
	public Integer getDegreesOfFreedom() {
//		The sample size is set to 0 if unknown
		if (getSampleSize() > 2)
			return getSampleSize() -2;
		else
			return Integer.MAX_VALUE;
	}
}