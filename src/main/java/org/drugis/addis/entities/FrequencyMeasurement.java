package org.drugis.addis.entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drugis.addis.entities.Endpoint.Type;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

public class FrequencyMeasurement extends BasicMeasurement {

	private static final long serialVersionUID = -6601562604420073113L;
	private CategoricalVariable d_cv;
	
	private Map<String, ValueHolder> d_frequencies = new HashMap<String, ValueHolder>();

	public FrequencyMeasurement(CategoricalVariable cv) {
		super(0);
		d_cv = cv;
		for (String cat : d_cv.getCategories()) {
			d_frequencies.put(cat, new ValueHolder(0));
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
		d_frequencies.get(category).setValue(freq);
		updateSampleSize();
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
	
	private boolean frequenciesEqual(Map<String, ValueHolder> frequencies,
			Map<String, ValueHolder> frequencies2) {
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

	public AbstractValueModel getFrequencyModel(String category) {
		return d_frequencies.get(category);
	}
}
