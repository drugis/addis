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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetaStudy extends AbstractStudy {
	
	private static final long serialVersionUID = 4551624355872585225L;
	private MetaAnalysis d_analysis;
	private List<PatientGroup> d_patientGroups;
	private List<Endpoint> d_additionalEndpoints = new ArrayList<Endpoint>();

	public MetaStudy(String id, MetaAnalysis analysis) {
		super(id);
		d_analysis = analysis;
		initPatientGroups();
	}
	
	private void initPatientGroups() {
		d_patientGroups = new ArrayList<PatientGroup>();
		for (Drug d : d_analysis.getDrugs()) {
			PooledPatientGroup pg = new PooledPatientGroup(this, d);
			d_patientGroups.add(pg);
			d_measurements.put(new MeasurementKey(d_analysis.getEndpoint(), pg),
					d_analysis.getPooledMeasurement(d));
		}
	}

	public MetaAnalysis getAnalysis() {
		return d_analysis;
	}

	public Set<Drug> getDrugs() {
		return d_analysis.getDrugs();
	}

	public List<Endpoint> getEndpoints() {
		List<Endpoint> points = new ArrayList<Endpoint>();
		points.add(d_analysis.getEndpoint());		
		points.addAll(d_additionalEndpoints);
		return points;
	}

	public List<PatientGroup> getPatientGroups() {
		return d_patientGroups;
	}

	public Set<Entity> getDependencies() {
		List<Study> studies = getAnalysis().getStudies();
		Set<Entity> deps = new HashSet<Entity>(studies);
		for (Study s : studies) {
			deps.addAll(s.getDependencies());
		}
		return deps;
	}

	public void addEndpoint(Endpoint e) {
		d_additionalEndpoints.add(e);
	}

	@Override
	public void setMeasurement(Endpoint e, PatientGroup g, Measurement m) {
		if (d_analysis.getEndpoint().equals(e)) {
			throw new IllegalArgumentException("Cannot set measurement on meta-analysis endpoint");
		}
		super.setMeasurement(e, g, m);
	}
}
