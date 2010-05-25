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

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Measurement;
import org.drugis.common.Interval;

public interface RelativeEffect<T extends Measurement> extends Entity{
	@Deprecated
	public static final String PROPERTY_SAMPLESIZE = "sampleSize";
	
	public T getSubject();

	public T getBaseline();

	@Deprecated
	public Integer getSampleSize();

	
	public Distribution getDistribution();
	
	/**
	 * Get the 95% confidence interval.
	 * @return The confidence interval.
	 */
	@Deprecated
	public Interval<Double> getConfidenceInterval();

	@Deprecated
	public Double getRelativeEffect();

	@Deprecated
	public Double getError();
	
	public String getName();
	
	public AxisType getAxisType();
	
	public boolean isDefined();
}
