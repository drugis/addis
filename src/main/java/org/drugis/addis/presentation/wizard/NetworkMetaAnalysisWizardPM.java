package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.DefaultListHolder;
import org.drugis.addis.presentation.DefaultSelectableStudyListPresentationModel;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.SelectableStudyListPresentationModel;
import org.drugis.addis.presentation.TypedHolder;

import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysisWizardPM extends AbstractMetaAnalysisWizardPM<SelectableStudyGraphModel>{

	private DrugSelectionCompleteListener d_connectedDrugsSelectedModel;
	private DefaultSelectableStudyListPresentationModel d_studyListPm;	

	public NetworkMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
		d_connectedDrugsSelectedModel = new DrugSelectionCompleteListener();
		d_studyGraphPresentationModel.getSelectedDrugsModel().addValueChangeListener(d_connectedDrugsSelectedModel);
		d_studyListPm = new DefaultSelectableStudyListPresentationModel(new StudyListHolder());
	}
	
	public SelectableStudyListPresentationModel getStudyListModel() {
		return d_studyListPm;
	}
	
	public ListHolder<Drug> getSelectedDrugsModel() {
		return d_studyGraphPresentationModel.getSelectedDrugsModel();
	}

	@Override
	protected SelectableStudyGraphModel buildStudyGraphPresentation() {
		return new SelectableStudyGraphModel(d_indicationHolder, d_endpointHolder, d_drugListHolder, d_domain);
	}
	
	public ValueModel getConnectedDrugsSelectedModel() {
		return d_connectedDrugsSelectedModel;
	}
	
	@SuppressWarnings("serial")
	private class StudyListHolder extends DefaultListHolder<Study> implements PropertyChangeListener {
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
	
	@SuppressWarnings("serial")
	private class DrugSelectionCompleteListener extends TypedHolder<Boolean> implements PropertyChangeListener {
		
		public DrugSelectionCompleteListener() {
			setValue(false);
		}
		@SuppressWarnings("unchecked")
		public void propertyChange(PropertyChangeEvent evt) {
			List<Drug> selectedDrugs = (List<Drug>) evt.getNewValue();	
			setValue(selectedDrugs.size() > 1 && d_studyGraphPresentationModel.isSelectionConnected());			
		}
	}
}
