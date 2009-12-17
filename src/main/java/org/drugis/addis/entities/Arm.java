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

public interface Arm extends Entity {

	public static final String PROPERTY_SIZE = "size";
	public static final String PROPERTY_DRUG = "drug";
	public static final String PROPERTY_DOSE = "dose";

	public Drug getDrug();
	public AbstractDose getDose();
	public Integer getSize();
	
	public VariableMap getCharacteristics();
	
	/**
	 * Gets the characteristic.
	 * 
	 * @param c
	 * @return A characteristic, or null if its not set.
	 */
	public Object getCharacteristic(Variable v);
}