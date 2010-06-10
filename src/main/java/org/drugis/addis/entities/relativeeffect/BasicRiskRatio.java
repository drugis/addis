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

public class BasicRiskRatio extends BasicRatio {

	public BasicRiskRatio(RateMeasurement baseline, RateMeasurement subject) {
		super(baseline, subject);
	}

	@Override
	public String toString() {
		return "[" + d_baseline.toString() + "] / [" 
		+ d_subject.toString() + "]";
	}

	public Double getError() { //NB: this is the LOG error
		if (!isDefined())
			return Double.NaN;

		return Math.sqrt((1.0 / (d_subject.getRate() + d_correction)) +
				(1.0 / (d_baseline.getRate() + d_correction)) -
				(1.0 / (d_subject.getSampleSize())) -
				(1.0 / (d_baseline.getSampleSize())));		
	}

	public String getName() {
		return "Risk ratio";
	}
	
	@Override
	public boolean isDefined() {
		return super.isDefined() && d_baseline.getRate() > 0;
	}

	@Override
	protected double getMu() {
		if (!isDefined())
			return Double.NaN;
		
		double ratio = ( (d_subject.getRate() + d_correction) / (d_subject.getSampleSize()) ) 
			/ ( (d_baseline.getRate() + d_correction) / (d_baseline.getSampleSize()) );
		return Math.log(ratio);
	}

	@Override
	protected Integer getDegreesOfFreedom() {
		return getSampleSize() - 2;
	}

	@Override
	protected double getSigma() {
		return getError();
	}
}
