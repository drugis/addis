/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

package org.drugis.addis.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.summary.NormalSummary;


public class MockNetworkMetaAnalysis extends NetworkMetaAnalysis {
	
	private InconsistencyModel d_mockInconsistencyModel;
	private ConsistencyModel d_mockConsistencyModel;

	public MockNetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<Study> studies, List<DrugSet> drugs,
			Map<Study, Map<DrugSet, Arm>> armMap) throws IllegalArgumentException {
		super(name, indication, om, studies, drugs, armMap);
		d_mockInconsistencyModel = new MockInconsistencyModel();
		d_mockConsistencyModel = new MockConsistencyModel(toTreatments(drugs));
		d_normalSummaries.put(d_mockConsistencyModel, new HashMap<Parameter, NormalSummary>());
		d_normalSummaries.put(d_mockInconsistencyModel, new HashMap<Parameter, NormalSummary>());
	}

	private List<Treatment> toTreatments(List<DrugSet> drugs) {
		List<Treatment> ts = new ArrayList<Treatment>();
		for (DrugSet d : drugs) {
			ts.add(new Treatment(d.getLabel()));
		}
		return ts;
	}

	@Override
	public NormalSummary getNormalSummary(MixedTreatmentComparison networkModel, Parameter ip) {
		NormalSummary summary = d_normalSummaries.get(networkModel).get(ip);
		if (summary == null) {
			summary = new MockNormalSummary(networkModel.getResults(), ip);
			d_normalSummaries.get(networkModel).put(ip, summary);
		}
		return summary;
	}

	@Override
	public InconsistencyModel getInconsistencyModel() {
		return d_mockInconsistencyModel;
	}
	
	@Override
	public ConsistencyModel getConsistencyModel() {
		return d_mockConsistencyModel;
	}
}
