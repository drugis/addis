/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.list.ObservableList;
import com.jgoodies.binding.value.AbstractValueModel;

public class DefaultSelectableStudyListPresentation implements SelectableStudyListPresentation {

	private HashMap<Study, ModifiableHolder<Boolean>> d_selectedStudiesMap;
	
	private ChangeListener d_listener = new ChangeListener();
	private ListHolder<Study> d_selectedStudiesList = new DefaultListHolder<Study>(new ArrayList<Study>());

	private final StudyListPresentation d_source;

	public DefaultSelectableStudyListPresentation(StudyListPresentation source) {
		d_source = source;
		
		getAvailableStudies().addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent e) {
				updateSelectedStudies();
			}
			public void intervalAdded(ListDataEvent e) {
				updateSelectedStudies();
			}
			public void contentsChanged(ListDataEvent e) {
				updateSelectedStudies();
			}
		});
		d_selectedStudiesMap = new HashMap<Study, ModifiableHolder<Boolean>>();
		updateSelectedStudies();
	}
	

	private void updateSelectedStudies() {
		for (Study s : getAvailableStudies()) {
			if (!d_selectedStudiesMap.containsKey(s)) {
				d_selectedStudiesMap.put(s, getBooleanHolder());
			}
		}
		
		Set<Study> leftStudies = new HashSet<Study>(d_selectedStudiesMap.keySet());
		leftStudies.removeAll(getAvailableStudies());
		for (Study s : leftStudies) {
			d_selectedStudiesMap.remove(s);
		}
		d_selectedStudiesList.setValue(createSelectedStudies());
	}
	
	public ListHolder<Study> getSelectedStudiesModel() {
		return d_selectedStudiesList;
	}

	private ModifiableHolder<Boolean> getBooleanHolder() {
		ModifiableHolder<Boolean> holder = new ModifiableHolder<Boolean>();
		holder.setValue(true);
		holder.addPropertyChangeListener(d_listener);
		return holder;
	}

	private List<Study> createSelectedStudies() {
		List<Study> selectedStudyList = new ArrayList<Study>();
		
		for (Study s : getAvailableStudies()) {
			if (d_selectedStudiesMap.get(s).getValue() ) {
				selectedStudyList.add(s);
			}	
		}
		
		return selectedStudyList;
	}
	
	/**
	 * 
	 * @see org.drugis.addis.presentation.SelectableStudyListPresentation#getSelectedStudyBooleanModel(org.drugis.addis.entities.Study)
	 * @throws IllegalArgumentException if !getIncludedStudies().getValue().contains(s)
	 */
	public ModifiableHolder<Boolean> getSelectedStudyBooleanModel(Study s) throws IllegalArgumentException{
		if (!getAvailableStudies().contains(s)) {
			throw new IllegalArgumentException();
		}
		return d_selectedStudiesMap.get(s);
	}

	private class ChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			updateSelectedStudies();
		}		
	}

	@Override
	public ObservableList<Study> getAvailableStudies() {
		return d_source.getIncludedStudies();
	}


	@Override
	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return d_source.getCharacteristicVisibleModel(c);
	}


	@Override
	public StudyListPresentation getSource() {
		return d_source;
	}
}
