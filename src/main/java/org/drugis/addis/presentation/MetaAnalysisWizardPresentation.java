package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

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
	abstract private class AbstractHolder<T> extends AbstractValueModel {
		protected abstract void checkArgument(Object newValue);

		private T d_content = null;

		public T getValue() {
			return d_content;
		}

		@SuppressWarnings("unchecked")
		public void setValue(Object newValue) {
			checkArgument(newValue);
			T oldValue = d_content;
			d_content = (T) newValue;
			fireValueChange(oldValue, d_content);
		}
		
	}
	
	@SuppressWarnings("serial")
	class IndicationHolder extends AbstractHolder<Indication> {
		@Override
		protected void checkArgument(Object newValue) {
			if (!getIndicationSet().contains(newValue))
				throw new IllegalArgumentException("Indication not in the actual set!");
		}
	}
	
	@SuppressWarnings("serial")
	class EndpointHolder extends AbstractHolder<Endpoint> {
		@Override
		protected void checkArgument(Object newValue) {
			if (!getEndpointSet().contains(newValue))
				throw new IllegalArgumentException("Endpoint not in the actual set!");
		}
	}
		
	private Domain d_domain;
	private AbstractHolder<Indication> d_indicationHolder;
	private AbstractHolder<Endpoint> d_endpointHolder;
	private StudiesMeasuringValueModel d_studiesMeasuringValueModel;	
	
	public MetaAnalysisWizardPresentation(Domain d) {
		d_domain = d;
		d_indicationHolder = new IndicationHolder();
		d_endpointHolder = new EndpointHolder();
		d_studiesMeasuringValueModel = new StudiesMeasuringValueModel();		
	}
	
	public SortedSet<Indication> getIndicationSet() {
		return d_domain.getIndications();
	}
	
	public ValueModel getIndicationModel() {
		return d_indicationHolder; 
	}
	
	public SortedSet<Endpoint> getEndpointSet() {
		TreeSet<Endpoint> endpoints = new TreeSet<Endpoint>();
		if (d_indicationHolder.getValue() != null) {
			for (Study s : d_domain.getStudies(d_indicationHolder.getValue())) {
				endpoints.addAll(s.getEndpoints());
			}			
		}	
		return endpoints;
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
			d_indicationHolder.addValueChangeListener(this);			
		}

		public Object getValue() {
			return constructString();
		}

		private Object constructString() {
			String indVal = d_indicationHolder.getValue() != null ? d_indicationHolder.getValue().toString() : "";
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