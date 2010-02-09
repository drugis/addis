package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.gui.Main;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
abstract public class SelectFromFiniteListPresentationImpl<T> extends Model
implements SelectFromFiniteListPresentationModel<T> {

	protected List<AbstractHolder<T>> d_slots;
	protected ListHolder<T> d_options;
	protected ValueModel d_addSlotsEnabled;
	protected InputCompleteModel d_inputCompleteModel;
	protected Main d_main;
	public static final String PROPERTY_NSLOTS = "nSlots";
	private String d_typeName;
	private String d_title;
	private String d_description;
	
	public SelectFromFiniteListPresentationImpl(ListHolder<T> options,
			String typeName, String title, String description) {
		d_typeName = typeName;
		d_title = title;
		d_description = description;
		d_slots = new ArrayList<AbstractHolder<T>>();
		d_options = options;
		d_addSlotsEnabled = new AddSlotsAlwaysEnabledModel();
		d_inputCompleteModel = new InputCompleteModel();
	}

	public ListHolder<T> getOptions() {
		return d_options;
	}

	public void addSlot() {
		Slot<T> s = new Slot<T>(d_slots);
		d_slots.add(s);
		firePropertyChange(PROPERTY_NSLOTS, d_slots.size() - 1, d_slots.size());
		d_inputCompleteModel.addSlot(s);
	}

	public int countSlots() {
		return d_slots.size();
	}

	public void removeSlot(int idx) {
		AbstractHolder<T> s = d_slots.get(idx);
		d_slots.remove(idx);
		firePropertyChange(PROPERTY_NSLOTS, d_slots.size() + 1, d_slots.size());
		d_inputCompleteModel.removeSlot(s);
	}

	public AbstractHolder<T> getSlot(int idx) {
		return d_slots.get(idx);
	}

	public ValueModel getAddSlotsEnabledModel() {
		return d_addSlotsEnabled;
	}

	public ValueModel getInputCompleteModel() {
		return d_inputCompleteModel;
	}

	public boolean hasAddOptionDialog() {
		return false;
	}
	
	public void showAddOptionDialog(int idx) {
		throw new RuntimeException("AddOptionDialog not implemented");
	}

	public String getTypeName() {
		return d_typeName;
	}

	public String getTitle() {
		return d_title;
	}

	public String getDescription() {
		return d_description;
	}

	public List<AbstractHolder<T>> getSlots() { 
		return Collections.unmodifiableList(d_slots);
	}

	class Slot<E> extends AbstractHolder<E> {
		private List<AbstractHolder<E>> d_slots;
		public Slot(List<AbstractHolder<E>> slots) {
			d_slots = slots;
		}
		@Override
		protected void cascade() {
			// Make sure each option is selected only once
			for (AbstractHolder<E> s : d_slots) {
				if (s != this && EqualsUtil.equal(s.getValue(), getValue())) {
					s.setValue(null);
				}
			}
		}

		@Override
		protected void checkArgument(Object newValue) {
		}
	}
	
	class InputCompleteModel extends AbstractValueModel implements PropertyChangeListener {
		private Boolean d_oldValue;
		
		public InputCompleteModel() {
			for (AbstractHolder<T> s : d_slots) {
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
			for (AbstractHolder<T> s: d_slots) {
				if (s.getValue() == null) {
					r = false;
					break;
				}
			}
			return r;
		}
		
		public void addSlot(AbstractHolder<T> s) {
			s.addValueChangeListener(this);
			evaluate();
		}
		
		public void removeSlot(AbstractHolder<T> s) {
			s.removeValueChangeListener(this);
			evaluate();
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("InputCompleteModel is read-only");
		}
	}
	
	public class AddSlotsAlwaysEnabledModel extends AbstractValueModel {
		public Object getValue() {
			return true;
		}

		public void setValue(Object newValue) {
			throw new RuntimeException("AddSlotsEnabledModel is read-only");
		}
	}
}
