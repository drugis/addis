package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.entities.AdverseDrugEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.DerivedStudyCharacteristic;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.DerivedStudyCharacteristic.Dosing;

import com.jgoodies.binding.PresentationModel;

@SuppressWarnings("serial")
public class StudyPresentationModel extends PresentationModel<Study> {
	private StudyCharacteristicHolder d_armsHolder;
	private StudyCharacteristicHolder d_doseHolder;
	private StudyCharacteristicHolder d_drugHolder;
	private StudyCharacteristicHolder d_sizeHolder;
	private PresentationModelFactory d_pmf;
	
	public StudyPresentationModel(Study s, PresentationModelFactory pmf) {
		super(s);
		
		d_pmf = pmf;
		
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

	public int getArmCount() {
		return getBean().getArms().size();
	}
	
	public List<BasicArmPresentation> getArms() {
		List<BasicArmPresentation> list = new ArrayList<BasicArmPresentation>();
		for (Arm arm : getBean().getArms()) {
			list.add((BasicArmPresentation) d_pmf.getModel(arm));
		}
		return list;
	}

	public int getPopulationCharacteristicCount() {
		return getPopulationCharacteristics().size();
	}

	public Set<Variable> getPopulationCharacteristics() {
		Set<Variable> vars = new HashSet<Variable>(getBean().getPopulationCharacteristics().keySet());
		for (Arm a : getBean().getArms()) {
			vars.addAll(a.getPopulationCharacteristics().keySet());
		}
		return vars;
	}
	
	public LabeledPresentationModel getCharacteristicModel(Variable v) {
		if (getBean().getPopulationCharacteristic(v) != null) {
			return d_pmf.getLabeledModel(getBean().getPopulationCharacteristic(v));
		}
		return null;
	}
	
	public Set<OutcomeMeasure> getEndpoints() {
		Set<OutcomeMeasure> s = new HashSet<OutcomeMeasure>();
		for (OutcomeMeasure m : getBean().getOutcomeMeasures()) {
			if (m instanceof Endpoint) {
				s.add(m);
			}
		}
		return s;
	}
	
	public Set<OutcomeMeasure> getAdes() {
		Set<OutcomeMeasure> s = new HashSet<OutcomeMeasure>();
		for (OutcomeMeasure m : getBean().getOutcomeMeasures()) {
			if (m instanceof AdverseDrugEvent) {
				s.add(m);
			}
		}
		return s;
	}
	
}
