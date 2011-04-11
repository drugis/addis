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

import org.drugis.common.EqualsUtil;

public class CategoricalPopulationCharacteristic extends AbstractVariable implements PopulationCharacteristic {
	private String[] d_categories;
	
	public static final String PROPERTY_CATEGORIESASLIST = "categoriesAsList";
	
	public CategoricalPopulationCharacteristic() {
		super("", Type.CATEGORICAL);
		d_categories = new String[]{};
	}
	
	public CategoricalPopulationCharacteristic(String name, String[] categories) {
		super(name, Type.CATEGORICAL);
		d_categories = categories;
		d_description = "";
	}
	
	public String[] getCategories() {
		return d_categories;
	}
	
	public void setCategories(String[] categories) {
		d_categories = categories;
	}
	
	public List<String> getCategoriesAsList() {
		return Arrays.asList(d_categories);
	}
	
	public void setCategoriesAsList(List<String> categories) {
		setCategories((String[]) categories.toArray(d_categories));
		firePropertyChange(PROPERTY_CATEGORIESASLIST, null, categories);
	}
	
	@Override
	public FrequencyMeasurement buildMeasurement() {
		return new FrequencyMeasurement(this);
	}

	@Override
	public FrequencyMeasurement buildMeasurement(int size) {
		FrequencyMeasurement m = new FrequencyMeasurement(this);
		m.setSampleSize(size);
		return m;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof PopulationCharacteristic) {
			return super.equals(o);			
		}
		return false;
	}
	
	@Override
	public boolean deepEquals(Entity obj) {
		if (super.deepEquals(obj)) {
			CategoricalPopulationCharacteristic other = (CategoricalPopulationCharacteristic)obj;
			return EqualsUtil.equal(other.getCategoriesAsList(), getCategoriesAsList()); 
		}
		return false;
	}
}
