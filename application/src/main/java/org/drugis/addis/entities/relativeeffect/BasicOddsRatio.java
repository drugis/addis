/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

package org.drugis.addis.entities.relativeeffect;

import org.drugis.addis.entities.RateMeasurement;


public class BasicOddsRatio extends BasicRatio {

	/**
	 * The odds-ratio of two RateMeasurements.
	 * In a forest plot, the numerator will be on the right and the denominator on the left.
	 * @param baseline
	 * @param subject
	 */
	public BasicOddsRatio(RateMeasurement baseline, RateMeasurement subject) { 
		super(baseline, subject);
	}

	public String getName() {
		return "Odds ratio";
	}
	
	@Override
	public AxisType getAxisType() {
		return AxisType.LOGARITHMIC;
	}
	
	@Override
	public boolean isDefined() {
		return  super.isDefined() && isAdmissible(d_subject) && isAdmissible(d_baseline);
	}

	private boolean isAdmissible(RateMeasurement measurement) {
		return measurement.getRate() > 0 && measurement.getRate() < measurement.getSampleSize();
	}

	@Override
	protected double getMu() {
		if (!isDefined())
			return Double.NaN;
		
		double a = getA();
		double b = getB();
		
		double c = getC();
		double d = getD();
		
		return Math.log((a * d) / (b * c)); 
	}

	@Override
	public Double getError() {
		if (!isDefined())
			return Double.NaN;
		
		double a = getA();
		double b = getB();
		
		double c = getC();
		double d = getD();
		
		return Math.sqrt(1.0/a + 1.0/b + 1.0/c + 1.0/d);
	}

	@Override
	protected Integer getDegreesOfFreedom() {
		return getSampleSize() - 2;
	}

	@Override
	protected double getSigma() {
		return getError();
	}

	public RelativeEffect<RateMeasurement> getCorrected() {
		return new CorrectedBasicOddsRatio(this);
	}
	
	protected double getA() {
		return d_subject.getRate();
	}

	protected double getB() {
		return d_subject.getSampleSize() - d_subject.getRate();
	}
	
	protected double getC() {
		return d_baseline.getRate();
	}

	protected double getD() {
		return d_baseline.getSampleSize() - d_baseline.getRate();
	}
}