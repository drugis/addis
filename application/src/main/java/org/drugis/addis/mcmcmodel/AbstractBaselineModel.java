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

package org.drugis.addis.mcmcmodel;

import gov.lanl.yadas.ArgumentMaker;
import gov.lanl.yadas.BasicMCMCBond;
import gov.lanl.yadas.ConstantArgument;
import gov.lanl.yadas.Gaussian;
import gov.lanl.yadas.GroupArgument;
import gov.lanl.yadas.IdentityArgument;
import gov.lanl.yadas.MCMCParameter;
import gov.lanl.yadas.MCMCUpdate;
import gov.lanl.yadas.Uniform;
import gov.lanl.yadas.UpdateTuner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.common.threading.AbstractIterativeComputation;
import org.drugis.common.threading.IterativeTask;
import org.drugis.common.threading.SimpleSuspendableTask;
import org.drugis.common.threading.SimpleTask;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.activity.ActivityModel;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.activity.DirectTransition;
import org.drugis.common.threading.activity.Transition;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.NormalSummary;
import org.drugis.mtc.yadas.ParameterWriter;
import org.drugis.mtc.yadas.YadasResults;

abstract public class AbstractBaselineModel<T extends Measurement> implements MCMCModel {

	public abstract Distribution getResult();

	private int d_tuningIter = 20000;
	private int d_simulationIter = 50000;
	private int d_reportingInterval = 100;
	protected List<T> d_measurements;
	private List<MCMCUpdate> d_updates;
	private ParameterWriter d_muWriter;
	private YadasResults d_results = new YadasResults();
	private Parameter d_muParam = new Parameter() {
		public String getName() {
			return("mu");
		}
	};
	private NormalSummary d_summary;

	private SimpleTask d_buildModelPhase = new SimpleSuspendableTask(new Runnable() {
		public void run() {
			buildModel();
		}
	}, "building model");
	
	private IterativeTask d_tuningPhase = new IterativeTask(new AbstractIterativeComputation(d_tuningIter) {
		@Override
		public void doStep() {
			update();
		}
	}, "Tuning");
	
	private IterativeTask d_simulationPhase = new IterativeTask(new AbstractIterativeComputation(d_simulationIter) {
		public void doStep() {
			update();
			output();
		}
	}, "Simulation");
	
	private SimpleTask d_calculateResultsPhase = new SimpleSuspendableTask(new Runnable() {
		public void run() {
			d_results.simulationFinished();
		}
	}, "Calculating summaries");
	
	private ActivityTask d_activityTask;
	
	public AbstractBaselineModel(List<T> measurements) {
		d_results.setDirectParameters(Collections.singletonList(d_muParam));
		d_summary = new NormalSummary(d_results, d_muParam);
		d_measurements = measurements;
		d_tuningPhase.setReportingInterval(d_reportingInterval);
		d_simulationPhase.setReportingInterval(d_reportingInterval);
		List<Transition> transitions = new ArrayList<Transition>();
		transitions.add(new DirectTransition(d_buildModelPhase, d_tuningPhase));
		transitions.add(new DirectTransition(d_tuningPhase, d_simulationPhase));
		transitions.add(new DirectTransition(d_simulationPhase, d_calculateResultsPhase));
		d_activityTask = new ActivityTask(
				new ActivityModel(d_buildModelPhase, d_calculateResultsPhase, transitions), 
				"MCMC model");
	}

	public ActivityTask getActivityTask() {
		return d_activityTask;
	}
	
	Task getBuildModelPhase() {
		return d_buildModelPhase;
	}
	
	Task getBurnInPhase() {
		return d_tuningPhase;
	}
	
	Task getSimulationPhase() {
		return d_simulationPhase;
	}

	private void output() {
		d_muWriter.output();
	}

	private void update() {
		for (MCMCUpdate u : d_updates) {
			u.update();
		}
	}

	public boolean isReady() {
		return d_simulationPhase.isFinished();
	}

	public int getBurnInIterations() {
		return d_tuningIter;
	}

	public int getSimulationIterations() {
		return d_simulationIter;
	}

	public void setBurnInIterations(int it) {
		d_tuningIter = it;
	}

	public void setSimulationIterations(int it) {
		d_simulationIter = it;
	}

	protected int[] intArray(int val) {
		int[] arr = new int[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = val;
		}
		return arr;
	}

	protected double[] doubleArray(double val) {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = val;
		}
		return arr;
	}

	protected abstract double getStdDevPrior();

	protected abstract void createDataBond(MCMCParameter studyMu);

	protected void buildModel() {
		MCMCParameter studyMu = new MCMCParameter(doubleArray(0.0), doubleArray(0.1), null);
		MCMCParameter mu = new MCMCParameter(new double[] {0.0}, new double[] {0.1}, null);
		d_results.setNumberOfIterations(d_simulationIter);
		d_results.setNumberOfChains(1);
		d_muWriter = d_results.getParameterWriter(d_muParam, 0, mu, 0);
		MCMCParameter sd = new MCMCParameter(new double[] {0.25}, new double[] {0.1}, null);
	
		// data bond
		createDataBond(studyMu);
		
		// studyMu bond
		new BasicMCMCBond(new MCMCParameter[] {studyMu, mu, sd},
				new ArgumentMaker[] {
					new IdentityArgument(0),
					new GroupArgument(1, intArray(0)),
					new GroupArgument(2, intArray(0))
				}, new Gaussian());
	
		// priors
		new BasicMCMCBond(new MCMCParameter[] {mu},
				new ArgumentMaker[] {
					new IdentityArgument(0),
					new ConstantArgument(0.0),
					new ConstantArgument(Math.sqrt(1000))
				}, new Gaussian());
		new BasicMCMCBond(new MCMCParameter[] {sd},
				new ArgumentMaker[] {
					new IdentityArgument(0),
					new ConstantArgument(0.0),
					new ConstantArgument(getStdDevPrior()) // FIXME
				}, new Uniform());
		
		List<MCMCParameter> parameters = new ArrayList<MCMCParameter>();
		parameters.add(studyMu);
		parameters.add(mu);
		parameters.add(sd);
		
		d_updates = new ArrayList<MCMCUpdate>();
		for (MCMCParameter param : parameters) {
			d_updates.add(new UpdateTuner(param, d_tuningIter / 50, 30, 1, Math.exp(-1)));
		}
	}
	
	public MCMCResults getResults() {
		return d_results;
	}
	
	public NormalSummary getSummary() {
		return d_summary;
	}
}
