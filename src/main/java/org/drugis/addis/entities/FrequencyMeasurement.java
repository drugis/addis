package org.drugis.addis.entities;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drugis.addis.entities.Endpoint.Type;

import com.jgoodies.binding.value.AbstractValueModel;

public class FrequencyMeasurement extends BasicMeasurement {

	private static final long serialVersionUID = -6601562604420073113L;
	private CategoricalVariable d_cv;
	
	private Map<String, AbstractValueModel> d_frequencies = new HashMap<String, AbstractValueModel>();

	private void writeObject(ObjectOutputStream oos) throws IOException {
		// FIXME: Holders should be on presentation layer due to serialization issues
		System.err.println("serialize freqmeas");
		oos.defaultWriteObject();
		System.err.println("done");
	}
	
	public class TransientListenerValueHolder extends AbstractValueModel {
		private static final long serialVersionUID = -8344718537084761274L;
		Object d_val;
		
		public TransientListenerValueHolder(Object val) {
			d_val = val;
		}
		
		private void writeObject(ObjectOutputStream oos) throws IOException {
			System.err.println("Remove listeners");
			removeListeners();
			oos.defaultWriteObject();
		}

		private void removeListeners() {
			for (PropertyChangeListener l : getPropertyChangeListeners()) {
				removePropertyChangeListener(l);
			}
		}

		public Object getValue() {
			return d_val;
		}

		public void setValue(Object newValue) {
			Object old = d_val;
			d_val = newValue;
			fireValueChange(old, d_val);
		}
	}

	public FrequencyMeasurement(CategoricalVariable cv) {
		super(0);
		d_cv = cv;
		for (String cat : d_cv.getCategories()) {
			d_frequencies.put(cat, new TransientListenerValueHolder(new Integer(0)));
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
	
	private boolean frequenciesEqual(Map<String, AbstractValueModel> frequencies,
			Map<String, AbstractValueModel> frequencies2) {
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
