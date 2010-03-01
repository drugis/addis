package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.TypedHolder;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentation extends AbstractMetaAnalysisWizardPM<StudyGraphModel> {				
	private TypedHolder<Drug> d_firstDrugHolder;
	private TypedHolder<Drug> d_secondDrugHolder;
	private DefaultSelectableStudyListPresentationModel d_studyListPm;
	private MetaAnalysisCompleteListener d_metaAnalysisCompleteListener;
	private ListHolder<Drug> d_selectedDrugs;
	private Map<Study, Map<Drug, TypedHolder<Arm>>> d_selectedArms;
	
	public MetaAnalysisWizardPresentation(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
		
		buildDrugHolders();
		
		d_studyListPm = new DefaultSelectableStudyListPresentationModel(new StudyListHolder());
				
		d_metaAnalysisCompleteListener = new MetaAnalysisCompleteListener();		
		d_studyListPm.getSelectedStudiesModel().addValueChangeListener(d_metaAnalysisCompleteListener);
		
		d_studyListPm.getSelectedStudiesModel().addValueChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent ev) {
				updateArmHolders();
			}
		});
		
		d_selectedArms = new HashMap<Study, Map<Drug, TypedHolder<Arm>>>();
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
		
		d_selectedDrugs = new SelectedDrugsHolder(d_firstDrugHolder, d_secondDrugHolder);
	}
	
	@SuppressWarnings("serial")
	private static class SelectedDrugsHolder extends AbstractListHolder<Drug> {
		private ArrayList<Drug> d_list;
		private TypedHolder<Drug> d_firstDrugHolder;
		private TypedHolder<Drug> d_secondDrugHolder;

		public SelectedDrugsHolder(TypedHolder<Drug> firstDrugHolder,
				TypedHolder<Drug> secondDrugHolder) {
			d_list = new ArrayList<Drug>();
			d_firstDrugHolder = firstDrugHolder;
			d_secondDrugHolder = secondDrugHolder;
			updated();
			PropertyChangeListener listener = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					updated();
				}
			};
			d_firstDrugHolder.addValueChangeListener(listener);
			d_secondDrugHolder.addValueChangeListener(listener);
		}
		
		private void updated() {
			d_list.clear();
			if (d_firstDrugHolder.getValue() != null) {
				d_list.add(d_firstDrugHolder.getValue());
			}
			if (d_secondDrugHolder.getValue() != null) {
				d_list.add(d_secondDrugHolder.getValue());
			}
			fireValueChange(null, Collections.unmodifiableList(d_list));
		}

		@Override
		public List<Drug> getValue() {
			return Collections.unmodifiableList(d_list);
		}
		
	}
		
	public TypedHolder<Drug> getFirstDrugModel() {
		return d_firstDrugHolder;
	}
	
	public TypedHolder<Drug> getSecondDrugModel() {
		return d_secondDrugHolder;
	}
	
	private void updateArmHolders() {
		d_selectedArms.clear();
		
		for(Study s : getStudyListModel().getSelectedStudiesModel().getValue()) {
			d_selectedArms.put(s, new HashMap<Drug, TypedHolder<Arm>>());
			for(Drug d : getSelectedDrugsModel().getValue()){
				if(s.getDrugs().contains(d)){
					d_selectedArms.get(s).put(d, new TypedHolder<Arm>(getDefaultArm(s, d)));
				}
			}
		}
	}

	private Arm getDefaultArm(Study s, Drug d) {
		return getArmsPerStudyPerDrug(s, d).getValue().get(0);
	}
	
	private Drug getFirstDrug() {
		return d_firstDrugHolder.getValue();
	}

	private Drug getSecondDrug() {
		return d_secondDrugHolder.getValue();
	}
	
	public ListHolder<Drug> getSelectedDrugsModel() {
		return d_selectedDrugs;
	}
	
	public SelectableStudyListPresentationModel getStudyListModel() {
		return d_studyListPm;
	}
	
	private RandomEffectsMetaAnalysis createMetaAnalysis() {
		List<StudyArmsEntry> studyArms = new ArrayList <StudyArmsEntry>();
		
		for (Study s : getStudyListModel().getSelectedStudiesModel().getValue()) {
			Arm left = d_selectedArms.get(s).get(d_firstDrugHolder.getValue()).getValue();
			Arm right = d_selectedArms.get(s).get(d_secondDrugHolder.getValue()).getValue();
			studyArms.add(new StudyArmsEntry(s, left, right));
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
	
	public TypedHolder<Arm> getSelectedArmModel(Study study, Drug drug) {
		return d_selectedArms.get(study).get(drug);
	}

	@SuppressWarnings("serial")
	private class StudyListHolder extends AbstractListHolder<Study> implements PropertyChangeListener {
		private List<Study> d_studyList = new ArrayList<Study>();
		
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
		
		private void updateStudyList() {
			List<Study> studies = new ArrayList<Study>();
			if (getSecondDrug() != null && getFirstDrug() != null) {
				studies = getStudiesEndpointAndIndication();
				studies.retainAll(d_domain.getStudies(getFirstDrug()).getValue());
				studies.retainAll(d_domain.getStudies(getSecondDrug()).getValue());
			}
			d_studyList = studies;
		}		
	}

	@Override
	protected StudyGraphModel buildStudyGraphPresentation() {
		return new StudyGraphModel(d_indicationHolder, d_endpointHolder, d_drugListHolder, d_domain);				
	}	
}
