package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.DefaultListHolder;
import org.drugis.addis.presentation.DefaultSelectableStudyListPresentationModel;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyListPresentationModel;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.ModifiableHolder;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public abstract class AbstractMetaAnalysisWizardPM<G extends StudyGraphModel> {

	protected Domain d_domain;
	protected PresentationModelFactory d_pmm;
	protected ModifiableHolder<Indication> d_indicationHolder;
	protected ModifiableHolder<OutcomeMeasure> d_endpointHolder;
	protected OutcomeListHolder d_outcomeListHolder;
	protected DrugListHolder d_drugListHolder;
	protected G d_studyGraphPresentationModel;	
	private StudiesMeasuringValueModel d_studiesMeasuringValueModel;
	protected Map<Study, Map<Drug, ModifiableHolder<Arm>>> d_selectedArms;
	protected DefaultSelectableStudyListPresentationModel d_studyListPm;	

	public AbstractMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmm) {
		d_domain = d;
		d_pmm = pmm;
	
		d_indicationHolder = new ModifiableHolder<Indication>();
		d_endpointHolder = new ModifiableHolder<OutcomeMeasure>();
		d_indicationHolder.addPropertyChangeListener(new SetEmptyListener(d_endpointHolder));
		d_outcomeListHolder = new OutcomeListHolder(d_indicationHolder, d_domain);		
		d_drugListHolder = new DrugListHolder();
		d_studyGraphPresentationModel = buildStudyGraphPresentation();
		buildDrugHolders();
		d_studyListPm = new DefaultSelectableStudyListPresentationModel(new StudyListHolder());

		d_studiesMeasuringValueModel = new StudiesMeasuringValueModel();
		
		d_selectedArms = new HashMap<Study, Map<Drug, ModifiableHolder<Arm>>>();
		d_studyListPm.getSelectedStudiesModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				updateArmHolders();
			}
		});
	}
	
	protected abstract void buildDrugHolders();
	
	abstract protected G buildStudyGraphPresentation();
	
	public ValueModel getIndicationModel() {
		return d_indicationHolder; 
	}
	
	protected List<Study> getStudiesEndpointAndIndication() {
		if (d_endpointHolder.getValue() == null || d_indicationHolder.getValue() == null) {
			return Collections.emptyList();
		}
		List<Study> studies = new ArrayList<Study>(d_domain.getStudies(d_endpointHolder.getValue()).getValue());
		studies.retainAll(d_domain.getStudies(d_indicationHolder.getValue()).getValue());
		return studies;
	}	
	
	public ValueModel getStudiesMeasuringLabelModel() {
		return d_studiesMeasuringValueModel;
	}	
	
	public ValueModel getEndpointModel() {
		return d_endpointHolder;
	}

	public AbstractListHolder<Drug> getDrugListModel() {
		return d_drugListHolder;
	}

	@SuppressWarnings("serial")
	public ListHolder<Indication> getIndicationListModel() {
		return new AbstractListHolder<Indication>() {
			@Override
			public List<Indication> getValue() {
				return new ArrayList<Indication>(d_domain.getIndications());
			}
		};
	}
	
	public AbstractListHolder<OutcomeMeasure> getOutcomeMeasureListModel() {
		return d_outcomeListHolder;
	}

	public abstract ListHolder<Drug> getSelectedDrugsModel();

	public G getStudyGraphModel() {
		return d_studyGraphPresentationModel;
	}

	protected void updateArmHolders() {
		d_selectedArms.clear();
		
		for(Study s : getStudyListModel().getSelectedStudiesModel().getValue()) {
			d_selectedArms.put(s, new HashMap<Drug, ModifiableHolder<Arm>>());
			for(Drug d : getSelectedDrugsModel().getValue()){
				if(s.getDrugs().contains(d)){
					d_selectedArms.get(s).put(d, new ModifiableHolder<Arm>(getDefaultArm(s, d)));
				}
			}
		}
	}

	private Arm getDefaultArm(Study s, Drug d) {
		return getArmsPerStudyPerDrug(s, d).getValue().get(0);
	}

	public ListHolder<Arm> getArmsPerStudyPerDrug(Study study, Drug drug) {
		return new ArmListHolder(study, drug);
	}

	public ModifiableHolder<Arm> getSelectedArmModel(Study study, Drug drug) {
		return d_selectedArms.get(study).get(drug);
	}

	public SelectableStudyListPresentationModel getStudyListModel() {
		return d_studyListPm;
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
	
	@SuppressWarnings("serial")
	public class StudiesMeasuringValueModel extends AbstractValueModel implements PropertyChangeListener {
		
		public StudiesMeasuringValueModel() {
			d_endpointHolder.addValueChangeListener(this);			
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

	@SuppressWarnings("serial")
	protected class StudyListHolder extends DefaultListHolder<Study> implements PropertyChangeListener {
		public StudyListHolder() {
			super(new ArrayList<Study>());
			getSelectedDrugsModel().addValueChangeListener(this);
		}
	
		public void propertyChange(PropertyChangeEvent ev) {
			List<Study> allStudies = getStudiesEndpointAndIndication();
			List<Study> okStudies = new ArrayList<Study>();
			for (Study s : allStudies) {
				List<Drug> drugs = new ArrayList<Drug>(s.getDrugs());
				drugs.retainAll(getSelectedDrugsModel().getValue());
				if (drugs.size() >= 2) {
					okStudies.add(s);
				}
			}
			setValue(okStudies);
		}		
	}	
}
