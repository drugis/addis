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

import org.drugis.addis.entities.ContinuousMeasurement;


public class BasicMeanDifference extends AbstractBasicRelativeEffect<ContinuousMeasurement> {

	/**
	 * The MeanDifference of two ContinuousMeasurements.
	 * In a forest plot, the numerator will be on the right and the denominator on the left.
	 * @param baseline
	 * @param subject
	 */
	
	public BasicMeanDifference(ContinuousMeasurement baseline, ContinuousMeasurement subject) throws IllegalArgumentException {
		super(baseline, subject);
	}

	private double getMu() {
		return d_subject.getMean() - d_baseline.getMean();
	}
	
	@Override
	public Double getError() {
		return Math.sqrt(square(d_subject.getStdDev()) / (double) d_subject.getSampleSize() 
						+ square(d_baseline.getStdDev()) / (double) d_baseline.getSampleSize());
	}
	
	private Double square(double x) {
		return x*x;
	}

	public String getName() {
		return "Mean Difference";
	}

	@Override
	protected Integer getDegreesOfFreedom() {
		return getSampleSize() - 2;
	}

	public Distribution getDistribution() {
		return new TransformedStudentT(getMu(), getError(), getDegreesOfFreedom());
	}
}
