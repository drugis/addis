/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

package org.drugis.addis.util.JSMAAintegration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;

import junit.framework.AssertionFailedError;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.CriterionMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.MultivariateGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.RelativeGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.RelativeLogitGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.simulator.SMAA2Simulation;

public class NetworkBenefitRiskTestBase {

	private static final double[][] EXPECTED_RA = {
			{ 0.0215, 0.2648, 0.3217, 0.3143, 0.0777 },
			{ 0.1496, 0.3248, 0.2519, 0.2249, 0.0488 },
			{ 0.6114, 0.1201, 0.0868, 0.0545, 0.1272 },
			{ 0.0758, 0.1342, 0.1931, 0.2389, 0.3580 },
			{ 0.1417, 0.1561, 0.1465, 0.1674, 0.3883 }};
	private static final double[][] EXPECTED_CW = {
			{ 0.08646901, 0.24112709, 0.22045918, 0.1362404, 0.10229834, 0.21340600 },
			{ 0.17593187, 0.12181948, 0.29181468, 0.1293235, 0.13949573, 0.14161473 },
			{ 0.17986517, 0.17659262, 0.09069475, 0.1618931, 0.19437357, 0.19658082 },
			{ 0.07495453, 0.31162260, 0.27471687, 0.1266365, 0.09423491, 0.11783455 },
			{ 0.18030730, 0.07638429, 0.27879293, 0.2584503, 0.11542176, 0.09064338 }};
	private static final double[] EXPECTED_CF = {
			0.1144, 0.5532, 0.9870, 0.5538, 0.6522
		};
	private static final double EPSILON_CW = 0.03;
	private static final double EPSILON_RA = 0.02;

	protected static CriterionMeasurement buildDiarrhea(final List<Alternative> alternatives) {
		checkAlternativeOrder(alternatives, true);
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

	protected static CriterionMeasurement buildDizziness(final List<Alternative> alternatives) {
		checkAlternativeOrder(alternatives, true);
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

	protected static CriterionMeasurement buildHAMD(final List<Alternative> alternatives) {
		checkAlternativeOrder(alternatives, true);
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

	protected static CriterionMeasurement buildHeadache(final List<Alternative> alternatives) {
		checkAlternativeOrder(alternatives, true);
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

	protected static CriterionMeasurement buildInsomnia(final List<Alternative> alternatives) {
		checkAlternativeOrder(alternatives, true);
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

	protected static CriterionMeasurement buildNausea(final List<Alternative> alternatives) {
		checkAlternativeOrder(alternatives, true);
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

	private static RelativeLogitGaussianCriterionMeasurement createMeasurement(final double mean, final double stdDev,
			final double[] meanVector, final double[][] covMatrix, final List<Alternative> alternatives) {
				GaussianMeasurement baseline = new GaussianMeasurement(mean, stdDev);
				MultivariateGaussianCriterionMeasurement delta = new MultivariateGaussianCriterionMeasurement(alternatives);
				delta.setMeanVector(new ArrayRealVector(meanVector));
				delta.setCovarianceMatrix(new Array2DRowRealMatrix(covMatrix));
				RelativeGaussianCriterionMeasurement relative = new RelativeGaussianCriterionMeasurement(delta, baseline);
				RelativeLogitGaussianCriterionMeasurement incidence = new RelativeLogitGaussianCriterionMeasurement(relative);
				return incidence;
			}

	public NetworkBenefitRiskTestBase() {
		super();
	}

	protected void checkResults(SMAAModel model, SMAA2Simulation simulation, double slack) {
		checkAlternativeOrder(model.getAlternatives(), false);
		assertEquals("Diarrhea", model.getCriteria().get(0).getName());
		assertEquals("HAM-D Responders", model.getCriteria().get(2).getName());
		
		// verify CWs & CFs
		for (int i = 0; i < model.getAlternatives().size(); ++i) {
			Alternative alt = model.getAlternatives().get(i);
			for (int j = 0; j < model.getCriteria().size(); ++j) {
				Criterion crit = model.getCriteria().get(j);
				final double actual = simulation.getResults().getCentralWeightVectors().get(alt).get(crit);
				final double expected = EXPECTED_CW[i][j];
				if (Math.abs(actual - expected) > EPSILON_CW * slack) {
					throw new AssertionFailedError(
							"Central weight for " + alt + " differs on " + crit +
							": expected <" + expected + ">, actual <" + actual + ">");
				}
			}
			final double actual = simulation.getResults().getConfidenceFactors().get(alt);
			final double expected = EXPECTED_CF[i];
			if (Math.abs(actual - expected) > EPSILON_CW * slack) {
				throw new AssertionFailedError(
						"Confidence factor for " + alt + " differs: expected <" +
						expected + ">, actual <" + actual + ">");
			}
		}
		
		// verify RAs
		for (int i = 0; i < model.getAlternatives().size(); ++i) {
			for (int r = 0; r < model.getAlternatives().size(); ++r) {
				Alternative alt = model.getAlternatives().get(i);
				final double actual = simulation.getResults().getRankAcceptabilities().get(alt).get(r);
				final double expected = EXPECTED_RA[i][r];
				if (Math.abs(actual - expected) > EPSILON_RA * slack) {
					throw new AssertionFailedError(
							"Rank probability for " + alt + " differs on " + (r + 1) +
							": expected <" + expected + ">, actual <" + actual + ">");
				}
			}
		}
	}

	private static void checkAlternativeOrder(List<Alternative> alternatives, boolean placeboFirst) {
		List<String> expected;
		if (placeboFirst) {
			expected = Arrays.asList("Placebo", "Fluoxetine", "Paroxetine", "Sertraline", "Venlafaxine");
		} else {
			expected = Arrays.asList("Fluoxetine", "Paroxetine", "Placebo", "Sertraline", "Venlafaxine");
		}
		for (int i = 0; i < expected.size(); ++i) {
			assertEquals(expected.get(i), alternatives.get(i).getName());
		}
	}

	protected static List<Alternative> movePlacebo(final List<Alternative> alternatives, final int fromIndex, final int toIndex) {
		List<Alternative> newAlts = new ArrayList<Alternative>(alternatives);
		Alternative placebo = newAlts.remove(fromIndex);
		assertEquals("Placebo", placebo.getName());
		newAlts.add(toIndex, placebo);
		return newAlts;
	}

}