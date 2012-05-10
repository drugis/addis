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
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.analysis.models.InconsistencyWrapper;
import org.drugis.addis.entities.analysis.models.SimulationInconsistencyModel;
import org.drugis.common.threading.SimpleSuspendableTask;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.activity.ActivityModel;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.activity.DirectTransition;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.parameterization.InconsistencyParameter;
import org.drugis.mtc.yadas.YadasResults;

public class MockInconsistencyModel  extends SimulationInconsistencyModel implements InconsistencyWrapper {

	boolean d_ready = false;
	private ActivityTask d_task;
	private YadasResults d_results;
	
	private static final int BURNIN_ITER = 1000;
	private static final int SIMULATION_ITER = 10000;

	public MockInconsistencyModel() {
		super(null); // FIXME!!!
		Task start = new SimpleSuspendableTask(new Runnable() { public void run() {} });
		Task end = new SimpleSuspendableTask(new Runnable() { public void run() { finished(); } });
		d_task = new ActivityTask(new ActivityModel(start, end, 
				Collections.singleton(new DirectTransition(start, end))));
		d_results = new YadasResults();
		d_results.setNumberOfIterations(SIMULATION_ITER);
		d_results.setNumberOfChains(1);
		d_results.setDirectParameters(getInconsistencyFactors());
	}

	public List<Parameter> getInconsistencyFactors() {
		List<Treatment> cycle = new ArrayList<Treatment>();
		cycle.add(new Treatment("Fluoxetine"));
		cycle.add(new Treatment("Sertraline"));
		cycle.add(new Treatment("Paroxetine"));
		cycle.add(new Treatment("Fluoxetine"));

		List<Parameter> inFac = new ArrayList<Parameter>();
		inFac.add(new InconsistencyParameter(cycle));

		return inFac;
	}

	public Parameter getRelativeEffect(Treatment base, Treatment subj) {
		return null;
	}

	public boolean isReady() {
		return d_task.isFinished();
	}

	public int getBurnInIterations() {
		return BURNIN_ITER;
	}

	public int getSimulationIterations() {
		return SIMULATION_ITER;
	}

	public void setBurnInIterations(int it) {
	}

	public void setSimulationIterations(int it) {
	}

	public ActivityTask getActivityTask() {
		return d_task;
	}

	public Parameter getInconsistencyVariance() {
		return null;
	}

	public Parameter getRandomEffectsVariance() {
		return null;
	}

	public MCMCResults getResults() {
		return d_results;
	}
	
	protected void finished() {
		d_results.simulationFinished();
	}
}