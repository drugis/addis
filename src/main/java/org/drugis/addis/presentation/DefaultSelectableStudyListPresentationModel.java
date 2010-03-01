package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.Study;

public class DefaultSelectableStudyListPresentationModel extends DefaultStudyListPresentationModel implements SelectableStudyListPresentationModel{

	private HashMap<Study, ModifiableHolder<Boolean>> d_selectedStudiesMap;
	
	private ChangeListener d_listener = new ChangeListener();
	private ListHolder<Study> d_selectedStudiesList = new DefaultListHolder<Study>(new ArrayList<Study>());

	public DefaultSelectableStudyListPresentationModel(ListHolder<Study> list) {
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
	 * @see org.drugis.addis.presentation.SelectableStudyListPresentationModel#getSelectedStudyBooleanModel(org.drugis.addis.entities.Study)
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
