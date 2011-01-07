package org.drugis.addis.util.JSMAAintegration;

import org.drugis.mtc.util.Statistics;

import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.Interval;

public class LogitNormalMeasurement extends GaussianMeasurement {
	private static final long serialVersionUID = -3227427739303388222L;
	
	public LogitNormalMeasurement(double mean, double stdDev) {
		super(mean, stdDev);
	}

	@Override
	public double sample() {
		return Statistics.ilogit(super.sample());
	}
	
	@Override
	public Interval getRange() {
		Interval r = super.getRange();
		return new Interval(Statistics.ilogit(r.getStart()), Statistics.ilogit(r.getEnd()));
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof LogitNormalMeasurement) {
			LogitNormalMeasurement other = (LogitNormalMeasurement)o;
			return other.getMean().equals(getMean()) && other.getStDev().equals(getStDev());
		}
		return false;
	}
	
	@Override
	public LogitNormalMeasurement deepCopy() {
		return new LogitNormalMeasurement(getMean(), getStDev());
	}
}