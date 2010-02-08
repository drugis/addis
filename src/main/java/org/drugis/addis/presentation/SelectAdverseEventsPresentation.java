package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.AdverseDrugEvent;
import org.drugis.addis.gui.Main;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class SelectAdverseEventsPresentation extends Model
implements SelectFromFiniteListPresentationModel<AdverseDrugEvent> {
	private List<AbstractHolder<AdverseDrugEvent>> d_slots;
	private ListHolder<AdverseDrugEvent> d_options;
	private AddSlotsEnabledModel d_addSlotsEnabled;
	private InputCompleteModel d_inputCompleteModel;
	private Main d_main;
	
	public static final String PROPERTY_NSLOTS = "nSlots";
	
	private class Slot<T> extends AbstractHolder<T> {
		@Override
		protected void cascade() {
			for (AbstractHolder<AdverseDrugEvent> s : d_slots) {
				if (s != this && EqualsUtil.equal(s.getValue(), getValue())) {
					s.setValue(null);
				}
			}
		}

		@Override
		protected void checkArgument(Object newValue) {
		}
	}
	
	private class InputCompleteModel extends AbstractValueModel implements PropertyChangeListener {
		private Boolean d_oldValue;
		
		public InputCompleteModel() {
			for (AbstractHolder<AdverseDrugEvent> s : d_slots) {
				s.addValueChangeListener(this);
			}
			d_oldValue = getValue();
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			evaluate();
		}

		private void evaluate() {
			if (getValue() != d_oldValue) {
				fireValueChange(d_oldValue, getValue());
				d_oldValue = getValue();
			}
		}

		public Boolean getValue() {
			boolean r = true;
			for (AbstractHolder<AdverseDrugEvent> s: d_slots) {
				if (s.getValue() == null) {
					r = false;
					break;
				}
			}
			return r;
		}
		
		public void addSlot(AbstractHolder<AdverseDrugEvent> s) {
			s.addValueChangeListener(this);
			evaluate();
		}
		
		public void removeSlot(AbstractHolder<AdverseDrugEvent> s) {
			s.removeValueChangeListener(this);
			evaluate();
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("InputCompleteModel is read-only");
		}
	}
	
	/*
	private class AddSlotsEnabledModel extends AbstractValueModel implements PropertyChangeListener {
		public AddSlotsEnabledModel() {
			d_options.addValueChangeListener(this);
			SelectAdverseEventsPresentation.this.addPropertyChangeListener(this);
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

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == d_options) {
				boolean oldVal = addSlotsEnabled(d_slots.size(), ((List)evt.getOldValue()).size());
				boolean newVal = addSlotsEnabled(d_slots.size(), ((List)evt.getNewValue()).size());
				fireValueChange(oldVal, newVal);
			} else if (evt.getSource() == SelectAdverseEventsPresentation.this) {
				boolean oldVal = addSlotsEnabled((Integer)evt.getOldValue(), d_options.getValue().size());
				boolean newVal = addSlotsEnabled((Integer)evt.getNewValue(), d_options.getValue().size());
				fireValueChange(oldVal, newVal);
			}
		}
	} */
	
	private class AddSlotsEnabledModel extends AbstractValueModel {
		public Object getValue() {
			return true;
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("AddSlotsEnabledModel is read-only");
		}
	}
	
	public SelectAdverseEventsPresentation(ListHolder<AdverseDrugEvent> options, Main main) {
		d_slots = new ArrayList<AbstractHolder<AdverseDrugEvent>>();
		d_options = options;
		d_addSlotsEnabled = new AddSlotsEnabledModel();
		d_inputCompleteModel = new InputCompleteModel();
		d_main = main;
	}

	public ListHolder<AdverseDrugEvent> getOptions() {
		return d_options;
	}
	
	public void addSlot() {
		Slot<AdverseDrugEvent> s = new Slot<AdverseDrugEvent>();
		d_slots.add(s);
		firePropertyChange(PROPERTY_NSLOTS, d_slots.size() - 1, d_slots.size());
		d_inputCompleteModel.addSlot(s);
	}

	public int countSlots() {
		return d_slots.size();
	}
	
	public void removeSlot(int idx) {
		AbstractHolder<AdverseDrugEvent> s = d_slots.get(idx);
		d_slots.remove(idx);
		firePropertyChange(PROPERTY_NSLOTS, d_slots.size() + 1, d_slots.size());
		d_inputCompleteModel.removeSlot(s);
	}

	public AbstractHolder<AdverseDrugEvent> getSlot(int idx) {
		return d_slots.get(idx);
	}
	
	public ValueModel getAddSlotsEnabledModel() {
		return d_addSlotsEnabled;
	}

	public ValueModel getInputCompleteModel() {
		return d_inputCompleteModel;
	}

	public boolean hasAddOptionDialog() {
		return true;
	}
	
	public void showAddOptionDialog(int idx) {
		d_main.showAddAdeDialog(getSlot(idx));
	}

	public String getTypeName() {
		return "Adverse Event";
	}

	public String getTitle() {
		return "Select Adverse Events";
	}
	
	public String getDescription() {
		return "Please select the appropriate adverse events";
	}

	public List<AbstractHolder<AdverseDrugEvent>> getSlots() { 
		return Collections.unmodifiableList(d_slots);
	}
}
