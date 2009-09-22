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

public class LogOddsRatio extends OddsRatio implements ContinuousMeasurement {
	private static final long serialVersionUID = -9012075635937781733L;
	
	public LogOddsRatio(RateMeasurement denominator, RateMeasurement numerator) {
		super(denominator, numerator);
	}

	public Double getMean() {
		return Math.log(getRatio());
	}

	public Double getStdDev() {
		return Math.sqrt(invEffect(d_denominator) + invNoEffect(d_denominator) +
				invEffect(d_numerator) + invNoEffect(d_numerator));
	}
	
	public String getLabel() {
		DecimalFormat format = new DecimalFormat("0.00");
		return format.format(getMean()) + "\u00B1" + format.format(getStdDev());
	}
	
	public boolean isOfType(Endpoint.Type type) {
		return type.equals(Endpoint.Type.CONTINUOUS);
	}
	
	private double invEffect(RateMeasurement m) {
		return 1.0 / m.getRate();
	}
	
	private double invNoEffect(RateMeasurement m) {
		return 1.0 / (m.getSampleSize() - m.getRate());
	}
}
