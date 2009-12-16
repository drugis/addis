package org.drugis.addis.entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.drugis.addis.entities.Endpoint.Type;

public class FrequencyMeasurement extends BasicMeasurement {

	private static final long serialVersionUID = -6601562604420073113L;
	private CategoricalVariable d_cv;
	
	private Map<String, Integer> d_frequencies = new HashMap<String, Integer>();

	public FrequencyMeasurement(CategoricalVariable cv) {
		super(0);
		d_cv = cv;
		for (String cat : d_cv.getCategories()) {
			d_frequencies.put(cat, 0);
		}
	}
	
	private void updateSampleSize() {
		int size = 0;
		for (String cat : d_cv.getCategories()) {
			size += d_frequencies.get(cat);
		}
		setSampleSize(size);
	}

	public void setFrequency(String category, Integer freq) throws IllegalArgumentException {
		checkCategory(category);
		d_frequencies.put(category, freq);
		updateSampleSize();
	}
	
	public Integer getFrequency(String category) throws IllegalArgumentException {
		checkCategory(category);
		return d_frequencies.get(category);
	}

	private void checkCategory(String category) {
		if (!d_frequencies.containsKey(category)) {
			throw new IllegalArgumentException("illegal category");
		}
	}
	
	public CategoricalVariable getCategoricalVariable() {
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
		
	public boolean isOfType(Type type) {
		return false;
	}

	@Override
	public Set<Entity> getDependencies() {
		return Collections.<Entity>singleton(d_cv);
	}
	
	@Override
	public String toString() {
		String ret = new String();
		for (Entry<String, Integer> entry : d_frequencies.entrySet()) {
			if (!ret.equals("")) {
				ret += " / ";
			}
			ret += entry.getKey() + " = " + entry.getValue();
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
			return d_frequencies.equals(m.d_frequencies);
		}
		return false;
	}

}
