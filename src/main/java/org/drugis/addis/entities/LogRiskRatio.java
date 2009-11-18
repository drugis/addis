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

import org.drugis.common.Interval;

public class LogRiskRatio extends RiskRatio {

	private static final long serialVersionUID = 5344954293964132074L;

	public LogRiskRatio(RateMeasurement denominator, RateMeasurement numerator) {
		super(denominator, numerator);
	}
	
	@Override
	public Double getError() {
		return Math.sqrt((1.0 / this.d_numerator.getRate()) +
		(1.0 / this.d_denominator.getRate()) -
		(1.0 / this.d_numerator.getSampleSize()) -
		(1.0 / this.d_denominator.getSampleSize()));
	}
	
	@Override
	public Double getRelativeEffect() {
		return Math.log(super.getRelativeEffect());
	}
	
	@Override
	public double getCriticalValue() {
		return super.getCriticalValue();
	}
	
	@Override
	public Interval<Double> getConfidenceInterval() {
		double lBound = getRelativeEffect();
		lBound -= getCriticalValue() * getError();
		double uBound = getRelativeEffect();
		uBound += getCriticalValue() * getError();
		return new Interval<Double>(lBound, uBound);
	}
}
