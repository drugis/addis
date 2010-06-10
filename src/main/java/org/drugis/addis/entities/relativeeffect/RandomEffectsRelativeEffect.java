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
			List<Double> adjweights = new ArrayList<Double>();

			// Calculate the weights.
			for (Distribution dist : distributions) {
				weights.add(1D / Math.pow(getSigma(dist),2));
			}
			
			// Calculate needed variables.
			double thetaIV = getThetaIV(weights, distributions);
			d_qIV = getQIV(weights, distributions, thetaIV);
			double tauSquared = getTauSquared(d_qIV, weights);
			
			// Calculated the adjusted Weights.
			for (Distribution dist : distributions) {
				adjweights.add(1 / (Math.pow(getSigma(dist),2) + tauSquared) );
			}
			
			d_thetaDSL = getThetaDL(adjweights, distributions);
			d_SEThetaDSL = getSE_ThetaDL(adjweights);
			
			d_distribution = getPooledDistribution();
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
			double squaredWeightsSum = 0;
			for (int i=0;i<weights.size();i++) {
				squaredWeightsSum += Math.pow(weights.get(i),2);
			}
			
			double num = Q - (k - 1);
			double denum = computeSum(weights) - (squaredWeightsSum / computeSum(weights));
			if (denum == 0) // FIXME denum shouldn't be 0.
				return 0;
			return Math.max(num / denum, 0);
		}
		
		private double getQIV(List<Double> weights, List<Distribution> relEffects, double thetaIV) {
			double sum = 0;
			for (int i = 0; i < weights.size(); ++i) {
				sum += weights.get(i) * Math.pow(getMu(relEffects.get(i)) - thetaIV,2);
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
			return Math.max(0, 100* ((getHeterogeneity() - (d_numRelativeEffects-1)) / getHeterogeneity() ) );
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

	public RandomEffectsRelativeEffect(List<BasicRelativeEffect<? extends Measurement>> componentEffects, int totalSampleSize) {
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
}
