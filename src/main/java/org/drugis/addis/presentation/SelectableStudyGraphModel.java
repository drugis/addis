package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;

@SuppressWarnings("serial")
public class SelectableStudyGraphModel extends StudyGraphModel {
	
	private ListHolder<Drug> d_selectedDrugs;

	public SelectableStudyGraphModel(ValueHolder<Indication> indication,
			ValueHolder<OutcomeMeasure> outcome, ListHolder<Drug> drugs,
			Domain domain) {
		super(indication, outcome, drugs, domain);
		
		d_selectedDrugs = new DefaultListHolder<Drug>(new ArrayList<Drug>(d_drugs.getValue()));
		d_drugs.addValueChangeListener(new DrugsChangedListener());		
	}
	
	public ListHolder<Drug> getSelectedDrugsModel() {
		return d_selectedDrugs;
	}

	private class DrugsChangedListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent evt) {
			d_selectedDrugs.setValue(d_drugs.getValue());
		}		
	}
}
