package org.drugis.addis.presentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;
import com.jgoodies.binding.value.ValueHolder;

@SuppressWarnings("serial")
public class MetaStudyPresentationModel extends PresentationModel<MetaStudy> {
	private Map<StudyCharacteristic, AbstractValueModel> d_characteristicVisibleMap;
	
	public MetaStudyPresentationModel(MetaStudy study) {
		super(study);
		d_characteristicVisibleMap = new HashMap<StudyCharacteristic, AbstractValueModel>();
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			d_characteristicVisibleMap.put(c, new ValueHolder(true));
		}
	}
	
	public List<Study> getIncludedStudies() {
		return getBean().getAnalysis().getStudies();
	}
	
	public AbstractValueModel getCharacteristicVisibleModel(StudyCharacteristic c) {
		return d_characteristicVisibleMap.get(c);
	}
}
