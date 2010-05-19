package org.drugis.addis.entities;

import org.drugis.addis.entities.Distribution.AxisType;
import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;


public abstract class AbstractDistribution extends AbstractEntity implements Distribution {

	public Interval<Double> getConfidenceInterval() {
		if (!isDefined()) {
			return new Interval<Double>(Double.NaN, Double.NaN);
		}
		double t = StudentTTable.getT(getDegreesOfFreedom());

		if (getAxisType() == AxisType.LINEAR)
			return new Interval<Double>(getMu() - t * getSigma(), getMu() + t * getSigma());
		else if (getAxisType() == AxisType.LOGARITHMIC)
			return new Interval<Double>(Math.exp(getMu() - t * getSigma()), Math.exp(getMu() + t * getSigma()));
		throw new IllegalStateException("Unknown axis type");
	}
	
	public Double getMedian() {
		if (getAxisType() == AxisType.LINEAR)
			return getMu();
		else if (getAxisType() == AxisType.LOGARITHMIC)
			return Math.exp(getMu());
		throw new IllegalStateException("Unknown axis type");
	}
	
	protected abstract Integer getDegreesOfFreedom();
	
	protected abstract boolean isDefined();
	
	public abstract Double getSigma();

	public abstract Double getMu();
	
}
