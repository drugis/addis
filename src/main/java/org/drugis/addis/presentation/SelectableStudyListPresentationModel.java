package org.drugis.addis.presentation;

import org.drugis.addis.entities.Study;

public interface SelectableStudyListPresentationModel extends StudyListPresentationModel {
	
	public AbstractHolder<Boolean> getSelectedStudyBooleanModel(Study s);
	public ListHolder<Study> getSelectedStudiesModel();
}
