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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class CategoricalVariableType extends AbstractEntity implements VariableType {
	public static final String PROPERTY_CATEGORIES = "categories";
	private ObservableList<String> d_cats = new ArrayListModel<String>();
	
	public CategoricalVariableType() {
	}
	
	public CategoricalVariableType(List<String> cats) {
		d_cats.addAll(cats);
	}

	public BasicMeasurement buildMeasurement() {
		return new FrequencyMeasurement(d_cats, new HashMap<String, Integer>());
	}

	public BasicMeasurement buildMeasurement(int size) {
		return new FrequencyMeasurement(d_cats, new HashMap<String, Integer>());
	}

	public String getType() {
		return "Categorical";
	}

	public Set<? extends Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	public ObservableList<String> getCategories() {
		return d_cats;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CategoricalVariableType) {
			CategoricalVariableType other = (CategoricalVariableType) obj;
			return EqualsUtil.equal(d_cats, other.d_cats);
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return d_cats == null ? 0 : d_cats.hashCode();
	}
	
	@Override
	public String toString() {
		return getType();
	}
}
