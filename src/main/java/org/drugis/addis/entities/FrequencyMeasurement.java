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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drugis.common.EqualsUtil;

import scala.actors.threadpool.Arrays;

public class FrequencyMeasurement extends BasicMeasurement {

	private Map<String, Integer> d_frequencies = new HashMap<String, Integer>();
	private String[] d_categories;
	public static final String PROPERTY_FREQUENCIES = "frequencies";
	
	private FrequencyMeasurement() {
		super(null);
	}
	
	public FrequencyMeasurement(CategoricalPopulationCharacteristic cv) {
		super(null);
		d_categories = cv.getCategories();
		for (String cat : cv.getCategories()) {
			getFrequencies().put(cat, null);
		}
	}
	
	/**
	 * @param map Note: defensively copied. 
	 */
	public FrequencyMeasurement(String[] categories, Map<String, Integer> map) {
		super(null);
		d_categories = categories;
		d_frequencies = new HashMap<String, Integer>();
		for (String cat : d_categories) {
			d_frequencies.put(cat, map.get(cat));
		}
		updateSampleSize();
	}

	private void updateSampleSize() {
		int size = 0;
		for (String cat : d_categories) {
			if (getFrequencies().get(cat) == null) {
				setSampleSize(null);
				return;
			}
			size += getFrequencies().get(cat);
		}
		setSampleSize(size);
	}

	public void setFrequency(String category, Integer freq) throws IllegalArgumentException {
		checkCategory(category);
		Map<String, Integer> oldfreq = new HashMap<String,Integer>(getFrequencies());
		getFrequencies().put(category, freq);
		updateSampleSize();
		firePropertyChange(PROPERTY_FREQUENCIES, oldfreq, getFrequencies());
	}
	
	public Integer getFrequency(String category) throws IllegalArgumentException {
		checkCategory(category);
		return getFrequencies().get(category);
	}

	private void checkCategory(String category) {
		if (!getFrequencies().containsKey(category)) {
			throw new IllegalArgumentException("illegal category");
		}
	}

	public String[] getCategories() {
		return d_categories;
	}
	
	public void add(FrequencyMeasurement other) {
		for (String cat : d_categories) {
			if (getFrequency(cat) == null || other.getFrequency(cat) == null) {
				setFrequency(cat, null);
			} else {
				setFrequency(cat, getFrequency(cat) + other.getFrequency(cat));
			}
		}
	}
		
	public boolean isOfType(Variable.Type type) {
		return type.equals(Variable.Type.CATEGORICAL);
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.emptySet();
	}
	
	@Override
	public String toString() {
		String ret = new String();
		for (String cat : d_categories) {
			if (!ret.equals("")) {
				ret += " / ";
			}
			ret += cat + " = " + (getFrequencies().get(cat) == null ? "N/A" : getFrequencies().get(cat));
		}
		return ret;
	}
	
	@Override
	public FrequencyMeasurement clone() {
		return new FrequencyMeasurement(d_categories, d_frequencies);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FrequencyMeasurement) {
			FrequencyMeasurement m = (FrequencyMeasurement) o;
			if (!Arrays.deepEquals(getCategories(), m.getCategories())) {
				return false;
			}
			return frequenciesEqual(getFrequencies(), m.getFrequencies());
		}
		return false;
	}
	
	private boolean frequenciesEqual(Map<String, Integer> frequencies,
			Map<String, Integer> frequencies2) {
		if (!frequencies.keySet().equals(frequencies2.keySet())) {
			return false;
		}
		for (String key : frequencies.keySet()) {
			if (!EqualsUtil.equal(frequencies.get(key), frequencies2.get(key))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return d_frequencies.hashCode();
	}

	Map<String, Integer> getFrequencies() {
		return d_frequencies;
	}
}
