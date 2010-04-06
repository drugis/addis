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

import java.util.Collections;
import java.util.Set;

public abstract class BasicMeasurement extends AbstractEntity implements Measurement {
	private static final long serialVersionUID = 6892934487858770855L;
	protected Integer d_sampleSize;

	public BasicMeasurement(int size) {
		d_sampleSize = size;
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}

	public void setSampleSize(Integer size) {
		Integer oldVal = d_sampleSize;
		d_sampleSize = size;
		firePropertyChange(PROPERTY_SAMPLESIZE, oldVal, d_sampleSize);
	}

	public Integer getSampleSize() {
		return d_sampleSize;
	}
	
	
	
	// XML Formatter
	
}