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

package org.drugis.addis.entities;


public class OddsRatio extends AbstractRatio {
	private static final long serialVersionUID = -6897859558867350347L;

	/**
	 * The odds-ratio of two RateMeasurements.
	 * In a forest plot, the numerator will be on the right and the denominator on the left.
	 * @param denominator
	 * @param numerator
	 */
	public OddsRatio(RateMeasurement denominator, RateMeasurement numerator) { 
		super(numerator, denominator);
	}

	public String getName() {
		return "Odds ratio";
	}
	
	public AxisType getAxisType() {
		return AxisType.LOGARITHMIC;
	}

	public Double getRelativeEffect() {
		int d = d_baseline.getPatientGroup().getSize() - d_baseline.getRate();
		int c = d_subject.getPatientGroup().getSize() - d_subject.getRate();
		return ((double) d_subject.getRate() * (double) d) / ((double) d_baseline.getRate() * (double) c); 
	}

	public Double getError() { //NB: this is the LOG error
		return Math.sqrt(invEffect(d_baseline) + invNoEffect(d_baseline) +
		invEffect(d_subject) + invNoEffect(d_subject));
	}

	private double invEffect(RateMeasurement m) {
		return 1.0 / m.getRate();
	}

	private double invNoEffect(RateMeasurement m) {
		return 1.0 / (m.getPatientGroup().getSize() - m.getRate());
	}
}