package org.drugis.addis.entities;

import org.drugis.common.Interval;

public interface RelativeEffect<T extends Measurement> {

	public T getSubject();

	public T getBaseline();

	public Endpoint getEndpoint();

	public Integer getSampleSize();

	/**
	 * Get the 95% confidence interval.
	 * @return The confidence interval.
	 */
	public Interval<Double> getConfidenceInterval();

	public Double getRelativeEffect();

}
