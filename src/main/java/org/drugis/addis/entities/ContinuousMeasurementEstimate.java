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

package org.drugis.addis.entities;

import java.text.DecimalFormat;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistribution;
import org.apache.commons.math.distribution.NormalDistributionImpl;
import org.drugis.common.Interval;

public class ContinuousMeasurementEstimate extends BasicContinuousMeasurement{
	protected DecimalFormat d_decimalFormatter;

	public ContinuousMeasurementEstimate(Double mean, Double stddev){
		super(mean, stddev, 0);
		d_decimalFormatter = new DecimalFormat("##0.000");
	}
	
	public Interval<Double> getConfidenceInterval() {
		NormalDistribution distribution = new NormalDistributionImpl(getMean(), getStdDev());
		try {
			return new Interval<Double>(distribution.inverseCumulativeProbability(0.025),
					distribution.inverseCumulativeProbability(0.975));
		} catch (MathException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String toString() {
		if (getMean() == null || getStdDev() == null)
			return "n/a"; 
		
		return d_decimalFormatter.format(getMean()) + " (" + d_decimalFormatter.format(getConfidenceInterval().getLowerBound())
				+ ", " + d_decimalFormatter.format(getConfidenceInterval().getUpperBound()) + ")";
	}
}
