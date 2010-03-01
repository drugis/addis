package org.drugis.addis.presentation.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyGraphModel;
import org.drugis.addis.presentation.StudyGraphModel;
import org.drugis.addis.presentation.TypedHolder;

import com.jgoodies.binding.value.ValueModel;

public class NetworkMetaAnalysisWizardPM extends AbstractMetaAnalysisWizardPM<SelectableStudyGraphModel>{
	private DrugSelectionCompleteListener d_connectedDrugsSelectedModel;

	public NetworkMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
	}

	@Override
	protected void buildDrugHolders() {
		d_connectedDrugsSelectedModel = new DrugSelectionCompleteListener();
		d_studyGraphPresentationModel.getSelectedDrugsModel().addValueChangeListener(d_connectedDrugsSelectedModel);
	}

	public ListHolder<Drug> getSelectedDrugsModel() {
		return d_studyGraphPresentationModel.getSelectedDrugsModel();
	}
	
	public StudyGraphModel getSelectedStudyGraphModel(){
		return getStudyGraphModel();
	}

	@Override
	protected SelectableStudyGraphModel buildStudyGraphPresentation() {
		return new SelectableStudyGraphModel(d_indicationHolder, d_endpointHolder, d_drugListHolder, d_domain);
	}
	
	public ValueModel getConnectedDrugsSelectedModel() {
		return d_connectedDrugsSelectedModel;
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
