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

package nl.rug.escher.addis.entities;

import com.jgoodies.binding.beans.Model;


public class RateContinuousAdapter extends Model implements ContinuousMeasurement {
	private static final long serialVersionUID = 3646088897115931916L;
	private RateMeasurement d_measurement;
	
	public RateContinuousAdapter(RateMeasurement m) {
		d_measurement = m;
	}

	public Double getMean() {
		return (double)d_measurement.getRate() / (double)d_measurement.getSampleSize();
	}

	public Double getStdDev() {
		return getMean() / Math.sqrt(d_measurement.getSampleSize());
	}

	public Endpoint getEndpoint() {
		return d_measurement.getEndpoint();
	}

	public String getLabel() {
		return getMean().toString() + " \u00B1 " + getStdDev().toString();
	}

	public Integer getSampleSize() {
		return d_measurement.getSampleSize();
	}
}
