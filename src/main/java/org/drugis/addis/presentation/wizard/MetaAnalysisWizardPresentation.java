package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.DefaultSelectableStudyListPresentationModel;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.addis.presentation.SelectableStudyListPresentationModel;
import org.drugis.addis.presentation.StudyGraphPresentation;
import org.drugis.addis.presentation.TypedHolder;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentation extends AbstractMetaAnalysisWizardPM<StudyGraphPresentation> {
				
	private StudiesMeasuringValueModel d_studiesMeasuringValueModel;	
	private TypedHolder<Drug> d_firstDrugHolder;
	private TypedHolder<Drug> d_secondDrugHolder;
	private DefaultSelectableStudyListPresentationModel d_studyListPm;
	private MetaAnalysisCompleteListener d_metaAnalysisCompleteListener;
	private List<Study> d_studyList = new ArrayList<Study>();
	private Map<Study, TypedHolder<Arm>> d_firstArms;
	private Map<Study, TypedHolder<Arm>> d_secondArms;
	public MetaAnalysisWizardPresentation(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
		
		d_studiesMeasuringValueModel = new StudiesMeasuringValueModel();
		
		buildDrugHolders();
		
		d_studyListPm = new DefaultSelectableStudyListPresentationModel(new StudyListHolder());
				
		d_metaAnalysisCompleteListener = new MetaAnalysisCompleteListener();		
		d_studyListPm.getSelectedStudiesModel().addValueChangeListener(d_metaAnalysisCompleteListener);
		
		d_studyListPm.getSelectedStudiesModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				updateArmHolders();
			}
		});
		d_firstArms = new HashMap <Study, TypedHolder<Arm>>() ;
		d_secondArms = new HashMap <Study, TypedHolder<Arm>>() ;		
	}

	private void buildDrugHolders() {
		d_firstDrugHolder = new TypedHolder<Drug>();		
		d_secondDrugHolder = new TypedHolder<Drug>();
		d_firstDrugHolder.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null && evt.getNewValue().equals(getSecondDrug())) {
					d_secondDrugHolder.setValue(null);
				}									
			}
		});
		d_secondDrugHolder.addValueChangeListener(new PropertyChangeListener(){
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getNewValue() != null && evt.getNewValue().equals(getFirstDrug())) {
					d_firstDrugHolder.setValue(null);
				}									
			}			
		});
		
		d_endpointHolder.addPropertyChangeListener(
				new SetEmptyListener(new TypedHolder[]{d_firstDrugHolder, d_secondDrugHolder}));
	}
		
	public ValueModel getFirstDrugModel() {
		return d_firstDrugHolder;
	}
	
	public ValueModel getSecondDrugModel() {
		return d_secondDrugHolder;
	}
	
	private void updateStudyList() {
		List<Study> studies = new ArrayList<Study>();
		if (getSecondDrug() != null && getFirstDrug() != null) {
			studies = getStudiesEndpointAndIndication();
			studies.retainAll(d_domain.getStudies(getFirstDrug()).getValue());
			studies.retainAll(d_domain.getStudies(getSecondDrug()).getValue());
		}
		d_studyList = studies;
	}
	
	private void updateArmHolders() {
		d_firstArms.clear();
		d_secondArms.clear();
		for (Study s : getStudyListModel().getSelectedStudiesModel().getValue()) {
			d_firstArms.put(s, new TypedHolder<Arm>(getArmsPerStudyPerDrug(s, getFirstDrug()).getValue().get(0)));
			d_secondArms.put(s, new TypedHolder<Arm>(getArmsPerStudyPerDrug(s, getSecondDrug()).getValue().get(0)));
		}
	}
	
	private Drug getFirstDrug() {
		return d_firstDrugHolder.getValue();
	}

	private Drug getSecondDrug() {
		return d_secondDrugHolder.getValue();
	}
	
	public ValueModel getStudiesMeasuringLabelModel() {
		return d_studiesMeasuringValueModel;
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
			return "Graph of studies measuring " + indVal + " on " + endpVal;
		}
		
		public void setValue(Object newValue) {
			throw new RuntimeException("value set not allowed");
		}

		public void propertyChange(PropertyChangeEvent arg0) {
			fireValueChange(null, constructString());
		}		
	}

	public SelectableStudyListPresentationModel getStudyListModel() {
		return d_studyListPm;
	}
	
	private RandomEffectsMetaAnalysis createMetaAnalysis() {
		List<StudyArmsEntry> studyArms = new ArrayList <StudyArmsEntry>();
		
		for(Entry<Study,TypedHolder<Arm>> entry : d_firstArms.entrySet()) {
			studyArms.add(new StudyArmsEntry(entry.getKey(), entry.getValue().getValue(), d_secondArms.get(entry.getKey()).getValue() ) );
		}
		
		return new RandomEffectsMetaAnalysis("", (OutcomeMeasure) getEndpointModel().getValue(), studyArms);
	}
	
	public RandomEffectsMetaAnalysisPresentation saveMetaAnalysis(String name) throws EntityIdExistsException {
		RandomEffectsMetaAnalysis ma = createMetaAnalysis();		
		ma.setName(name);
		d_domain.addMetaAnalysis(ma);
		return (RandomEffectsMetaAnalysisPresentation) d_pmm.getModel(ma);
	}
	
	public ValueModel getMetaAnalysisCompleteModel() {
		return d_metaAnalysisCompleteListener;
	}
	
	@SuppressWarnings("serial")
	public class MetaAnalysisCompleteListener extends AbstractValueModel implements PropertyChangeListener {

		public void propertyChange(PropertyChangeEvent evt) {
			firePropertyChange(PROPERTYNAME_VALUE, null, getValue());
		}

		public Object getValue() {
			return new Boolean(!d_studyListPm.getSelectedStudiesModel().getValue().isEmpty());
		}

		public void setValue(Object newValue) {			
		}		
	}

	public RandomEffectsMetaAnalysisPresentation getMetaAnalysisModel() {
		return (RandomEffectsMetaAnalysisPresentation) d_pmm.getModel(createMetaAnalysis());
	}
	
	public ListHolder<Arm> getArmsPerStudyPerDrug(Study study, Drug drug) {
		return new ArmListHolder(study, drug);
	}
	
	public ValueModel getLeftArmPerStudyPerDrug(Study study) {
		return d_firstArms.get(study);
	}
	
	public ValueModel getRightArmPerStudyPerDrug(Study study) {
		return d_secondArms.get(study);
	}

	public StudyGraphPresentation getStudyGraphModel() {
		return d_studyGraphPresentationModel;
	}
		
	@SuppressWarnings("serial")
	private class StudyListHolder extends AbstractListHolder<Study> implements PropertyChangeListener {
		public StudyListHolder() {
			getFirstDrugModel().addValueChangeListener(this);
			getSecondDrugModel().addValueChangeListener(this);
		}
		
		@Override
		public List<Study> getValue() {
			return d_studyList;
		}

		public void propertyChange(PropertyChangeEvent evt) {
			updateStudyList();
			fireValueChange(null, getValue());
		}
	}

	@Override
	protected StudyGraphPresentation buildStudyGraphPresentation() {
		return new StudyGraphPresentation(d_indicationHolder, d_endpointHolder, d_drugListHolder, d_domain);				
	}	
}
