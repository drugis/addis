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

import java.text.DecimalFormat;


public class BasicContinuousMeasurement extends BasicMeasurement implements ContinuousMeasurement {
	private static final long serialVersionUID = 6086085465347586428L;
	private Double d_mean;
	private Double d_stdDev;
		
	public BasicContinuousMeasurement(Double mean, Double stdDev, Integer size) {
		super(size);
		d_mean = mean;
		d_stdDev = stdDev;
	}
	
	/**
	 * @see org.drugis.addis.entities.ContinuousMeasurement#getMean()
	 */
	public Double getMean() {
		return d_mean;
	}
	
	public void setMean(Double mean) {
		Double oldVal = d_mean;
		d_mean = mean;
		firePropertyChange(PROPERTY_MEAN, oldVal, d_mean);
	}
	
	/**
	 * @see org.drugis.addis.entities.ContinuousMeasurement#getStdDev()
	 */
	public Double getStdDev() {
		return d_stdDev;
	}
	
	public void setStdDev(Double stdDev) {
		Double oldVal = d_stdDev;
		d_stdDev = stdDev;
		firePropertyChange(PROPERTY_STDDEV, oldVal, d_stdDev);
	}
	
	@Override
	public String toString() {
		if (d_mean == null || d_stdDev == null || d_sampleSize == null) {
			return "INCOMPLETE"; 
		}
		
		DecimalFormat df = new DecimalFormat("##0.0##");
		return df.format(d_mean) + " \u00B1 " + df.format(d_stdDev) + " (" + d_sampleSize + ")";
	}

	public boolean isOfType(Variable.Type type) {
		return type.equals(Variable.Type.CONTINUOUS);
	}
}