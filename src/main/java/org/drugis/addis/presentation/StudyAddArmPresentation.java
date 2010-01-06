package org.drugis.addis.presentation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.common.Interval;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.AbstractValueModel;

public class StudyAddArmPresentation {
	private PresentationModelFactory d_pmf;
	private Study d_study;
	private BasicArmPresentation d_pg;
	private Map<OutcomeMeasure, Measurement> d_measurements = new HashMap<OutcomeMeasure,Measurement>();
	
	public StudyAddArmPresentation(Study study, PresentationModelFactory pmf) {
		d_pmf = pmf;
		d_study = study;
		Arm pg = new Arm(null, new FlexibleDose(new Interval<Double>(0.0, 0.0), SIUnit.MILLIGRAMS_A_DAY), 0);
		d_pg = (BasicArmPresentation)d_pmf.getModel(pg);
		
		for (OutcomeMeasure e : d_study.getOutcomeMeasures()) {
			BasicMeasurement m = e.buildMeasurement(pg);
			if (m instanceof BasicMeasurement) {
				final BasicMeasurement rm = (BasicMeasurement)m;
				getSizeModel().addValueChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent evt) {
						if (rm.getSampleSize().equals(0)) {
							rm.setSampleSize((Integer)evt.getNewValue());
						}
					}
				});
			}
			d_measurements.put(e, m);
		}
	}
	
	public Arm getArm() {
		return d_pg.getBean();
	}
	
	public DosePresentationModel getDoseModel() {
		return d_pg.getDoseModel();
	}
	
	public List<OutcomeMeasure> getOutcomeMeasures(Endpoint.Type type) {
		List<OutcomeMeasure> result = new ArrayList<OutcomeMeasure>();
		for (OutcomeMeasure e : d_study.getOutcomeMeasures()) {
			if (e.getType().equals(type)) {
				result.add(e);
			}
		}
		return result;
	}
	
	public boolean hasEndpoints(Endpoint.Type type) {
		return !getOutcomeMeasures(type).isEmpty();
	}
	
	public PresentationModel<Measurement> getMeasurementModel(OutcomeMeasure e) {
		//return d_pmf.getModel(d_measurements.get(e));
		return new MeasurementPresentationModel(d_measurements.get(e), getSizeModel());
	}
	
	public AbstractValueModel getDrugModel() {
		return d_pg.getModel(Arm.PROPERTY_DRUG);
	}
	
	public AbstractValueModel getSizeModel() {
		return d_pg.getModel(Arm.PROPERTY_SIZE);
	}
	
	public void addToStudy() {
		d_study.addArm((Arm)getArm());
		for (OutcomeMeasure e: d_study.getOutcomeMeasures()) {
			d_study.setMeasurement(e, getArm(), d_measurements.get(e));
		}
	}
}
