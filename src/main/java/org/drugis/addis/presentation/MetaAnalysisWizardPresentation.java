package org.drugis.addis.presentation;

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
		
		public void unSet() {
			T oldValue = d_content;
			d_content = null;
			fireValueChange(oldValue, d_content);
		}
	}
	
	@SuppressWarnings("serial")
	private class IndicationHolder extends AbstractHolder<Indication> {
		@Override
		public void setValue(Object newValue) {
			super.setValue(newValue);
			d_endpointHolder.unSet();
		}
		@Override
		protected void checkArgument(Object newValue) {
			if (!getIndicationSet().contains(newValue))
				throw new IllegalArgumentException("Indication not in the actual set!");
		}
	}
	
	@SuppressWarnings("serial")
	private class EndpointHolder extends AbstractHolder<Endpoint> {
		@Override
		protected void checkArgument(Object newValue) {
			if (!getEndpointSet().contains(newValue))
				throw new IllegalArgumentException("Endpoint not in the actual set!");
		}
	}
		
	private Domain d_domain;
	private AbstractHolder<Indication> d_indicationHolder;
	private AbstractHolder<Endpoint> d_endpointHolder;
	
	public MetaAnalysisWizardPresentation(Domain d) {
		d_domain = d;
		d_indicationHolder = new IndicationHolder();
		d_endpointHolder = new EndpointHolder();
	}
	
	public SortedSet<Indication> getIndicationSet() {
		return d_domain.getIndications();
	}
	
	public ValueModel getIndicationModel() {
		return d_indicationHolder; 
	}
	
	public SortedSet<Endpoint> getEndpointSet() {
		TreeSet<Endpoint> endpoints = new TreeSet<Endpoint>();
		if (getIndication() != null) {
			for (Study s : d_domain.getStudies(getIndication())) {
				endpoints.addAll(s.getEndpoints());
			}
		}
		return endpoints;
	}
	
	public ValueModel getEndpointModel() {
		return d_endpointHolder;
	}
	
	public SortedSet<Drug> getDrugSet() {
		SortedSet<Drug> drugs = new TreeSet<Drug>();
		if (getIndication() != null && getEndpoint() != null) {
			SortedSet<Study> studies = new TreeSet<Study>(d_domain.getStudies(getEndpoint()));
			studies.retainAll(d_domain.getStudies(getIndication()));
			for (Study s : studies) {
				drugs.addAll(s.getDrugs());
			}
		}
		return drugs;
	}

	private Indication getIndication() {
		return d_indicationHolder.getValue();
	}

	private Endpoint getEndpoint() {
		return d_endpointHolder.getValue();
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