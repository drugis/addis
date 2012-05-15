/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.analysis.models.ConsistencyWrapper;
import org.drugis.addis.entities.analysis.models.InconsistencyWrapper;
import org.drugis.addis.entities.analysis.models.SimulationConsistencyModel;
import org.drugis.addis.entities.analysis.models.SimulationInconsistencyModel;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.mtc.model.Treatment;


public class MockNetworkMetaAnalysis extends NetworkMetaAnalysis {
	
	private InconsistencyWrapper d_mockInconsistencyModel;
	private ConsistencyWrapper d_mockConsistencyModel;

	public MockNetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<Study> studies, List<DrugSet> drugs,
			Map<Study, Map<DrugSet, Arm>> armMap) throws IllegalArgumentException {
		super(name, indication, om, studies, drugs, armMap);
		d_mockInconsistencyModel =  new SimulationInconsistencyModel(getBuilder(), MockInconsistencyModel.buildMockSimulationIconsistencyModel());
		d_mockConsistencyModel = new SimulationConsistencyModel(getBuilder(), MockConsistencyModel.buildMockSimulationConsistencyModel(toTreatments(drugs)), drugs);
	}

	private List<Treatment> toTreatments(List<DrugSet> drugs) {
		List<Treatment> ts = new ArrayList<Treatment>();
		for (DrugSet d : drugs) {
			ts.add(new Treatment(d.getLabel(), d.getLabel()));
		}
		return ts;
	}
	
	@Override
	public InconsistencyWrapper getInconsistencyModel() {
		return d_mockInconsistencyModel;
	}
	
	@Override
	public ConsistencyWrapper getConsistencyModel() {
		return d_mockConsistencyModel;
	}
	
	public void run() {
		List<Task> tasks = new ArrayList<Task>();
		if (!getConsistencyModel().isReady()) {
			tasks.add(getConsistencyModel().getActivityTask());
		}
		if (!getInconsistencyModel().isReady()) {
			tasks.add(getInconsistencyModel().getActivityTask());
		}
		ThreadHandler.getInstance().scheduleTasks(tasks);
	}
	
}
