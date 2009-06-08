/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nl.rug.escher.addis.entities;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import nl.rug.escher.common.Interval;
import nl.rug.escher.common.StudentTTable;

import org.contract4j5.contract.Contract;
import org.contract4j5.contract.Pre;

import com.jgoodies.binding.beans.Model;

@Contract
public class OddsRatio extends Model implements ContinuousMeasurement {
	private static final long serialVersionUID = 5004304962294140838L;
	private ContinuousMeasurement d_numerator;
	private ContinuousMeasurement d_denominator;
	
	private class ChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getPropertyName().equals(ContinuousMeasurement.PROPERTY_MEAN) ||
					evt.getPropertyName().equals(ContinuousMeasurement.PROPERTY_STDDEV)) {
				firePropertyChange(PROPERTY_LABEL, null, getLabel());
			}
		}
	}
	
	/**
	 * The Peto odds-ratio of two ContinuousMeasurements.
	 * In a forest plot, the numerator will be on the right and the denominator on the left.
	 * @param denominator
	 * @param numerator
	 */
	@Pre("denominator != null && numerator != null && " +
			"denominator.getEndpoint().equals(numerator.getEndpoint())")
	public OddsRatio(ContinuousMeasurement denominator, ContinuousMeasurement numerator) {
		d_numerator = numerator;
		d_denominator = denominator;
		
		PropertyChangeListener listener = new ChangeListener();
		d_numerator.addPropertyChangeListener(listener);
		d_denominator.addPropertyChangeListener(listener);
	}

	public Endpoint getEndpoint() {
		return d_numerator.getEndpoint();
	}

	public String getLabel() {
		DecimalFormat format = new DecimalFormat("0.00");
		Interval<Double> ci = getConfidenceInterval();
		return format.format(getMean()) + " (" + format.format(ci.getLowerBound()) + "-" + 
				format.format(ci.getUpperBound()) + ")";
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
		return qx * Math.sqrt((1 - g) * sq(d_numerator.getStdDev()) / sq(d_numerator.getMean()) +
				sq(d_denominator.getStdDev()) / sq(d_denominator.getMean()));
	}

	private double getAssymmetricalMean(double g) {
		return getMean() / (1 - g);
	}

	private double getG(double t) {
		return sq(t * d_denominator.getStdDev() / d_denominator.getMean());
	}

	private double getCriticalValue() {
		return StudentTTable.getT(getSampleSize() - 2);
	}
	
	public Double getStdDev() {
		double g = getG(getCriticalValue());
		return getStdDev(g, getAssymmetricalMean(g));
	}
	
	private static double sq(double d) {
		return d * d;
	}
	
	/**
	 * Get the mean odds-ratio. This is mean(numerator) / mean(denominator)
	 * @return The mean.
	 */
	public Double getMean() {
		return d_numerator.getMean() / d_denominator.getMean();
	}
}