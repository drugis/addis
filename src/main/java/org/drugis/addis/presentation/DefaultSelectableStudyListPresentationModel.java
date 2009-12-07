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

	private HashMap<Study, AbstractHolder<Boolean>> d_selectedStudies;
	
	private List<PropertyChangeListener> d_selectionListeners = new ArrayList<PropertyChangeListener>();

	public DefaultSelectableStudyListPresentationModel(ListHolder<Study> list) {
		super(list);
		
		getIncludedStudies().addValueChangeListener(new StudyListListener());
		
		d_selectedStudies = new HashMap<Study, AbstractHolder<Boolean>>();
		updateSelectedStudiesMap();
	}
	
	public void addSelectionListener(PropertyChangeListener l) {
		d_selectionListeners.add(l);
		
		for (AbstractHolder<Boolean> h : d_selectedStudies.values()) {
			h.addPropertyChangeListener(l);
		}
	}

	private void updateSelectedStudiesMap() {
		List<Study> studyList = getIncludedStudies().getValue();
		if (studyList != null) {
			for (Study s : studyList) {
				if (!d_selectedStudies.containsKey(s)) {
					d_selectedStudies.put(s, getBooleanHolder());
				}
			}
			
			Set<Study> leftStudies = new HashSet<Study>(d_selectedStudies.keySet());
			leftStudies.removeAll(studyList);
			for (Study s : leftStudies) {
				d_selectedStudies.remove(s);
			}
		}
	}

	private BooleanHolder getBooleanHolder() {
		BooleanHolder holder = new BooleanHolder();
		for (PropertyChangeListener l : d_selectionListeners) {
			holder.addPropertyChangeListener(l);
		}
		return holder;
	}

	public List<Study> getSelectedStudies() {
		List<Study> selectedStudyList = new ArrayList<Study>();
		
		for (Study s : d_selectedStudies.keySet()) {
			if (d_selectedStudies.get(s).getValue() ) {
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
	public AbstractHolder<Boolean> getSelectedStudyBooleanModel(Study s) throws IllegalArgumentException{
		if (!getIncludedStudies().getValue().contains(s)) {
			throw new IllegalArgumentException();
		}
		return d_selectedStudies.get(s);
	}

	private class StudyListListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent arg0) {
			updateSelectedStudiesMap();
		}		
	}
}
