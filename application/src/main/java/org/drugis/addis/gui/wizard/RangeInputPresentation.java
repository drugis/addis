package org.drugis.addis.gui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.math3.util.Pair;
import org.drugis.addis.entities.AbstractDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.treatment.DecisionTreeNode;
import org.drugis.addis.entities.treatment.RangeNode;
import org.drugis.addis.gui.knowledge.DosedDrugTreatmentKnowledge.CategorySpecifiers;
import org.drugis.addis.presentation.DosedDrugTreatmentPresentation;
import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.addis.presentation.ValueHolder;

import com.jgoodies.binding.list.ObservableList;

public class RangeInputPresentation {
	private final Pair<Class<? extends AbstractDose>, String> d_beanProperty;
	private final Map<DecisionTreeNode, Object> d_selections = new HashMap<DecisionTreeNode, Object>();
	private final Family d_family;
	private final DosedDrugTreatmentPresentation d_pm;
	private final CategorySpecifiers d_nextProperty;
	private final ValueHolder<Boolean> d_considerNext = new ModifiableHolder<Boolean>(false);

	public RangeInputPresentation(
			DosedDrugTreatmentPresentation presentationModel,
			Pair<Class<? extends AbstractDose>, String> beanProperty,
			String nextPropertyName, Family family) {
		d_pm = presentationModel;
		d_beanProperty = beanProperty;
		d_nextProperty = getNextProperty(nextPropertyName);
		d_family = family;
	}

	public void determineSelections() {
		for (DecisionTreeNode node : getChildren()) {
			ObservableList<DecisionTreeNode> children = d_pm.getChildNodes(node);
			if (children.size() == 1 && !(children.get(0) instanceof RangeNode)) {
				d_selections.put(node, children.get(0));
			} else {
				d_selections.put(node, d_nextProperty);
			}
		}
		d_family.getChildren().addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				updateValueHolder();
				
			}
			
			public void intervalAdded(ListDataEvent e) {
				updateValueHolder();
				
			}
			
			public void contentsChanged(ListDataEvent e) {
				
			}
		});
		updateValueHolder();
	}

	public DecisionTreeNode getParent() {
		return d_family.parent;
	}

	public ObservableList<DecisionTreeNode> getChildren() {
		return d_family.getChildren();
	}

	public Family getFamily() {
		return d_family;
	}

	RangeNode getChild(final int index) {
		return (RangeNode)getChildren().get(index);
	}

	public Object getSelected(DecisionTreeNode node) {
		return d_selections.get(node);
	}

	public void setSelected(RangeNode rangeNode, Object selected) {
		if(getBeanProperty() == null) { 
			getParentPresentation().setKnownDoses(rangeNode, selected);
		} else {
			getParentPresentation().setSelected(rangeNode, selected);
		}
		d_selections.put(rangeNode, selected);
		updateValueHolder();
	}

	private void updateValueHolder() {
		System.out.println("next property is " + d_nextProperty);

		if (d_nextProperty == null) {
			return;
		}
		
		boolean value = false;
		for(Object selection : d_selections.values()) { 
			if(selection.equals(d_nextProperty)) {
				value = true;
			}
		}
		System.out.println("setting to " + value);
		d_considerNext.setValue(value);
	}
	
	private static CategorySpecifiers getNextProperty(String nextPropertyName) {
		if (FlexibleDose.PROPERTY_MIN_DOSE.equals(nextPropertyName)) { 
			return CategorySpecifiers.FLEXIBLE_CONSIDER_LOWER_2;
		} else if(FlexibleDose.PROPERTY_MAX_DOSE.equals(nextPropertyName)) {
			return CategorySpecifiers.FLEXIBLE_CONSIDER_UPPER_2;
		}
		return null;
	}

	public CategorySpecifiers[] getExtraOptions() {
		if (d_nextProperty != null) {
			return new CategorySpecifiers[] { d_nextProperty };
		}
		return new CategorySpecifiers[] {};
	}

	public Pair<Class<? extends AbstractDose>, String> getBeanProperty() {
		return d_beanProperty;
	}

	public DosedDrugTreatmentPresentation getParentPresentation() {
		return d_pm;
	}

	public ValueHolder<Boolean> getConsiderNext() {
		d_considerNext.addValueChangeListener(new PropertyChangeListener() {
			
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("Changed from " + evt.getOldValue() + " to " + evt.getNewValue());
			}
		});
		return d_considerNext;
	}
}