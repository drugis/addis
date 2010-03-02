package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.EntityIdExistsException;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyArmsEntry;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.ModifiableHolder;

import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueModel;

public class MetaAnalysisWizardPresentation extends AbstractMetaAnalysisWizardPM<StudyGraphModel> {				
	private ModifiableHolder<Drug> d_firstDrugHolder;
	private ModifiableHolder<Drug> d_secondDrugHolder;
	private MetaAnalysisCompleteListener d_metaAnalysisCompleteListener;
	private ListHolder<Drug> d_selectedDrugs;
	public MetaAnalysisWizardPresentation(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
				
		d_metaAnalysisCompleteListener = new MetaAnalysisCompleteListener();		
		d_studyListPm.getSelectedStudiesModel().addValueChangeListener(d_metaAnalysisCompleteListener);		
	}

	protected void buildDrugHolders() {
		d_firstDrugHolder = new ModifiableHolder<Drug>();		
		d_secondDrugHolder = new ModifiableHolder<Drug>();

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
				new SetEmptyListener(new ModifiableHolder[]{d_firstDrugHolder, d_secondDrugHolder}));
		
		d_selectedDrugs = new SelectedDrugsHolder(d_firstDrugHolder, d_secondDrugHolder);
	}
	
	@SuppressWarnings("serial")
	private static class SelectedDrugsHolder extends AbstractListHolder<Drug> {
		private ArrayList<Drug> d_list;
		private ModifiableHolder<Drug> d_firstDrugHolder;
		private ModifiableHolder<Drug> d_secondDrugHolder;

		public SelectedDrugsHolder(ModifiableHolder<Drug> firstDrugHolder,
				ModifiableHolder<Drug> secondDrugHolder) {
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
		
	public ModifiableHolder<Drug> getFirstDrugModel() {
		return d_firstDrugHolder;
	}
	
	public ModifiableHolder<Drug> getSecondDrugModel() {
		return d_secondDrugHolder;
	}
	
	private Drug getFirstDrug() {
		return d_firstDrugHolder.getValue();
	}

	private Drug getSecondDrug() {
		return d_secondDrugHolder.getValue();
	}
	
	@Override
	public ListHolder<Drug> getSelectedDrugsModel() {
		return d_selectedDrugs;
	}
	
	public RandomEffectsMetaAnalysis createMetaAnalysis(String name) {
		List<StudyArmsEntry> studyArms = new ArrayList <StudyArmsEntry>();
		
		for (Study s : getStudyListModel().getSelectedStudiesModel().getValue()) {
			Arm left = d_selectedArms.get(s).get(d_firstDrugHolder.getValue()).getValue();
			Arm right = d_selectedArms.get(s).get(d_secondDrugHolder.getValue()).getValue();
			studyArms.add(new StudyArmsEntry(s, left, right));
		}
		
		return new RandomEffectsMetaAnalysis(name, (OutcomeMeasure) getOutcomeMeasureModel().getValue(), studyArms);
	}
	
	public RandomEffectsMetaAnalysisPresentation saveMetaAnalysis(String name) throws EntityIdExistsException {
		RandomEffectsMetaAnalysis ma = createMetaAnalysis(name);		
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
		return (RandomEffectsMetaAnalysisPresentation) d_pmm.getModel(createMetaAnalysis(""));
	}

	@Override
	protected StudyGraphModel buildStudyGraphPresentation() {
		return new StudyGraphModel(d_indicationHolder, d_endpointHolder, d_drugListHolder, d_domain);				
	}	
}
