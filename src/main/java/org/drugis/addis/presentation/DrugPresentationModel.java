package org.drugis.addis.presentation;

import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

@SuppressWarnings("serial")
public class DrugPresentationModel extends PresentationModel<Drug> implements StudyListPresentationModel, LabeledPresentationModel {
	
	private CharacteristicVisibleMap d_charVisibleMap = new CharacteristicVisibleMap();
	private ListHolder<Study> d_studies;

	public DrugPresentationModel(Drug drug, ListHolder<Study> studies) {
		super(drug);
		d_studies = studies;		
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(Characteristic c) {
		return d_charVisibleMap.get(c);
	}

	public ListHolder<Study> getIncludedStudies() {
		return d_studies;
	}

	public AbstractValueModel getLabelModel() {
		return new ValueHolder(getBean().getName());
	}
}
