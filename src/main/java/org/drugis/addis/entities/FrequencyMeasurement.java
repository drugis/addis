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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

public class FrequencyMeasurement extends BasicMeasurement {

	private CategoricalPopulationCharacteristic d_cv;
	
	@SuppressWarnings("serial")
	private static class FrequencyMap extends HashMap<String, Integer> {
		
	}

	private static class FrequencyEntry {
		public String key;
		public Integer value;

		public FrequencyEntry() {}
		
		public FrequencyEntry(Entry<String, Integer> entry) {
			key = entry.getKey();
			value = entry.getValue();
		}
	}
	
	private FrequencyMap d_frequencies = new FrequencyMap();
	 
	public static final String PROPERTY_FREQUENCIES = "frequencies";
	
	private FrequencyMeasurement() {
		super(0);
	}
	
	public FrequencyMeasurement(CategoricalPopulationCharacteristic cv) {
		super(0);
		d_cv = cv;
		for (String cat : d_cv.getCategories()) {
			getFrequencies().put(cat, new Integer(0));
		}
	}
	
	private void updateSampleSize() {
		int size = 0;
		for (String cat : d_cv.getCategories()) {
			size += getFrequencies().get(cat).intValue();
		}
		setSampleSize(size);
	}

	public void setFrequency(String category, int freq) throws IllegalArgumentException {
		checkCategory(category);
		Map<String, Integer> oldfreq = new HashMap<String,Integer>(getFrequencies());
		getFrequencies().put(category, freq);
		updateSampleSize();
		firePropertyChange(PROPERTY_FREQUENCIES, oldfreq, getFrequencies());
	}
	
	public int getFrequency(String category) throws IllegalArgumentException {
		checkCategory(category);
		return getFrequencies().get(category).intValue();
	}

	private void checkCategory(String category) {
		if (!getFrequencies().containsKey(category)) {
			throw new IllegalArgumentException("illegal category");
		}
	}
	
	public CategoricalPopulationCharacteristic getCategoricalVariable() {
		return d_cv;
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
			ret += cat + " = " + getFrequencies().get(cat).intValue();
		}
		return ret;
	}
	
	@Override
	public FrequencyMeasurement clone() {
		FrequencyMeasurement m = new FrequencyMeasurement(getCategoricalVariable());
		for (String cat : d_cv.getCategories()) {
			m.setFrequency(cat, getFrequency(cat));
		}
		return m;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof FrequencyMeasurement) {
			FrequencyMeasurement m = (FrequencyMeasurement) o;
			if (!m.getCategoricalVariable().equals(getCategoricalVariable())) {
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
			if (frequencies.get(key).intValue() != frequencies2.get(key).intValue()) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode() {
		return d_frequencies.hashCode();
	}

	public Map<String, Integer> getFrequencies() {
		return d_frequencies;
	}

	protected static final XMLFormat<FrequencyMeasurement> XML = new XMLFormat<FrequencyMeasurement>(FrequencyMeasurement.class) {
		@Override
		public FrequencyMeasurement newInstance(Class<FrequencyMeasurement> arg0, XMLFormat.InputElement arg1) 
		throws XMLStreamException {
			return new FrequencyMeasurement();
		}
		
		@Override
		public void read(javolution.xml.XMLFormat.InputElement ie,
				FrequencyMeasurement fm) throws XMLStreamException {
			fm.d_cv = ie.get("variable", CategoricalPopulationCharacteristic.class);
			fm.d_frequencies = ie.get("frequencies", FrequencyMap.class);
			fm.updateSampleSize();
		}

		@Override
		public void write(FrequencyMeasurement fm,
				javolution.xml.XMLFormat.OutputElement oe)
				throws XMLStreamException {
			oe.add(fm.d_cv, "variable", CategoricalPopulationCharacteristic.class);
			oe.add(fm.d_frequencies, "frequencies", FrequencyMap.class);
		}
	};
	
	
	@SuppressWarnings("unused")
	private static final XMLFormat<FrequencyMap> armMapXML = new XMLFormat<FrequencyMap>(FrequencyMap.class) {
		@Override
		public FrequencyMap newInstance(Class<FrequencyMap> cls, XMLFormat.InputElement xml) {
			return new FrequencyMap();
		}
		
		@Override
		public boolean isReferenceable() {
			return false;
		}
		
		@Override
		public void read(javolution.xml.XMLFormat.InputElement ie,
				FrequencyMap map) throws XMLStreamException {
			while (ie.hasNext()) {
				FrequencyEntry entry = ie.get("frequency", FrequencyEntry.class);
				map.put(entry.key, entry.value);
			}
		}

		@Override
		public void write(FrequencyMap map,
				javolution.xml.XMLFormat.OutputElement oe)
				throws XMLStreamException {
			for (Entry<String, Integer> e : map.entrySet()) {
				oe.add(new FrequencyEntry(e), "frequency", FrequencyEntry.class);// write entries
			}
		}
	};
	
	@SuppressWarnings("unused")
	private static final XMLFormat<FrequencyEntry> entryXML = new XMLFormat<FrequencyEntry>(FrequencyEntry.class) {
		@Override
		public FrequencyEntry newInstance(Class<FrequencyEntry> cls, XMLFormat.InputElement xml) {
			return new FrequencyEntry();
		}
		
		@Override
		public boolean isReferenceable() {
			return false;
		}
		
		@Override
		public void read(javolution.xml.XMLFormat.InputElement ie,
				FrequencyEntry entry) throws XMLStreamException {
			entry.key = ie.getAttribute("category").toString();
			entry.value = ie.getAttribute("count", 0);
		}

		@Override
		public void write(FrequencyEntry entry,
				javolution.xml.XMLFormat.OutputElement oe)
				throws XMLStreamException {
			oe.setAttribute("category", entry.key);
			oe.setAttribute("count", entry.value);
		}
	};
}
