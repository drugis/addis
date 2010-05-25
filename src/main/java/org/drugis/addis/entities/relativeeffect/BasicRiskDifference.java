/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

public class BasicRiskDifference extends BasicRelativeEffect<RateMeasurement> {

	public BasicRiskDifference(RateMeasurement denominator, RateMeasurement numerator) {
		super(numerator, denominator);
	}

	public Double getRelativeEffect() {
		double a = getSubject().getRate();
		double n1 = getSubject().getSampleSize();
		double c = getBaseline().getRate();
		double n2 = getBaseline().getSampleSize();
		
		return (a/n1 - c/n2);
	}

	// Here: gets the STANDARD ERROR of the RISK DIFFERENCE
	public Double getError() {
		double a = getSubject().getRate();
		double n1 = getSubject().getSampleSize();
		double b = n1 - a;
		double c = getBaseline().getRate();
		double n2 = getBaseline().getSampleSize();
		double d = n2 - c;
		
		return new Double(Math.sqrt(a*b/Math.pow(n1,3) + c*d/Math.pow(n2,3)));
	}

	public String getName() {
		return "Risk Difference";
	}

	@Override
	protected Integer getDegreesOfFreedom() {
		return getSampleSize() - 2;
	}

	public Distribution getDistribution() {
		return new TransformedStudentT(getRelativeEffect(), getError(), getDegreesOfFreedom());
	}
}
