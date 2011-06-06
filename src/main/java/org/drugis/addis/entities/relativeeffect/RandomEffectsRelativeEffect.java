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

package org.drugis.addis.entities.relativeeffect;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Measurement;
import org.drugis.common.stat.EstimateWithPrecision;
import org.drugis.mtc.util.DerSimonianLairdPooling;

public class RandomEffectsRelativeEffect extends AbstractRelativeEffect<Measurement> implements RandomEffectMetaAnalysisRelativeEffect<Measurement> {
	
	private abstract static class DerSimonianLairdComputations {
		private DerSimonianLairdPooling d_nested;
		
		public DerSimonianLairdComputations(List<Distribution> distributions) {
			if (distributions.isEmpty())
				throw new IllegalStateException("Cannot calculate RandomEffectMetaAnalysis without any relative effects.");
	
			d_nested = new DerSimonianLairdPooling(getMu(distributions), getSigma(distributions));
		}
		
		public double getHeterogeneity() {
			return d_nested.getHeterogeneity();
		}
		
		public double getHeterogeneityI2() {
			return d_nested.getHeterogeneityTestStatistic() * 100;
		}
		
		public Distribution getDistribution() {
			return getPooledDistribution(d_nested.getPooled());
		}
		
		protected abstract Distribution getPooledDistribution(EstimateWithPrecision estimate);
		protected abstract double getMu(Distribution d);
		protected abstract double getSigma(Distribution d);

		private double[] getSigma(List<Distribution> distributions) {
			double[] val = new double[distributions.size()];
			for (int i = 0; i < distributions.size(); ++i) {
				val[i] = getSigma(distributions.get(i));
			}
			return val;
		}

		private double[] getMu(List<Distribution> distributions) {
			double[] val = new double[distributions.size()];
			for (int i = 0; i < distributions.size(); ++i) {
				val[i] = getMu(distributions.get(i));
			}
			return val;
		}
	}
	
	private class LinDSLComputations extends DerSimonianLairdComputations {
		public LinDSLComputations(List<Distribution> distributions) {
			super(distributions);
		}
		
		@Override
		public double getMu(Distribution d) {
			return ((TransformedStudentTBase)d).getMu();
		}
		
		@Override
		public double getSigma(Distribution d) {
			return ((TransformedStudentTBase)d).getSigma();
		}
		
		@Override
		protected Distribution getPooledDistribution(EstimateWithPrecision estimate) {
			return new Gaussian(estimate.getPointEstimate(), estimate.getStandardError());
		}
	}
	
	private class LogDSLComputations extends DerSimonianLairdComputations {
		public LogDSLComputations(List<Distribution> distributions) {
			super(distributions);
		}
		
		@Override
		public double getMu(Distribution d) {
			return ((TransformedLogStudentT)d).getMu();
		}
		
		@Override
		public double getSigma(Distribution d) {
			return ((TransformedLogStudentT)d).getSigma();
		}
		
		@Override
		protected Distribution getPooledDistribution(EstimateWithPrecision estimate) {
			return new LogGaussian(estimate.getPointEstimate(), estimate.getStandardError());
		}
	}

	private DerSimonianLairdComputations d_results;

	public RandomEffectsRelativeEffect(List<BasicRelativeEffect<? extends Measurement>> componentEffects) {
		switch (componentEffects.get(0).getAxisType()) {
		case LINEAR:
			d_results = new LinDSLComputations(getDistributions(componentEffects));
			break;
		case LOGARITHMIC:
			d_results = new LogDSLComputations(getDistributions(componentEffects));
			break;
		default:
			throw new IllegalStateException("Unknown AxisType " + componentEffects.get(0).getAxisType());
		}
	}

	public Distribution getDistribution() {
		return d_results.getDistribution();
	}
	
	public boolean isDefined() {
		return true;
	}

	public double getHeterogeneity() {
		return d_results.getHeterogeneity();
	}
	
	public double getHeterogeneityI2() {
		return d_results.getHeterogeneityI2();
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "(" + getConfidenceInterval().toString() +")";
	}
	
	public String getName() {
		return "Random Effects Relative Effect";
	}

	public static List<Distribution> getDistributions(List<BasicRelativeEffect<?>> res) {
		List<Distribution> dists = new ArrayList<Distribution>();
		for (RelativeEffect<?> re: res) {
			dists.add(re.getDistribution());
		}
		return dists;
	}

	public static List<Distribution> getCorrectedDistributions(List<BasicRelativeEffect<?>> res) {
		List<Distribution> dists = new ArrayList<Distribution>();
		for (RelativeEffect<?> re: res) {
			if(re instanceof BasicRiskDifference) {
				dists.add(((BasicRiskDifference) re).getCorrected().getDistribution());
			} else if(re instanceof BasicOddsRatio) {
				dists.add(((BasicOddsRatio) re).getCorrected().getDistribution());
			} else if(re instanceof BasicRiskRatio) {
				dists.add(((BasicRiskRatio) re).getCorrected().getDistribution());
			} else {
				dists.add(re.getDistribution());
			}
		}
		return dists;
	}

}
