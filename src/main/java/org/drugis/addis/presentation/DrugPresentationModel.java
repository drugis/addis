package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.SortedSet;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class DrugPresentationModel extends PresentationModel<Drug> {
	
	private StudyListPresentationModelImpl d_studyListModel;

	public DrugPresentationModel(Drug drug, SortedSet<Study> studies) {
		super(drug);
		
		d_studyListModel = new StudyListPresentationModelImpl(new ArrayList<Study>(studies));
	}
	
	public StudyListPresentationModel getStudyListModel() {
		return d_studyListModel;
	}

}
