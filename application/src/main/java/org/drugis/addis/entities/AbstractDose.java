/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import java.util.Set;

public abstract class AbstractDose extends AbstractEntity {
	protected DoseUnit d_unit;
	public static final String PROPERTY_DOSE_UNIT = "doseUnit";
	public static final String PROPERTY_DOSE_TYPE = "doseType";

	public DoseUnit getDoseUnit() {
		return d_unit;
	}

	public void setDoseUnit(DoseUnit unit) {
		DoseUnit oldVal = d_unit;
		d_unit = unit;
		firePropertyChange(PROPERTY_DOSE_UNIT, oldVal, d_unit);
	}
	
	public Class<? extends AbstractDose> getDoseType() {
		return getClass();
	}

	@Override
	public Set<Entity> getDependencies() {
		return null;
	}
	
	@Override
	public abstract AbstractDose clone();
}
