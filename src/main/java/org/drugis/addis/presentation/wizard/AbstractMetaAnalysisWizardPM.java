package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.TypedHolder;

import com.jgoodies.binding.value.ValueModel;

public abstract class AbstractMetaAnalysisWizardPM {

	protected Domain d_domain;
	protected PresentationModelFactory d_pmm;
	protected TypedHolder<Indication> d_indicationHolder;
	protected TypedHolder<OutcomeMeasure> d_endpointHolder;
	protected OutcomeListHolder d_outcomeListHolder;
	protected DrugListHolder d_drugListHolder;	

	public AbstractMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmm) {
		d_domain = d;
		d_pmm = pmm;
		
		d_indicationHolder = new TypedHolder<Indication>();
		d_endpointHolder = new TypedHolder<OutcomeMeasure>();
		d_indicationHolder.addPropertyChangeListener(new SetEmptyListener(d_endpointHolder));
		d_outcomeListHolder = new OutcomeListHolder(d_indicationHolder, d_domain);		
		d_drugListHolder = new DrugListHolder();		
	}
	
	public ValueModel getIndicationModel() {
		return d_indicationHolder; 
	}
	
	protected List<Study> getStudiesEndpointAndIndication() {
		List<Study> studies = new ArrayList<Study>(d_domain.getStudies(d_endpointHolder.getValue()).getValue());
		studies.retainAll(d_domain.getStudies(d_indicationHolder.getValue()).getValue());
		return studies;
	}	
	
	public ValueModel getEndpointModel() {
		return d_endpointHolder;
	}

	public AbstractListHolder<Drug> getDrugListModel() {
		return d_drugListHolder;
	}

	@SuppressWarnings("serial")
	protected class DrugListHolder extends AbstractListHolder<Drug> implements PropertyChangeListener {
		public DrugListHolder() {
			d_endpointHolder.addValueChangeListener(this);
		}
		
		@Override
		public List<Drug> getValue() {
			SortedSet<Drug> drugs = new TreeSet<Drug>();
			if (d_indicationHolder.getValue() != null && d_endpointHolder.getValue() != null) {
				List<Study> studies = getStudiesEndpointAndIndication();
				for (Study s : studies) {
					drugs.addAll(s.getDrugs());
				}
			}			
			return new ArrayList<Drug>(drugs);
		}
		
		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange(null, getValue());
		}
	}		
}
