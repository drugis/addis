package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.Study;
import org.drugis.common.EqualsUtil;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentation {
	@SuppressWarnings("serial") 
	abstract class AbstractHolder<T> extends AbstractValueModel {
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
			conditionalCascade(newValue, oldValue);
		}

		private void conditionalCascade(Object newValue, T oldValue) {
			if (!EqualsUtil.equal(oldValue, newValue)) {
				cascade();
			}
		}
		
		public void unSet() {
			T oldValue = d_content;
			d_content = null;
			fireValueChange(oldValue, d_content);
			conditionalCascade(null, oldValue);
		}

		protected abstract void cascade();
	}
	
	@SuppressWarnings("serial") class IndicationHolder extends AbstractHolder<Indication> {
		@Override
		protected void cascade() {
			d_endpointHolder.unSet();
		}
		
		@Override
		protected void checkArgument(Object newValue) {
			if (!getIndicationSet().contains(newValue))
				throw new IllegalArgumentException("Indication not in the actual set!");
		}
	}
	
	@SuppressWarnings("serial")
	private class BooleanHolder extends AbstractHolder<Boolean> {
		public BooleanHolder() {
			setValue(true);
		}
		
		@Override
		protected void cascade() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void checkArgument(Object newValue) {
			// TODO Auto-generated method stub
			
		}

	}
	
	@SuppressWarnings("serial")
	private class EndpointHolder extends AbstractHolder<Endpoint> {
		@Override
		protected void checkArgument(Object newValue) {
			if (newValue != null)
				if (!getEndpointSet().contains(newValue))
					throw new IllegalArgumentException("Endpoint not in the actual set!");
		}

		@Override
		protected void cascade() {
			d_firstDrugHolder.unSet();
			d_secondDrugHolder.unSet();
		}
	}
	
	@SuppressWarnings("serial")
	private class DrugHolder extends AbstractHolder<Drug> {
		@Override
		protected void checkArgument(Object newValue) {
			if (newValue != null)
				if (!getDrugSet().contains(newValue))
					throw new IllegalArgumentException("Drug not in the actual set!");
		}

		protected void cascade() {
		}
	}
	
	@SuppressWarnings("serial")
	private class IndicationListHolder extends AbstractListHolder<Indication> {
		@Override
		public List<Indication> getValue() {
			return new ArrayList<Indication>(getIndicationSet());
		}
	}
	
	@SuppressWarnings("serial")
	private class EndpointListHolder extends AbstractListHolder<Endpoint> implements PropertyChangeListener {
		public EndpointListHolder() {
			getIndicationModel().addValueChangeListener(this);
		}
		
		@Override
		public List<Endpoint> getValue() {
			return new ArrayList<Endpoint>(getEndpointSet());
		}

		public void propertyChange(PropertyChangeEvent event) {
			fireValueChange(null, getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private class DrugListHolder extends AbstractListHolder<Drug> implements PropertyChangeListener {
		public DrugListHolder() {
			getEndpointModel().addValueChangeListener(this);
		}
		
		@Override
		public List<Drug> getValue() {
			return new ArrayList<Drug>(getDrugSet());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange(null, getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private class StudyListHolder extends AbstractListHolder<Study> implements PropertyChangeListener {
		public StudyListHolder() {
			getFirstDrugModel().addValueChangeListener(this);
			getSecondDrugModel().addValueChangeListener(this);
		}
		
		@Override
		public List<Study> getValue() {
			return new ArrayList<Study>(getStudySet());
		}

		public void propertyChange(PropertyChangeEvent evt) {
			fireValueChange(null, getValue());
			fillSelectedStudySet();
		}
	}
		
	private Domain d_domain;
	private AbstractHolder<Indication> d_indicationHolder;
	private AbstractHolder<Endpoint> d_endpointHolder;
	private StudiesMeasuringValueModel d_studiesMeasuringValueModel;	
	private DrugHolder d_firstDrugHolder;
	private DrugHolder d_secondDrugHolder;
	private EndpointListHolder d_endpointListHolder;
	private DrugListHolder d_drugListHolder;
	private StudyListHolder d_studyListHolder;
	private HashMap<Study,AbstractHolder<Boolean>> d_selectedStudies;
	
	
	public MetaAnalysisWizardPresentation(Domain d) {
		d_domain = d;
		d_indicationHolder = new IndicationHolder();
		d_endpointHolder = new EndpointHolder();
		d_firstDrugHolder = new DrugHolder();
		d_studiesMeasuringValueModel = new StudiesMeasuringValueModel();		
		d_secondDrugHolder = new DrugHolder();
		d_endpointListHolder = new EndpointListHolder();
		d_drugListHolder = new DrugListHolder();
		d_firstDrugHolder.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null && evt.getNewValue().equals(getSecondDrug())) {
					d_secondDrugHolder.unSet();
				}					
			}			
		});
		d_secondDrugHolder.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null && evt.getNewValue().equals(getFirstDrug())) {
					d_firstDrugHolder.unSet();
				}					
			}			
		});
		d_studyListHolder = new StudyListHolder();
		d_selectedStudies = new HashMap<Study,AbstractHolder<Boolean>>();
	}
	
	public SortedSet<Study> getSelectedStudySet() {
		SortedSet<Study> set = new TreeSet<Study>();
		for (Map.Entry<Study, AbstractHolder<Boolean>> e : d_selectedStudies.entrySet()) {
			if (e.getValue().getValue().equals(Boolean.TRUE)) {
				set.add(e.getKey());
			}
		}
		return set;
	}
	
	private void fillSelectedStudySet() {
		d_selectedStudies.clear();
		for (Study s : getStudySet()) {
			d_selectedStudies.put(s, new BooleanHolder()) ;
		}
	}
	
	public AbstractHolder<Boolean> getSelectedStudyBooleanModel(Study study) {
		return d_selectedStudies.get(study);
	}
	
	public ListHolder<Indication> getIndicationListModel() {
		return new IndicationListHolder();
	}
	
	public SortedSet<Indication> getIndicationSet() {
		return d_domain.getIndications();
	}
	
	public ValueModel getIndicationModel() {
		return d_indicationHolder; 
	}
	
	public AbstractListHolder<Endpoint> getEndpointListModel() {
		return d_endpointListHolder;
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
	
	public AbstractListHolder<Drug> getDrugListModel() {
		return d_drugListHolder;
	}
	
	public SortedSet<Drug> getDrugSet() {
		SortedSet<Drug> drugs = new TreeSet<Drug>();
		if (getIndication() != null && getEndpoint() != null) {
			SortedSet<Study> studies = getStudiesEndpointAndIndication();
			for (Study s : studies) {
				drugs.addAll(s.getDrugs());
			}
		}
		return drugs;
	}

	private SortedSet<Study> getStudiesEndpointAndIndication() {
		SortedSet<Study> studies = new TreeSet<Study>(d_domain.getStudies(getEndpoint()));
		studies.retainAll(d_domain.getStudies(getIndication()));
		return studies;
	}

	private Indication getIndication() {
		return d_indicationHolder.getValue();
	}

	private Endpoint getEndpoint() {
		return d_endpointHolder.getValue();
	}
	
	public ValueModel getFirstDrugModel() {
		return d_firstDrugHolder;
	}
	
	public ValueModel getSecondDrugModel() {
		return d_secondDrugHolder;
	}
	
	public SortedSet<Study> getStudySet() {
		SortedSet<Study> studies = new TreeSet<Study>();
		if (getSecondDrug() != null && getFirstDrug() != null) {
			studies = getStudiesEndpointAndIndication();
			studies.retainAll(d_domain.getStudies(getFirstDrug()));
			studies.retainAll(d_domain.getStudies(getSecondDrug()));
		}
		return studies;
	}
	
	private Drug getFirstDrug() {
		return d_firstDrugHolder.getValue();
	}

	private Drug getSecondDrug() {
		return d_secondDrugHolder.getValue();
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

	public AbstractListHolder<Study> getStudyListModel() {
		return d_studyListHolder;
	}
	
	public StudyCharTableModel getStudyTableModel() {
		return new StudyCharTableModel(new DefaultStudyListPresentationModel(getStudyListModel()));
	}

	public MetaAnalysis createMetaAnalysis() {
		return new MetaAnalysis((Endpoint)getEndpointModel().getValue(),
				new ArrayList<Study>(getSelectedStudySet()));
	}
	
	public void saveMetaAnalysis(String name, MetaAnalysis ma) {
		d_domain.addStudy(new MetaStudy(name,ma));
	}
}
