package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class DrugPresentationModel extends PresentationModel<Drug> implements StudyListPresentationModel {
	
	private CharacteristicVisibleMap d_charVisibleMap = new CharacteristicVisibleMap();
	private ArrayList<Study> d_studies;

	public DrugPresentationModel(Drug drug, SortedSet<Study> studies) {
		super(drug);
		d_studies = new ArrayList<Study>(studies);		
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(StudyCharacteristic c) {
		return d_charVisibleMap.get(c);
	}

	public ListHolder<Study> getIncludedStudies() {
		return new StudyListHolder();
	}

	class StudyListHolder extends AbstractListHolder<Study> {
		@Override
		public List<Study> getValue() {
			return new ArrayList<Study>(d_studies);
		}
	}
}
