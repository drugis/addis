package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.DerivedStudyCharacteristic;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.DerivedStudyCharacteristic.Dosing;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class StudyPresentationModel extends PresentationModel<Study> {
	private StudyCharacteristicHolder d_armsHolder;
	private StudyCharacteristicHolder d_doseHolder;
	private StudyCharacteristicHolder d_drugHolder;
	private StudyCharacteristicHolder d_sizeHolder;
	
	//Derived population characteristics
	private StudyCharacteristicHolder d_ageHolder;
	private StudyCharacteristicHolder d_genderHolder;
	
	public StudyPresentationModel(Study s) {
		super(s);
		
		d_armsHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.ARMS) {
			@Override
			protected Object getNewValue() {
				return getBean().getArms().size();
			}
		};
		d_doseHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.DOSING) {
			@Override
			protected Object getNewValue() {
				Dosing dose = DerivedStudyCharacteristic.Dosing.FIXED;
				for (Arm pg : getBean().getArms()) {
					if (pg.getDose() != null)
						if (pg.getDose() instanceof FlexibleDose)
							dose = DerivedStudyCharacteristic.Dosing.FLEXIBLE; 
				}
				return dose;
			}			
		};
		d_drugHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.DRUGS) {
			@Override
			protected Object getNewValue() {
				return getBean().getDrugs();				
			}
		};
		d_sizeHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.STUDYSIZE) {
			@Override
			protected Object getNewValue() {
				return getBean().getSampleSize();				
			}
		};
		
		d_genderHolder = new PopulationCharacteristicHolder(s, PopulationCharacteristic.GENDER);
		d_ageHolder = new PopulationCharacteristicHolder(s, PopulationCharacteristic.AGE);		
	}
	
	public StudyCharacteristicHolder getCharacteristicModel(Characteristic c) {
		if (c.equals(DerivedStudyCharacteristic.DOSING)) {
			return d_doseHolder;
		} else if (c.equals(DerivedStudyCharacteristic.DRUGS)) {
			return d_drugHolder;
		} else if (c.equals(DerivedStudyCharacteristic.STUDYSIZE)) {
			return d_sizeHolder;
		} else if (c.equals(DerivedStudyCharacteristic.ARMS)) {
			return d_armsHolder;
		} else if (c.equals(PopulationCharacteristic.GENDER)) {
			return d_genderHolder;
		} else if (c.equals(PopulationCharacteristic.AGE)) {
			return d_ageHolder;
		} else {
			return new StudyCharacteristicHolder(getBean(), c);
		}
	}
		
	public boolean isStudyFinished() {
		Object status = getBean().getCharacteristics().get(BasicStudyCharacteristic.STATUS);
		if (status != null) {
			return status.equals(BasicStudyCharacteristic.Status.FINISHED);
		}
		return false;
	}
	
	private class PopulationCharacteristicHolder extends ListeningCharacteristicHolder {
		public PopulationCharacteristicHolder(Study study, PopulationCharacteristic characteristic) {
			super(study, characteristic);
		}

		@Override
		protected Object getNewValue() {
			if (!getCharacteristic().getValueType().equals(FrequencyMeasurement.class)){
				return "NON-FREQUENCY STUDY CHARACTERISTICS NOT IMPLEMENTED";				
			}
			FrequencyMeasurement freq = null;
			for (Arm a : getBean().getArms()){
				FrequencyMeasurement val = (FrequencyMeasurement) a.getCharacteristic(getCharacteristic());
				if (val == null) {
					return "Cannot derive: arms without frequencies present";
				}
				if (freq == null) {
					freq = val.deepCopy();
				} else {
					freq.add(val);
				}
			}
			return freq;
		}
	}
	
	
	private abstract class ListeningCharacteristicHolder extends StudyCharacteristicHolder implements PropertyChangeListener {

		public ListeningCharacteristicHolder(Study study, Characteristic characteristic) {
			super(study, characteristic);
			study.addPropertyChangeListener(this);
			for (Arm p : study.getArms()) {
				p.addPropertyChangeListener(this);
			}
		}
		
		protected abstract Object getNewValue();
		
		@Override
		public Object getValue() {
			return getNewValue();
		}

		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getSource() == d_study) {
				if (evt.getPropertyName().equals(Study.PROPERTY_ARMS)) {
					for (Arm p : d_study.getArms()) {
						p.addPropertyChangeListener(this);
					}
				} else {
					return;
				}
			} 
			firePropertyChange("value", null, getNewValue());
		}
	}
}
