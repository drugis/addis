/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.drugis.addis.util.XMLPropertiesFormat;
import org.drugis.addis.util.XMLPropertiesFormat.PropertyDefinition;

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
	public int compareTo(Variable other) {
		return getName().compareTo(other.getName());
	}

	@Override
	public FrequencyMeasurement buildMeasurement(int size) {
		FrequencyMeasurement m = new FrequencyMeasurement(this);
		m.setSampleSize(size);
		return m;
	}
	
	
	@SuppressWarnings("unchecked")
	private List<PropertyDefinition> d_propDefs = Arrays.asList(new PropertyDefinition[]{
			new PropertyDefinition<ArrayList>("categories", ArrayList.class) {
				public ArrayList<String> getValue() { return new ArrayList<String>(getCategoriesAsList()); }
				public void setValue(Object val) { setCategoriesAsList((ArrayList<String>) val); }
			}
	});
	
	protected static final XMLFormat<CategoricalPopulationCharacteristic> CPC_XML = 
		new XMLFormat<CategoricalPopulationCharacteristic>(CategoricalPopulationCharacteristic.class) {

		@Override
		public boolean isReferenceable() { return false; }
		
		@Override
		public void read(InputElement ie, CategoricalPopulationCharacteristic cpc) throws XMLStreamException {
			cpc.setDescription(ie.getAttribute(PROPERTY_DESCRIPTION, null));
			cpc.setName(ie.getAttribute(PROPERTY_NAME, null));
			// read unused unit of measurement attribute for legacy xml
			ie.getAttribute(PROPERTY_UNIT_OF_MEASUREMENT, null); 
			XMLPropertiesFormat.readProperties(ie, cpc.d_propDefs);
		}

		@Override
		public void write(CategoricalPopulationCharacteristic cpc, OutputElement oe) throws XMLStreamException {
			oe.setAttribute(PROPERTY_DESCRIPTION, cpc.getDescription());
			oe.setAttribute(PROPERTY_NAME, cpc.getName());
			XMLPropertiesFormat.writeProperties(cpc.d_propDefs, oe);
		}
	};
}
