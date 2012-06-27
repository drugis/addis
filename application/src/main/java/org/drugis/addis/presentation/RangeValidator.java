package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.presentation.ValueHolder;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public final class RangeValidator extends AbstractValueModel implements ValueHolder<Boolean> {
	private static final String PROPERTY_VALID = "value";
	private final ValueHolder<Double> d_range;
	private final double d_maximum;
	private final double d_minimum;
	private boolean d_valid = false;
	public RangeValidator(ValueHolder<Double> range, double minimum, double maximum) {
		d_range = range;
		d_maximum = maximum;
		d_minimum = minimum;
		d_range.addValueChangeListener(new PropertyChangeListener() {		
			public void propertyChange(PropertyChangeEvent evt) {
				validate();
			}
		});
		validate();
	}
	
	@Override
	public Boolean getValue() {
		return d_valid;
	}

	@Override
	public void setValue(Object newValue) {
		throw new UnsupportedOperationException("Cannot set value on validators");
	}
	
	public void validate() { 
		boolean oldValue = d_valid;
		d_valid =  d_range.getValue() <= d_maximum && d_range.getValue() >= d_minimum;
		firePropertyChange(PROPERTY_VALID, oldValue, d_valid);
	}
}