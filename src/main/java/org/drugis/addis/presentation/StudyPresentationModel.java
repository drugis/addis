package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.DerivedStudyCharacteristic;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.entities.DerivedStudyCharacteristic.Dosing;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class StudyPresentationModel extends PresentationModel<Study> {
	
	private CharacteristicHolder d_doseHolder;
	private CharacteristicHolder d_drugHolder;
	private CharacteristicHolder d_sizeHolder;
	
	public StudyPresentationModel(Study s) {
		super(s);
		
		d_doseHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.DOSING) {
			@Override
			protected Object getNewValue() {
				Dosing dose = DerivedStudyCharacteristic.Dosing.FIXED;
				for (PatientGroup pg : getBean().getPatientGroups()) {
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
				return getBean().getDrugs().toString();				
			}
		};
		d_sizeHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.STUDYSIZE) {
			@Override
			protected Object getNewValue() {
				return getBean().getSampleSize();				
			}
		};
	}
	
	public CharacteristicHolder getCharacteristicModel(StudyCharacteristic c) {
		if (c.equals(DerivedStudyCharacteristic.DOSING)) {
			return d_doseHolder;
		} else if (c.equals(DerivedStudyCharacteristic.DRUGS)) {
			return d_drugHolder;
		} else if (c.equals(DerivedStudyCharacteristic.STUDYSIZE)) {
			return d_sizeHolder;
		} else {
			return new CharacteristicHolder(getBean(), c);
		}
	}
	
	public boolean isStudyFinished() {
		Object status = getBean().getCharacteristics().get(StudyCharacteristic.STATUS);
		if (status != null) {
			return status.equals(StudyCharacteristic.Status.FINISHED);
		}
		return false;
	}
	
	private abstract class ListeningCharacteristicHolder extends CharacteristicHolder implements PropertyChangeListener {

		public ListeningCharacteristicHolder(Study study, StudyCharacteristic characteristic) {
			super(study, characteristic);
			study.addPropertyChangeListener(this);
			for (PatientGroup p : study.getPatientGroups()) {
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
				if (evt.getPropertyName().equals(Study.PROPERTY_PATIENTGROUPS)) {
					for (PatientGroup p : d_study.getPatientGroups()) {
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
