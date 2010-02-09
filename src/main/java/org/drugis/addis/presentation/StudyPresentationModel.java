package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

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
	private StudyCharacteristicHolder d_indicationHolder;
	private PresentationModelFactory d_pmf;
	
	private Map<Characteristic, StudyCharacteristicHolder> d_characteristicModelMap;
	
	public StudyPresentationModel(Study s, PresentationModelFactory pmf) {
		super(s);
		
		d_characteristicModelMap = new HashMap<Characteristic, StudyCharacteristicHolder>();
		d_pmf = pmf;
		
		d_armsHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.ARMS) {
			@Override
			protected Object getNewValue() {
				return getBean().getArms().size();
			}
		};
		addToCharMap(d_armsHolder);
		
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
		addToCharMap(d_doseHolder);
		
		d_drugHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.DRUGS) {
			@Override
			protected Object getNewValue() {
				return getBean().getDrugs();				
			}
		};
		addToCharMap(d_drugHolder);
		
		d_sizeHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.STUDYSIZE) {
			@Override
			protected Object getNewValue() {
				return getBean().getSampleSize();				
			}
		};
		addToCharMap(d_sizeHolder);
		
		d_indicationHolder = new ListeningCharacteristicHolder(s, DerivedStudyCharacteristic.INDICATION) {
			@Override
			protected Object getNewValue() {
				return getBean().getIndication();				
			}
		};
		addToCharMap(d_indicationHolder);
	}

	private void addToCharMap(StudyCharacteristicHolder holder) {
		d_characteristicModelMap.put(holder.getCharacteristic(), holder);
	}
	
	public StudyCharacteristicHolder getCharacteristicModel(Characteristic c) {
		StudyCharacteristicHolder holder = d_characteristicModelMap.get(c);
		return holder != null ? holder : new StudyCharacteristicHolder(getBean(), c);
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

	public List<Variable> getPopulationCharacteristics() {
		return getBean().getPopulationCharacteristics();
	}
	
	public LabeledPresentationModel getCharacteristicModel(BasicStudyCharacteristic c) {
		if (getBean().getCharacteristic(c) != null) {
			return d_pmf.getLabeledModel(getBean().getCharacteristic(c));
		}
		return null;
	}
	
	public List<OutcomeMeasure> getEndpoints() {
		List<OutcomeMeasure> s = new ArrayList<OutcomeMeasure>();
		for (OutcomeMeasure m : getBean().getOutcomeMeasures()) {
			if (m instanceof Endpoint) {
				s.add(m);
			}
		}
		return s;
	}
	
	public List<OutcomeMeasure> getAdes() {
		List<OutcomeMeasure> s = new ArrayList<OutcomeMeasure>();
		for (OutcomeMeasure m : getBean().getOutcomeMeasures()) {
			if (m instanceof AdverseDrugEvent) {
				s.add(m);
			}
		}
		return s;
	}	
	
	public String getNoteText(Object key) {
		return getBean().getNote(key).getText();
	}

	public PopulationCharTableModel getPopulationCharTableModel() {
		return new PopulationCharTableModel(getBean(), d_pmf);
	}

	public TableModel getEndpointTableModel() {
		return new MeasurementTableModel(getBean(), d_pmf, Endpoint.class);
	}
	
	public TableModel getAdverseEventTableModel() {
		return new MeasurementTableModel(getBean(), d_pmf, AdverseDrugEvent.class);
	}
}
