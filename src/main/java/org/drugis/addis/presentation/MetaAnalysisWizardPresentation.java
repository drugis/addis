package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeListener;
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
	private ValueHolder d_endpointHolder;
	private StudiesMeasuringValueModel d_studiesMeasuringValueModel;	
	
	public MetaAnalysisWizardPresentation(Domain d) {
		d_domain = d;
		d_valueHolder = new IndicationHolder();
		d_endpointHolder = new ValueHolder();		
		d_studiesMeasuringValueModel = new StudiesMeasuringValueModel();		
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
		return d_endpointHolder;
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
	
	public ValueModel getStudiesMeasuringLabelModel() {
		return d_studiesMeasuringValueModel;
	}
	
	@SuppressWarnings("serial")
	public class StudiesMeasuringValueModel extends AbstractValueModel implements PropertyChangeListener {
		
		public StudiesMeasuringValueModel() {
			d_endpointHolder.addValueChangeListener(this);
			d_valueHolder.addValueChangeListener(this);			
		}

		public Object getValue() {
			return constructString();
		}

		private Object constructString() {
			String indVal = d_valueHolder.getValue() != null ? d_valueHolder.getValue().toString() : "";
			String endpVal = d_endpointHolder.getValue() != null ? d_endpointHolder.getValue().toString() : "";
			return "Studies measuring " + indVal + " on " + endpVal;
		}
		
		public void setValue(Object newValue) {
			throw new RuntimeException("value set not allowed");
		}

		public void propertyChange(PropertyChangeEvent arg0) {
			fireValueChange(null, constructString());
		}		
	}
}