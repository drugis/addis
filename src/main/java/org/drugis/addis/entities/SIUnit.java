/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import java.io.Serializable;

import org.drugis.addis.util.EnumXMLFormat;

public enum SIUnit implements Serializable {
	MILLIGRAMS_A_DAY("Milligrams a Day", "mg/day");
	
	private String d_name;
	private String d_symbol;
	private SIUnit(String name, String symbol) {
		d_name = name;
		d_symbol = symbol;
	}
	
	public String getName() {
		return d_name;
	}
	
	public String getSymbol() {
		return d_symbol;
	}
	
	@Override
	public String toString() {
		return d_symbol;
	}
	
	
	EnumXMLFormat<SIUnit> XML = new EnumXMLFormat<SIUnit>(SIUnit.class);
	
}
