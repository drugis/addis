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

import java.util.Arrays;
import java.util.List;

import com.jgoodies.binding.list.ObservableList;

public class CategoricalPopulationCharacteristic extends PopCharImplPlsDel implements PopulationCharacteristic {
	public static CategoricalPopulationCharacteristic createCategoricalPopulationCharacteristic() {
		return new CategoricalPopulationCharacteristic();
	}

	public static CategoricalPopulationCharacteristic createCategoricalPopulationCharacteristic(
			String name, String[] categories) {
		return new CategoricalPopulationCharacteristic(name, categories);
	}

	public static final String PROPERTY_CATEGORIESASLIST = "categoriesAsList";
	
	private CategoricalPopulationCharacteristic() {
		super("", Type.CATEGORICAL);
	}
	
	private CategoricalPopulationCharacteristic(String name, String[] categories) {
		super(name, Type.CATEGORICAL);
		getCategoriesAsList().addAll(Arrays.asList(categories));
	}

	public String[] getCategories() {
		return getCategoriesAsList().toArray(new String[]{});
	}
	
	public void setCategories(String[] categories) {
		List<String> asList = Arrays.asList(categories);
		setCategoriesAsList(asList);
	}

	public void setCategoriesAsList(List<String> asList) {
		getCategoriesAsList().clear();
		getCategoriesAsList().addAll(asList);
	}

	public ObservableList<String> getCategoriesAsList() {
		return ((CategoricalVariableType) d_varType).getCategories();
	}
}
