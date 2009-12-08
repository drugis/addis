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

public class RiskRatio extends AbstractRatio {
	private static final long serialVersionUID = 3178825436484450721L;

	public RiskRatio(RateMeasurement denominator, RateMeasurement numerator) {
		super(denominator, numerator);
	}

	@Override
	public String toString() {
		return "[" + d_denominator.toString() + "] / [" 
		+ d_numerator.toString() + "]";
	}
	
	public AxisType getAxisType() {
		return AxisType.LOGARITHMIC;
	}

	public Double getError() { //NB: this is the LOG error
		return Math.sqrt((1.0 / this.d_numerator.getRate()) +
				(1.0 / this.d_denominator.getRate()) -
				(1.0 / this.d_numerator.getPatientGroup().getSize()) -
				(1.0 / this.d_denominator.getPatientGroup().getSize()));		
	}

	public String getName() {
		return "Risk ratio";
	}
	
	public Double getRelativeEffect() {
		return ((double) d_numerator.getRate() / (double) d_numerator.getPatientGroup().getSize()) 
			/ ((double) d_denominator.getRate() / (double) d_denominator.getPatientGroup().getSize());  
	}
}
