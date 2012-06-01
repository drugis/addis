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

package org.drugis.addis.mcmcmodel;

import gov.lanl.yadas.ArgumentMaker;
import gov.lanl.yadas.BasicMCMCBond;
import gov.lanl.yadas.ConstantArgument;
import gov.lanl.yadas.Gaussian;
import gov.lanl.yadas.GroupArgument;
import gov.lanl.yadas.IdentityArgument;
import gov.lanl.yadas.MCMCParameter;
import gov.lanl.yadas.Uniform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.drugis.addis.entities.Measurement;
import org.drugis.common.stat.EstimateWithPrecision;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.NormalSummary;
import org.drugis.mtc.util.DerSimonianLairdPooling;
import org.drugis.mtc.yadas.AbstractYadasModel;

abstract public class AbstractBaselineModel<T extends Measurement> extends AbstractYadasModel {
	protected List<T> d_measurements;
	protected final RandomGenerator d_rng = new JDKRandomGenerator(); 
 
	private Parameter d_muParam = new Parameter() {
		public String getName() {
			return("mu");
		}
		public String toString() { 
			return getName();
		};
	};
	
	private Parameter d_sigmaParam = new Parameter() {
		public String getName() {
			return("sd");
		}
		public String toString() { 
			return getName();
		};
	};
	
	private NormalSummary d_summary;
	
	public AbstractBaselineModel(List<T> measurements) {
		setTuningIterations(5000);
		setSimulationIterations(15000);
		d_results.setDirectParameters(Collections.singletonList(d_muParam));
		d_summary = new NormalSummary(d_results, d_muParam);
		d_measurements = measurements;
	}
	
	public NormalSummary getSummary() {
		return d_summary;
	}

	@Override
	protected List<Parameter> getParameters() {
		return Arrays.asList(d_muParam, d_sigmaParam);
	}
	
	protected double getStandardDeviationPrior() {
		// FIXME: the factor 2 below is rather arbitrary. However, it is required to make
		// the tests pass for network-br. Until baselines can be specified explicitly, it
		// should remain there.
		double maxDev = 0.0;
		for (int i = 0; i < d_measurements.size() - 1; ++i) {
			EstimateWithPrecision e1 = estimateTreatmentEffect(i);
			for (int j = i + 1; j < d_measurements.size(); ++j) {
				EstimateWithPrecision e2 = estimateTreatmentEffect(j);
				maxDev = Math.max(maxDev, Math.abs(e2.getPointEstimate() - e1.getPointEstimate()));
			}
		}
		return 2 * maxDev;
	}
	
	protected abstract EstimateWithPrecision estimateTreatmentEffect(int i);

	protected abstract void createDataBond(MCMCParameter studyMu);
	
	protected double[] initializeStandardDeviation() {
		return new double[] {d_rng.nextDouble() * getStandardDeviationPrior()};
	}

	private double[] initializeMean() {
		List<EstimateWithPrecision> estimates = new ArrayList<EstimateWithPrecision>();
		for (int i = 0; i < d_measurements.size(); ++i) {
			estimates.add(estimateTreatmentEffect(i));
		}
		EstimateWithPrecision pooled = new DerSimonianLairdPooling(estimates).getPooled();
		return new double[] {generate(pooled)};
	}

	private double[] initializeStudyMeans() {
		double[] means = new double[d_measurements.size()];
		for (int i = 0; i < means.length; ++i) {
			final EstimateWithPrecision e = estimateTreatmentEffect(i);
			means[i] = generate(e);
		}
		return means;
	}

	private double generate(final EstimateWithPrecision e) {
		return e.getPointEstimate() + d_rng.nextGaussian() * VARIANCE_SCALING * e.getStandardError();
	}
	
	@Override
	protected void prepareModel() {
	}
	
	@Override
	protected void createChain(int chain) {
		MCMCParameter studyMu = new MCMCParameter(initializeStudyMeans(), doubleArray(0.1, d_measurements.size()), null);
		MCMCParameter mu = new MCMCParameter(initializeMean(), new double[] {0.1}, null);
		MCMCParameter sd = new MCMCParameter(initializeStandardDeviation(), new double[] {0.1}, null);
	
		// data bond
		createDataBond(studyMu);
		
		// studyMu bond
		new BasicMCMCBond(new MCMCParameter[] {studyMu, mu, sd},
				new ArgumentMaker[] {
					new IdentityArgument(0),
					new GroupArgument(1, new int[d_measurements.size()]),
					new GroupArgument(2, new int[d_measurements.size()])
				}, new Gaussian());
	
		// priors
		new BasicMCMCBond(new MCMCParameter[] {mu},
				new ArgumentMaker[] {
					new IdentityArgument(0),
					new ConstantArgument(0.0),
					new ConstantArgument(15 * getStandardDeviationPrior())
				}, new Gaussian());
		new BasicMCMCBond(new MCMCParameter[] {sd},
				new ArgumentMaker[] {
					new IdentityArgument(0),
					new ConstantArgument(0.0),
					new ConstantArgument(getStandardDeviationPrior())
				}, new Uniform());
		
		List<MCMCParameter> parameters = new ArrayList<MCMCParameter>();
		parameters.add(studyMu);
		parameters.add(mu);
		parameters.add(sd);
		
		addTuners(parameters);
		addWriters(Arrays.asList(
				d_results.getParameterWriter(d_muParam, chain, mu, 0),
				d_results.getParameterWriter(d_sigmaParam, chain, sd, 0)));
	}

	protected double[] doubleArray(double val, int size) {
		double[] arr = new double[size];
		Arrays.fill(arr, val);
		return arr;
	}

	protected abstract double getError(int i);
}
