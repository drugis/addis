package org.drugis.addis.presentation;

import org.drugis.addis.entities.Study;

public interface SelectableStudyListPresentationModel extends StudyListPresentationModel {
	
	public ModifiableHolder<Boolean> getSelectedStudyBooleanModel(Study s);
	public ListHolder<Study> getSelectedStudiesModel();
}
