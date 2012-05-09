package org.drugis.addis.util.JSMAAintegration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.drugis.common.threading.TaskUtil;
import org.junit.Test;

import fi.smaa.common.RandomUtil;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.CriterionMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.MultivariateGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.PerCriterionMeasurements;
import fi.smaa.jsmaa.model.RelativeGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.RelativeLogitGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.ScaleCriterion;
import fi.smaa.jsmaa.simulator.SMAA2Simulation;

/**
 * Test results of MetaBenefitRisk implementation against results of our published analysis.
 * This test uses MTC results calculated in R (the exact ones used for the paper) to build the model.
 * @see NetworkBenefitRiskIT for an integration test that also runs the MTCs in ADDIS. 
 * @see <a href="http://drugis.org/network-br">network-br paper</a>
 */
public class NetworkBenefitRiskTest extends NetworkBenefitRiskTestBase {
	/**
	 * Test SMAA using measurements derived in R.
	 */
	@Test
	public void testSMAA() throws InterruptedException {
		PerCriterionMeasurements measurements = new PerCriterionMeasurements(Collections.<Criterion>emptyList(), Collections.<Alternative>emptyList());
		SMAAModel model = new SMAAModel("Test", measurements);
		
		model.addAlternative(new Alternative("Placebo"));
		model.addAlternative(new Alternative("Fluoxetine"));
		model.addAlternative(new Alternative("Paroxetine"));
		model.addAlternative(new Alternative("Sertraline"));
		model.addAlternative(new Alternative("Venlafaxine"));
		
		addCriterion(model, measurements, new ScaleCriterion("Diarrhea", false), buildDiarrhea(model.getAlternatives()));
		addCriterion(model, measurements, new ScaleCriterion("Dizziness", false), buildDizziness(model.getAlternatives()));
		addCriterion(model, measurements, new ScaleCriterion("HAM-D Responders", true), buildHAMD(model.getAlternatives()));
		addCriterion(model, measurements, new ScaleCriterion("Headache", false), buildHeadache(model.getAlternatives()));
		addCriterion(model, measurements, new ScaleCriterion("Insomnia", false), buildInsomnia(model.getAlternatives()));
		addCriterion(model, measurements, new ScaleCriterion("Nausea", false), buildNausea(model.getAlternatives()));
		
		// Reorder alternatives
		model.reorderAlternatives(movePlacebo(model.getAlternatives(), 0, 2));
		
		RandomUtil random = RandomUtil.createWithRandomSeed();
		SMAA2Simulation simulation = new SMAA2Simulation(model, random, 10000);
		TaskUtil.run(simulation.getTask());
		
		checkResults(model, simulation, 1.0);
	}

	private void addCriterion(SMAAModel model, PerCriterionMeasurements measurements, final ScaleCriterion c, final CriterionMeasurement m) {
		model.addCriterion(c);
		measurements.setCriterionMeasurement(c, m);
	}

	private CriterionMeasurement buildDiarrhea(final List<Alternative> alternatives) {
		final double mean = -2.189737;
		final double stdDev = 0.2074509;
		final double[] meanVector = new double[] {0.0, 0.60820293,  0.14088424,  0.93203685,  0.09601785 };
		final double[][] covMatrix = new double[][]{
				{0.0, 0.0, 0.0, 0.0, 0.0},
				{0.0, 0.05366822, 0.04117003, 0.03407600,  0.03788519},
				{0.0, 0.04117003, 0.08108542, 0.03778130,  0.04324302},
				{0.0, 0.03407600, 0.03778130, 0.05394404,  0.03338272},
				{0.0, 0.03788519, 0.04324302, 0.03338272,  0.09859621}
		};
		return createMeasurement(mean, stdDev, meanVector, covMatrix, alternatives);
	}

	private CriterionMeasurement buildDizziness(final List<Alternative> alternatives) {
		final double mean = -2.229952;
		final double stdDev = 0.6103081;
		final double[] meanVector = new double[] {0, 0.1203208, 0.6538699, -0.2203627, 1.1915229};
		final double[][] covMatrix = new double[][]{
				{0.0, 0.0, 0.0, 0.0, 0.0},
				{0.0, 0.09124249, 0.07387436, 0.08086186, 0.06457527},
				{0.0, 0.07387436, 0.14080128, 0.10328788, 0.07030520},
				{0.0, 0.08086186, 0.10328788, 0.14662864, 0.06920427},
				{0.0, 0.06457527, 0.07030520, 0.06920427, 0.08590214}
		};
		return createMeasurement(mean, stdDev, meanVector, covMatrix, alternatives);
	}

	private CriterionMeasurement buildHAMD(final List<Alternative> alternatives) {
		final double mean = -0.1714358;
		final double stdDev = 0.1129926;
		final double[] meanVector = new double[] {0.0, 0.4718129,   0.7258847,   0.6715258,   0.8211993 };
		final double[][] covMatrix = new double[][]{
				{0.0, 0.0, 0.0, 0.0, 0.0},
				{0.0, 0.013445532, 0.010394690, 0.009881156, 0.010499559},
				{0.0, 0.010394690, 0.023006616, 0.008196856, 0.010732709},
				{0.0, 0.009881156, 0.008196856, 0.019023866, 0.009210099},
				{0.0, 0.010499559, 0.010732709, 0.009210099, 0.019107243}
		};
		return createMeasurement(mean, stdDev, meanVector, covMatrix, alternatives);
	}

	private CriterionMeasurement buildHeadache(final List<Alternative> alternatives) {
		final double mean = -1.195991;
		final double stdDev = 0.2926644;
		final double[] meanVector = new double[] {0.0, 0.1964591,   0.1263129,   0.2029933,  -0.2254059 };
		final double[][] covMatrix = new double[][]{
				{0.0, 0.0, 0.0, 0.0, 0.0},
				{0.0, 0.03519380, 0.02755068, 0.02189916,  0.02919822},
				{0.0, 0.02755068, 0.04847513, 0.02374509,  0.03277411},
				{0.0, 0.02189916, 0.02374509, 0.03259199,  0.02356638},
				{0.0, 0.02919822, 0.03277411, 0.02356638,  0.05920276}
		};
		return createMeasurement(mean, stdDev, meanVector, covMatrix, alternatives);
	}

	private CriterionMeasurement buildInsomnia(final List<Alternative> alternatives) {
		final double mean = -2.607277;
		final double stdDev = 0.1905852;
		final double[] meanVector = new double[] {0.0, 0.7978386,   0.7486351,   1.0663029,   0.9744811};
		final double[][] covMatrix = new double[][]{
				{0.0, 0.0, 0.0, 0.0, 0.0},
				{0.0, 0.04388258, 0.03516051, 0.02560003,  0.03283164},
				{0.0, 0.03516051, 0.06255486, 0.02807280,  0.03474863},
				{0.0, 0.02560003, 0.02807280, 0.05402063,  0.02592013},
				{0.0, 0.03283164, 0.03474863, 0.02592013,  0.06738850}
		};
		return createMeasurement(mean, stdDev, meanVector, covMatrix, alternatives);
	}

	private CriterionMeasurement buildNausea(final List<Alternative> alternatives) {
		final double mean = -2.017171;
		final double stdDev = 0.1917053;
		final double[] meanVector = new double[] {0.0, 0.3951561,   0.6442827,   0.6469638,   0.9771107};
		final double[][] covMatrix = new double[][]{
				{0.0, 0.0, 0.0, 0.0, 0.0},
				{0.0, 0.04167875, 0.03492591, 0.02952693,  0.03559971},
				{0.0, 0.03492591, 0.05629741, 0.03102295,  0.03658295},
				{0.0, 0.02952693, 0.03102295, 0.04396177,  0.02899487},
				{0.0, 0.03559971, 0.03658295, 0.02899487,  0.05369960}
		};
		return createMeasurement(mean, stdDev, meanVector, covMatrix, alternatives);
	}
	
	private RelativeLogitGaussianCriterionMeasurement createMeasurement(
			final double mean, final double stdDev, final double[] meanVector,
			final double[][] covMatrix, final List<Alternative> alternatives) {
		GaussianMeasurement baseline = new GaussianMeasurement(mean, stdDev);
		MultivariateGaussianCriterionMeasurement delta = new MultivariateGaussianCriterionMeasurement(alternatives);
		delta.setMeanVector(new ArrayRealVector(meanVector));
		delta.setCovarianceMatrix(new Array2DRowRealMatrix(covMatrix));
		RelativeGaussianCriterionMeasurement relative = new RelativeGaussianCriterionMeasurement(delta, baseline);
		RelativeLogitGaussianCriterionMeasurement incidence = new RelativeLogitGaussianCriterionMeasurement(relative);
		return incidence;
	}
	
	private List<Alternative> movePlacebo(final List<Alternative> alternatives, final int fromIndex, final int toIndex) {
		List<Alternative> newAlts = new ArrayList<Alternative>(alternatives);
		Alternative placebo = newAlts.remove(fromIndex);
		assertEquals("Placebo", placebo.getName());
		newAlts.add(toIndex, placebo);
		return newAlts;
	}
}
