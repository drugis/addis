/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

public class RandomEffectsRelativeEffect extends AbstractRelativeEffect<Measurement> implements RandomEffectMetaAnalysisRelativeEffect<Measurement> {
	
	private abstract static class DerSimonianLairdComputations {
		
		double d_thetaDSL;
		double d_SEThetaDSL;
		double d_qIV;
		int d_numRelativeEffects;
		Distribution d_distribution;
		
		public DerSimonianLairdComputations(List<Distribution> distributions) {
			if (distributions.isEmpty())
				throw new IllegalStateException("Cannot calculate RandomEffectMetaAnalysis without any relative effects.");
	
			d_numRelativeEffects = distributions.size();

			List<Double> weights = new ArrayList<Double>();
			for (Distribution dist : distributions) {
				weights.add(1D / Math.pow(getSigma(dist), 2));
			}
			
			double thetaIV = getThetaIV(weights, distributions);
			d_qIV = getQIV(weights, distributions, thetaIV);
			
			List<Double> adjweights = calculateAdjustedWeights(distributions, weights);
			
			d_thetaDSL = getThetaDL(adjweights, distributions);
			d_SEThetaDSL = getSE_ThetaDL(adjweights);
			
			d_distribution = getPooledDistribution();
		}

		private List<Double> calculateAdjustedWeights(List<Distribution> distributions, List<Double> weights) {
			if (distributions.size() < 2) { // getTauSquared is NaN if n = 1
				return weights;
			}
			
			final double tauSquared = getTauSquared(d_qIV, weights);
			
			List<Double> adjweights = new ArrayList<Double>();
			for (Distribution dist : distributions) {
				adjweights.add(1D / (Math.pow(getSigma(dist), 2) + tauSquared));
			}
			return adjweights;
		}
		
		private double getSE_ThetaDL(List<Double> adjweights) {
			return 1.0 / (Math.sqrt(computeSum(adjweights)));
		}

		private double getThetaDL(List<Double> adjweights, List<Distribution> relEffects) {
			double numerator = 0;
			for (int i=0; i < adjweights.size(); ++i) {
				numerator += adjweights.get(i) * getMu(relEffects.get(i));
			}
			
			return numerator / computeSum(adjweights);
		}
		
		private double getTauSquared(double Q, List<Double> weights) {
			double k = weights.size();
			double num = Q - (k - 1);
			double denum = computeSum(weights) - (computeSquaredSum(weights) / computeSum(weights));
			return Math.max(num / denum, 0);
		}

		private double computeSquaredSum(List<Double> weights) {
			double squaredWeightsSum = 0;
			for (int i=0;i<weights.size();i++) {
				squaredWeightsSum += Math.pow(weights.get(i), 2);
			}
			return squaredWeightsSum;
		}
		
		private double getQIV(List<Double> weights, List<Distribution> relEffects, double thetaIV) {
			double sum = 0;
			for (int i = 0; i < weights.size(); ++i) {
				sum += weights.get(i) * Math.pow(getMu(relEffects.get(i)) - thetaIV, 2);
			}
			return sum;
		}
		
		private double getThetaIV(List<Double> weights, List<Distribution> relEffects) {
			double sumWeightRatio = 0D;
			for (int i = 0; i < weights.size(); ++i) {
				sumWeightRatio += weights.get(i) * getMu(relEffects.get(i));
			}
			return sumWeightRatio / computeSum(weights);
		}	
		
		protected double computeSum(List<Double> weights) {
			double weightSum = 0;
			for (int i=0; i < weights.size(); ++i) {
				weightSum += weights.get(i);
			}
			return weightSum;
		}
		
		public double getHeterogeneity() {
			return d_qIV;
		}
		
		public double getHeterogeneityI2() {
			return Math.max(0, 100 * ((getHeterogeneity() - (d_numRelativeEffects - 1)) / getHeterogeneity()));
		}
		
		public Distribution getDistribution() {
			return d_distribution;
		}
		
		protected abstract Distribution getPooledDistribution();
		protected abstract double getMu(Distribution d);
		protected abstract double getSigma(Distribution d);
	}
	
	private class LinDSLComputations extends DerSimonianLairdComputations {
		public LinDSLComputations(List<Distribution> distributions) {
			super(distributions);
		}
		
		@Override
		public double getMu(Distribution d) {
			return ((TransformedStudentT)d).getMu();
		}
		
		@Override
		public double getSigma(Distribution d) {
			return ((TransformedStudentT)d).getSigma();
		}
		
		@Override
		public Distribution getPooledDistribution() {
			return new Gaussian(d_thetaDSL, d_SEThetaDSL);
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
		public Distribution getPooledDistribution() {
			return new LogGaussian(d_thetaDSL, d_SEThetaDSL);
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
