package org.drugis.addis.presentation;

import org.drugis.addis.entities.Variable;

import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

public class SelectPopulationCharsPresentation implements SelectFromFiniteListPresentationModel<Variable> {

	public SelectPopulationCharsPresentation(
			ListHolder<Variable> populationCharsListHolder) {
		// TODO Auto-generated constructor stub
	}

	public void addSlot() {
		// TODO Auto-generated method stub
		
	}

	public int countSlots() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ValueModel getAddSlotsEnabledModel() {
		return new ValueHolder(true);
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueModel getInputCompleteModel() {
		return new ValueHolder(true);
	}

	public ListHolder<Variable> getOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	public AbstractHolder<Variable> getSlot(int idx) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		return "Add Population Characteristics";
	}

	public String getTypeName() {
		return null;
	}

	public boolean hasAddOptionDialog() {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeSlot(int idx) {
		// TODO Auto-generated method stub
		
	}

	public void showAddOptionDialog(int idx) {
		// TODO Auto-generated method stub
		
	}

}
