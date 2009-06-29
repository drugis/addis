package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import nl.rug.escher.common.Interval;
import nl.rug.escher.common.StudentTTable;

import com.jgoodies.binding.beans.Model;

public abstract class Ratio extends Model implements Measurement {
	private static final long serialVersionUID = 1647344976539753330L;

	private class ChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(RateMeasurement.PROPERTY_RATE) ||
					evt.getPropertyName().equals(RateMeasurement.PROPERTY_SAMPLESIZE)) {
				firePropertyChange(PROPERTY_LABEL, null, getLabel());
			}
		}
	}
	
	protected Ratio(RateMeasurement denominator, RateMeasurement numerator) {
		d_numerator = numerator;
		d_denominator = denominator;
		
		PropertyChangeListener listener = new ChangeListener();
		d_numerator.addPropertyChangeListener(listener);
		d_denominator.addPropertyChangeListener(listener);
	}

	protected RateMeasurement d_numerator;

	protected double getStdDev(RateMeasurement m) {
		return getMean(m) / Math.sqrt(m.getSampleSize());
	}

	protected abstract double getMean(RateMeasurement m);

	protected RateMeasurement d_denominator;

	private static double sq(double d) {
		return d * d;
	}

	public Endpoint getEndpoint() {
		return d_numerator.getEndpoint();
	}

	public Integer getSampleSize() {
		return d_numerator.getSampleSize() + d_denominator.getSampleSize();
	}

	/**
	 * Get the 95% confidence interval.
	 * @return The confidence interval.
	 */
	public Interval<Double> getConfidenceInterval() {
		double g = getG(getCriticalValue());
		double qx = getAssymmetricalMean(g);
		double sd = getStdDev(g, qx);
		
		return new Interval<Double>((qx - getCriticalValue() * sd), (qx + getCriticalValue() * sd));
	}

	private double getStdDev(double g, double qx) {
		return qx * Math.sqrt((1 - g) * sq(getStdDev(d_numerator)) / sq(getMean(d_numerator)) +
				sq(getStdDev(d_denominator)) / sq(getMean(d_denominator)));
	}

	private double getAssymmetricalMean(double g) {
		return getRatio() / (1 - g);
	}

	private double getG(double t) {
		return sq(t * getStdDev(d_denominator) / getMean(d_denominator));
	}

	private double getCriticalValue() {
		return StudentTTable.getT(getSampleSize() - 2);
	}

	public Double getError() {
		double g = getG(getCriticalValue());
		return getStdDev(g, getAssymmetricalMean(g));
	}

	public Double getRatio() {
		return getMean(d_numerator) / getMean(d_denominator);
	}

	public String getLabel() {
		DecimalFormat format = new DecimalFormat("0.00");
		Interval<Double> ci = getConfidenceInterval();
		return format.format(getRatio()) + " (" + format.format(ci.getLowerBound()) + "-" + 
				format.format(ci.getUpperBound()) + ")";
	}
}