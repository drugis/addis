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

package org.drugis.addis.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a meta-analysis over all common drugs within the list of studies.
 * Current assumptions: each study has max one patient group for each drug;
 * every Measurement is a RateMeasurement.
 */
public class MetaAnalysis implements Serializable {
	private static final long serialVersionUID = 3621813375995564247L;
	private List<Study> d_studies;
	private Set<Drug> d_drugs;
	private Endpoint d_endpoint;
	
	public MetaAnalysis(Endpoint endpoint, List<Study> studies) throws IllegalArgumentException {
		validate(endpoint, studies);
		
		d_endpoint = endpoint;
		d_studies = studies;
		d_drugs = findCommonDrugs();
	}
	
	public MetaAnalysis(Endpoint endpoint, List<Study> studies, Drug firstDrug, Drug secondDrug)
	throws IllegalArgumentException {
		validate(endpoint, studies);
		
		d_endpoint = endpoint;
		d_studies = studies;
		
		Set<Drug> drugs = new HashSet<Drug>();
		drugs.add(firstDrug);
		drugs.add(secondDrug);
		validate(studies, drugs);
		d_drugs = drugs;
	}

	public Set<Drug> getDrugs() {
		return d_drugs;
	}
	
	public Endpoint getEndpoint() {
		return d_endpoint;
	}

	/**
	 * 
	 * @param study A study contained in getStudies()
	 * @param drug A drug contained in getDrugs()
	 * @return The measurement from Study on Drug
	 */
	public Measurement getMeasurement(Study study, Drug drug) {
		for (PatientGroup g : study.getPatientGroups()) {
			if (g.getDrug().equals(drug)) {
				return study.getMeasurement(getEndpoint(), g);
			}
		}
		return null;
	}
	
	public Measurement getPooledMeasurement(Drug drug) {
		List<RateMeasurement> measurements = new ArrayList<RateMeasurement>();
		for (Study s : d_studies) {			
			for (PatientGroup g : s.getPatientGroups()) {
				if (g.getDrug().equals(drug)) {
					measurements.add((RateMeasurement) s.getMeasurement(getEndpoint(), g));	
				}
			}
		}
		return new PooledRateMeasurement(measurements);
	}
	
	public List<Study> getStudies() {
		return d_studies;
	}
	
	@Override
	public int hashCode() {
		int hash = 1;
		hash = 31 * hash + getEndpoint().hashCode();
		hash = 31 * hash + new HashSet<Study>(getStudies()).hashCode();
		return hash;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof MetaAnalysis) {
			MetaAnalysis other = (MetaAnalysis)o;
			if (other.getStudies().size() != getStudies().size()) {
				return false;
			}
			return getStudies().containsAll(other.getStudies()) && 
					getEndpoint().equals(other.getEndpoint());
		}
		return false;
	}
	
	private Set<Drug> findCommonDrugs() {
		Set<Drug> drugs = d_studies.get(0).getDrugs();
		for (Study s : d_studies) {
			drugs.retainAll(s.getDrugs());
		}
		return drugs;
	}
	
	private void validate(Endpoint endpoint, List<Study> studies) {
		Study s0 = studies.get(0);
		for (Study s : studies) {
			if (!s.getEndpoints().contains(endpoint)) {
				throw new IllegalArgumentException("Study " + s + " does not measure " + endpoint);
			}
			if (!s0.getCharacteristic(StudyCharacteristic.INDICATION).equals(
					s.getCharacteristic(StudyCharacteristic.INDICATION))) {
				throw new IllegalArgumentException("All studies should have same Indication");
			}
		}
	}
		
	private void validate(List<Study> studies, Set<Drug> drugs) {
		for (Study s : studies) {
			if (!s.getDrugs().containsAll(drugs)) {
				throw new IllegalArgumentException("Study " + s + " does not measure all requested drugs");
			}
		}
	}

	public Indication getIndication() {
		return (Indication)d_studies.get(0).getCharacteristic(StudyCharacteristic.INDICATION);
	}
}