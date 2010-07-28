/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import org.drugis.addis.entities.Study;

public class DefaultSelectableStudyListPresentation extends DefaultStudyListPresentation implements SelectableStudyListPresentation{

	private HashMap<Study, ModifiableHolder<Boolean>> d_selectedStudiesMap;
	
	private ChangeListener d_listener = new ChangeListener();
	private ListHolder<Study> d_selectedStudiesList = new DefaultListHolder<Study>(new ArrayList<Study>());

	public DefaultSelectableStudyListPresentation(ListHolder<Study> list) {
		super(list);
		
		getIncludedStudies().addValueChangeListener(d_listener);
		d_selectedStudiesMap = new HashMap<Study, ModifiableHolder<Boolean>>();
		updateSelectedStudies();
	}
	

	private void updateSelectedStudies() {
		List<Study> studyList = getIncludedStudies().getValue();
		if (studyList != null) {
			for (Study s : studyList) {
				if (!d_selectedStudiesMap.containsKey(s)) {
					d_selectedStudiesMap.put(s, getBooleanHolder());
				}
			}
			
			Set<Study> leftStudies = new HashSet<Study>(d_selectedStudiesMap.keySet());
			leftStudies.removeAll(studyList);
			for (Study s : leftStudies) {
				d_selectedStudiesMap.remove(s);
			}
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
		
		for (Study s : getIncludedStudies().getValue()) {
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
		if (!getIncludedStudies().getValue().contains(s)) {
			throw new IllegalArgumentException();
		}
		return d_selectedStudiesMap.get(s);
	}

	private class ChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			updateSelectedStudies();
		}		
	}
}
