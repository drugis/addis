package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.SortedSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentation {
	
	@SuppressWarnings("serial")
	private class IndicationHolder extends AbstractValueModel {

		private Indication d_indication = null;
		
		public Object getValue() {
			return d_indication;
		}

		public void setValue(Object newValue) {
			if (!getIndicationSet().contains(newValue))
				throw new IllegalArgumentException("Indication not in the actual set!");
			
			Indication oldValue = d_indication;
			d_indication = (Indication) newValue;
				
			fireValueChange(oldValue, d_indication);
		}
		
	}
	private Domain d_domain;
	private IndicationHolder d_valueHolder;
	
	public MetaAnalysisWizardPresentation(Domain d) {
		d_domain = d;
		d_valueHolder = new IndicationHolder();		
	}
	
	public SortedSet<Indication> getIndicationSet() {
		return d_domain.getIndications();
	}
	
	public ValueModel getIndicationModel() {
		return d_valueHolder; 
	}
	
	public SortedSet<Endpoint> getEndpointSet() {
		return d_domain.getEndpoints();
	}
	
	public ValueModel getEndpointModel() {
		return new ValueHolder();
	}
	
	public SortedSet<Drug> getDrugSet() {
		return d_domain.getDrugs();
	}
	
	public ValueModel getFirstDrugModel() {
		return new ValueHolder();
	}
	
	public ValueModel getSecondDrugModel() {
		return new ValueHolder();
	}
	
	public SortedSet<Study> getStudySet() {
		return d_domain.getStudies();
	}
	
	public MetaAnalysis getAnalysis() {
		Endpoint e = d_domain.getEndpoints().first();
		return new MetaAnalysis(e, new ArrayList<Study>(d_domain.getStudies(e)));
	}
}