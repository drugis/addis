/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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