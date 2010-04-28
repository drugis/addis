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

package org.drugis.addis.entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FrequencyMeasurement extends BasicMeasurement {

	private CategoricalPopulationCharacteristic d_cv;
	
	private Map<String, Integer> d_frequencies = new HashMap<String, Integer>();
	 
	public static final String PROPERTY_FREQUENCIES = "frequencies";

	public FrequencyMeasurement(CategoricalPopulationCharacteristic cv) {
		super(0);
		d_cv = cv;
		for (String cat : d_cv.getCategories()) {
			d_frequencies.put(cat, new Integer(0));
		}
	}
	
	private void updateSampleSize() {
		int size = 0;
		for (String cat : d_cv.getCategories()) {
			size += d_frequencies.get(cat).intValue();
		}
		setSampleSize(size);
	}

	public void setFrequency(String category, int freq) throws IllegalArgumentException {
		checkCategory(category);
		Map<String, Integer> oldfreq = new HashMap<String,Integer>(d_frequencies);
		d_frequencies.put(category, freq);
		updateSampleSize();
		firePropertyChange(PROPERTY_FREQUENCIES, oldfreq, d_frequencies);
	}
	
	public int getFrequency(String category) throws IllegalArgumentException {
		checkCategory(category);
		return d_frequencies.get(category).intValue();
	}

	private void checkCategory(String category) {
		if (!d_frequencies.containsKey(category)) {
			throw new IllegalArgumentException("illegal category");
		}
	}
	
	public CategoricalPopulationCharacteristic getCategoricalVariable() {
		return d_cv;
	}
	
	public FrequencyMeasurement deepCopy() {
		FrequencyMeasurement m = new FrequencyMeasurement(getCategoricalVariable());
		for (String cat : d_cv.getCategories()) {
			m.setFrequency(cat, getFrequency(cat));
		}
		return m;
	}
	
	public void add(FrequencyMeasurement other) {
		for (String cat : d_cv.getCategories()) {
			setFrequency(cat, getFrequency(cat) + other.getFrequency(cat));
		}
	}
		
	public boolean isOfType(Variable.Type type) {
		return false;
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.<Entity>singleton(d_cv);
	}
	
	@Override
	public String toString() {
		String ret = new String();
		for (String cat : d_cv.getCategories()) {
			if (!ret.equals("")) {
				ret += " / ";
			}
			ret += cat + " = " + d_frequencies.get(cat).intValue();
		}
		return ret;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FrequencyMeasurement) {
			FrequencyMeasurement m = (FrequencyMeasurement) o;
			if (!m.getCategoricalVariable().equals(getCategoricalVariable())) {
				return false;
			}
			return frequenciesEqual(d_frequencies, m.d_frequencies);
		}
		return false;
	}
	
	private boolean frequenciesEqual(Map<String, Integer> frequencies,
			Map<String, Integer> frequencies2) {
		if (!frequencies.keySet().equals(frequencies2.keySet())) {
			return false;
		}
		for (String key : frequencies.keySet()) {
			if (frequencies.get(key).intValue() != frequencies2.get(key).intValue()) {
				return false;
			}
		}
		return true;
	}

	public Map<String, Integer> getFrequencies() {
		return Collections.unmodifiableMap(d_frequencies);
	}
}
