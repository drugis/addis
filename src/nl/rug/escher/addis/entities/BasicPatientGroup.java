/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package nl.rug.escher.addis.entities;

import java.util.ArrayList;
import java.util.List;

import com.jgoodies.binding.beans.Model;

public class BasicPatientGroup extends Model implements MutablePatientGroup {
	private static final long serialVersionUID = -2092185548220089471L;
	private BasicStudy d_study;
	private Integer d_size;
	private Drug d_drug;
	private Dose d_dose;
	private List<BasicMeasurement> d_measurements = new ArrayList<BasicMeasurement>();
	
	public BasicPatientGroup(BasicStudy study, Drug drug, Dose dose, int size,
			List<BasicMeasurement> measurements) {
		d_study = study;
		d_drug = drug;
		d_dose = dose;
		d_size = size;
		d_measurements.addAll(measurements);
	}
	
	public BasicPatientGroup(BasicStudy study, Drug drug, Dose dose, int size) {
		d_study = study;
		d_drug = drug;
		d_dose = dose;
		d_size = size;
	}
	
	public BasicStudy getStudy() {
		return d_study;
	}
	
	public void setStudy(BasicStudy study) {
		BasicStudy oldVal = d_study;
		d_study = study;
		firePropertyChange(PROPERTY_STUDY, oldVal, d_study);
	}
	
	public Drug getDrug() {
		return d_drug;
	}
	
	public void setDrug(Drug drug) {
		String oldLabel = getLabel();
		Drug oldVal = d_drug;
		d_drug = drug;
		firePropertyChange(PROPERTY_DRUG, oldVal, d_drug);
		firePropertyChange(PROPERTY_LABEL, oldLabel, getLabel());
	}
	
	public Dose getDose() {
		return d_dose;
	}
	
	public void setDose(Dose dose) {
		String oldLabel = getLabel();
		Dose oldVal = d_dose;
		d_dose = dose;
		firePropertyChange(PROPERTY_DOSE, oldVal, d_dose);
		firePropertyChange(PROPERTY_LABEL, oldLabel, getLabel());
	}

	public List<BasicMeasurement> getMeasurements() {
		return d_measurements;
	}

	public void setMeasurements(List<BasicMeasurement> measurements) {
		List<BasicMeasurement> oldVal = d_measurements;
		d_measurements = measurements;
		firePropertyChange(PROPERTY_MEASUREMENTS, oldVal, d_measurements);
	}
	
	public void addMeasurement(BasicMeasurement m) {
		List<BasicMeasurement> newVal = new ArrayList<BasicMeasurement>(d_measurements);
		newVal.add(m);
		setMeasurements(newVal);
	}
	
	/**
	 * Get Measurement by Endpoint.
	 * @param endpoint Endpoint to get measurement for.
	 * @return Measurement if Endpoint is measured, null otherwise.
	 */
	public BasicMeasurement getMeasurement(Endpoint endpoint) {
		for (BasicMeasurement m : d_measurements) {
			if (m.getEndpoint().equals(endpoint)) {
				return m;
			}
		}
		return null;
	}
	
	public String getLabel() {
		if (d_drug == null || d_dose == null) {
			return "INCOMPLETE";
		}
		return d_drug.toString() + " " + d_dose.toString();
	}
	
	@Override
	public String toString() {
		return "PatientGroup(" + d_drug + ", " + d_dose + ", " + d_size + ")";
	}

	public Integer getSize() {
		return d_size;
	}

	public void setSize(Integer size) {
		Integer oldVal = d_size;
		d_size = size;
		firePropertyChange(PROPERTY_SIZE, oldVal, d_size);
	}
}
