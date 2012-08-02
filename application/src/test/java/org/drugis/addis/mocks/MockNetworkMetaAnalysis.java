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
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.analysis.NetworkBuilderFactory;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.mtcwrapper.ConsistencyWrapper;
import org.drugis.addis.entities.mtcwrapper.InconsistencyWrapper;
import org.drugis.addis.entities.mtcwrapper.SimulationConsistencyWrapper;
import org.drugis.addis.entities.mtcwrapper.SimulationInconsistencyWrapper;
import org.drugis.addis.entities.treatment.TreatmentCategorySet;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.model.Treatment;


public class MockNetworkMetaAnalysis extends NetworkMetaAnalysis {
	
	private InconsistencyWrapper d_mockInconsistencyModel;
	private ConsistencyWrapper d_mockConsistencyModel;

	public MockNetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<Study> studies, List<TreatmentCategorySet> drugs,
			Map<Study, Map<TreatmentCategorySet, Arm>> armMap) throws IllegalArgumentException {
		super(name, indication, om, studies, drugs, armMap);
		
		d_builder = NetworkBuilderFactory.createBuilderStub(drugs);
		
		d_mockInconsistencyModel = new SimulationInconsistencyWrapper(d_builder, MockInconsistencyModel.buildMockSimulationInconsistencyModel(toTreatments(drugs)));
		d_mockConsistencyModel = new SimulationConsistencyWrapper(d_builder, MockConsistencyModel.buildMockSimulationConsistencyModel(toTreatments(drugs)), drugs);

	}

	private List<Treatment> toTreatments(List<TreatmentCategorySet> drugs) {
		List<Treatment> ts = new ArrayList<Treatment>();
		for (TreatmentCategorySet d : drugs) {
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
	
	public void run() throws InterruptedException {
		List<Task> tasks = new ArrayList<Task>();
		if (!getConsistencyModel().getModel().isReady()) {
			tasks.add(getConsistencyModel().getModel().getActivityTask());
		}
		if (!getInconsistencyModel().getModel().isReady()) {
			tasks.add(getInconsistencyModel().getModel().getActivityTask());
		}
		
		for (Task task : tasks) {
			TaskUtil.run(task);
		}
		firePropertyChange("fasrt", false, true);
	}
	
	@Override
	public NetworkBuilder<TreatmentCategorySet> getBuilder() {
		return d_builder;
	}
}
