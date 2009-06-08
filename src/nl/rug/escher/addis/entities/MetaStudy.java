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
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MetaStudy extends AbstractStudy {
	
	private static final long serialVersionUID = 4551624355872585225L;
	private MetaAnalysis d_analysis;

	public MetaStudy(String id, MetaAnalysis analysis) {
		super(id);
		d_analysis = analysis;
	}
	
	public MetaAnalysis getAnalysis() {
		return d_analysis;
	}

	public Set<Drug> getDrugs() {
		return d_analysis.getDrugs();
	}

	public List<Endpoint> getEndpoints() {
		return Collections.singletonList(d_analysis.getEndpoint());
	}

	public List<PatientGroup> getPatientGroups() {
		List<PatientGroup> l = new ArrayList<PatientGroup>();

		for (Drug d : d_analysis.getDrugs()) {
			l.add(new PooledPatientGroup(this, d));
		}
		
		return l;
	}

}
