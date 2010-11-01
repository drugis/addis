/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

public class BasicRiskDifference extends AbstractBasicRelativeEffect<RateMeasurement> implements BasicRateRelativeEffect {

	public BasicRiskDifference(RateMeasurement baseline, RateMeasurement subject) {
		super(baseline, subject);
	}

	protected double getMu() {
		if (!isDefined())
			return Double.NaN;
		double a = getA();
		double n1 = getA() + getB();
		double c = getC();
		double n2 = getC() + getD();
		
		return (a/n1 - c/n2);
	}

	// Here: gets the STANDARD ERROR of the RISK DIFFERENCE
	@Override
	public Double getError() {
		if (!isDefined())
			return Double.NaN;
		
		double a = getA();
		double b = getB();
		double n1 = a + b;

		double c = getC();
		double d = getD();
		double n2 = c + d;
		
		return new Double(Math.sqrt(a*b/Math.pow(n1,3) + c*d/Math.pow(n2,3)));
	}

	@Override
	public boolean isDefined() {
		return super.isDefined() && (d_baseline.getRate() > 0 || d_subject.getRate() > 0);
	}

	public String getName() {
		return "Risk Difference";
	}

	@Override
	protected Integer getDegreesOfFreedom() {
		return getSampleSize() - 2;
	}

	public Distribution getDistribution() {
		return new TransformedStudentT(getMu(), getError(), getDegreesOfFreedom());
	}

	public RelativeEffect<RateMeasurement> getCorrected() {
		return new CorrectedBasicRiskDifference(this);
	}

	protected double getA() {
		return getSubject().getRate();
	}

	protected double getB() {
		return getSubject().getSampleSize() - getSubject().getRate();
	}

	protected double getC() {
		return getBaseline().getRate();
	}

	protected double getD() {
		return getBaseline().getSampleSize() - getBaseline().getRate();
	}
	
}