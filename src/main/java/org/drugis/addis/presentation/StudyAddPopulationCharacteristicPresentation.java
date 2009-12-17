package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.Variable;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class StudyAddPopulationCharacteristicPresentation {

	private Domain d_domain;
	private AbstractHolder<Variable> d_varModel;
	private StudyPresentationModel d_studyModel;
	private AbstractHolder<Measurement> d_measurement;
	
	public StudyAddPopulationCharacteristicPresentation(
			StudyPresentationModel model, Domain domain) {
		d_domain = domain;
		d_varModel = new AbstractHolder<Variable>() {
			protected void cascade() {
				d_measurement.setValue(getValue().buildMeasurement());
			}
			protected void checkArgument(Object newValue) {
			}
		};
		d_measurement = new AbstractHolder<Measurement>() {
			@Override
			protected void cascade() {
			}
			@Override
			protected void checkArgument(Object newValue) {
			}
		};
		
		d_studyModel = model;
		
	}

	public void addToStudy() {
		d_studyModel.getBean().setPopulationCharacteristic(d_varModel.getValue(), d_measurement.getValue());
	}
	
	public List<Variable> getVariableList() {
		ArrayList<Variable> list = new ArrayList<Variable>(d_domain.getVariables());
		list.removeAll(d_studyModel.getBean().getPopulationCharacteristics().keySet());
		return list;
	}
	
	public AbstractValueModel getVariableModel() {
		return d_varModel;
	}
	
	public AbstractValueModel getMeasurementModel() {
		return d_measurement;
	}
}
