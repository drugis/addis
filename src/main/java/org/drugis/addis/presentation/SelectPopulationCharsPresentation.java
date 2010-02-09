package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.drugis.addis.entities.Variable;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class SelectPopulationCharsPresentation
extends SelectFromFiniteListPresentationImpl<Variable> {
	public SelectPopulationCharsPresentation(ListHolder<Variable> options) {
		super(options, "Population Characteristic", "Select Population Characteristics",
			"Please select the appropriate population characteristics.");
		d_addSlotsEnabled = new AddSlotsEnabledModel();
	}
	
	public class AddSlotsEnabledModel extends AbstractValueModel implements PropertyChangeListener {
		public AddSlotsEnabledModel() {
			d_options.addValueChangeListener(this);
			SelectPopulationCharsPresentation.this.addPropertyChangeListener(this);
		}
		
		public Object getValue() {
			return addSlotsEnabled();
		}

		private boolean addSlotsEnabled() {
			return addSlotsEnabled(d_slots.size(), d_options.getValue().size());
		}
		
		private boolean addSlotsEnabled(int slots, int options) {
			return slots < options;
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("AddSlotsEnabledModel is read-only");
		}

		@SuppressWarnings("unchecked")
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == d_options) {
				boolean oldVal = addSlotsEnabled(d_slots.size(), ((List)evt.getOldValue()).size());
				boolean newVal = addSlotsEnabled(d_slots.size(), ((List)evt.getNewValue()).size());
				fireValueChange(oldVal, newVal);
			} else if (evt.getSource() == SelectPopulationCharsPresentation.this) {
				boolean oldVal = addSlotsEnabled((Integer)evt.getOldValue(), d_options.getValue().size());
				boolean newVal = addSlotsEnabled((Integer)evt.getNewValue(), d_options.getValue().size());
				fireValueChange(oldVal, newVal);
			}
		}
	}
}
