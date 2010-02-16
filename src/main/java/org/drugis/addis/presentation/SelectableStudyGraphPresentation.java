package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;

@SuppressWarnings("serial")
public class SelectableStudyGraphPresentation extends StudyGraphPresentation {
	
	private ListHolder<Drug> d_selectedDrugs;

	public SelectableStudyGraphPresentation(ValueHolder<Indication> indication,
			ValueHolder<OutcomeMeasure> outcome, ListHolder<Drug> drugs,
			Domain domain) {
		super(indication, outcome, drugs, domain);
		
		d_selectedDrugs = new DefaultListHolder<Drug>(d_drugs.getValue());
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
