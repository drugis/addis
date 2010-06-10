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

public class BasicStandardisedMeanDifference extends AbstractBasicRelativeEffect<ContinuousMeasurement> {
	/*
	 * The Standardised Mean Difference is calculated through Cohen's d and adjusted with J(degrees of freedom)
	 * to result in Hedges g. All formulas are based on The Handbook of Research Synthesis and Meta-Analysis 
	 * by Cooper et al. 2nd Edition pages 225-230
	 */
	
	public BasicStandardisedMeanDifference(ContinuousMeasurement baseline,
			ContinuousMeasurement subject) throws IllegalArgumentException {
		super(baseline, subject);
	}

	private double getMu() {
		return getCorrectionJ() * getCohenD();
	}
	
	@Override
	public Double getError() {
		return Math.sqrt(square(getCorrectionJ()) * getCohenVariance());
	}
	
	private double square(double x) {
		return x*x;
	}

	// Package access only:
	double getCohenD() {
		return (d_subject.getMean() - d_baseline.getMean()) / getPooledStdDev();
	}

	double getCohenVariance() {
		double frac1 = (double) getSampleSize() / ((double) d_subject.getSampleSize() *
				(double) d_baseline.getSampleSize());
		double frac2 = square(getCohenD()) / (2D * (double) getSampleSize());
		return (frac1 + frac2);
	}
	
	double getCorrectionJ() {
		return (1 - (3 / (4 * (double) getDegreesOfFreedom() - 1)));
	}
	
	private double getPooledStdDev() {
		double numerator = ((double) d_subject.getSampleSize() - 1) * square(d_subject.getStdDev()) 
							+ ((double) d_baseline.getSampleSize() - 1) * square(d_baseline.getStdDev());
		return Math.sqrt(numerator/(double) getDegreesOfFreedom());
	}
	
	@Override
	protected Integer getDegreesOfFreedom() {
		return getSampleSize() - 2;
	}

	public String getName() {
		return "Standardised Mean Difference";
	}
	
	public Distribution getDistribution() {
		return new TransformedStudentT(getMu(), getError(), getDegreesOfFreedom());
	}
}
