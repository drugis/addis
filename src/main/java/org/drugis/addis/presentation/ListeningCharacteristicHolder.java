/**
 * 
 */
package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;

public abstract class ListeningCharacteristicHolder extends StudyCharacteristicHolder implements PropertyChangeListener, ListDataListener {
	private static final long serialVersionUID = -4456864289598668715L;

	public ListeningCharacteristicHolder(Study study, Characteristic characteristic) {
		super(study, characteristic);
		study.getArms().addListDataListener(this);
		for (Arm p : study.getArms()) {
			p.addPropertyChangeListener(this);
		}
	}
	
	protected abstract Object getNewValue();
	
	@Override
	public Object getValue() {
		return getNewValue();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		update();
	}

	private void update() {
		firePropertyChange("value", null, getNewValue());
	}
	
	private void updateListeners() {
		for (Arm p : d_study.getArms()) {
			p.removePropertyChangeListener(this);
			p.addPropertyChangeListener(this);
		}
		update();
	}
	
	public void intervalAdded(ListDataEvent e) {
		updateListeners();
	}

	public void intervalRemoved(ListDataEvent e) {
		updateListeners();			
	}
	
	public void contentsChanged(ListDataEvent e) {
		updateListeners();			
	}
}