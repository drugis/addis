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

package org.drugis.addis.util.JSMAAintegration;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.gui.AddisMCMCPresentation;
import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.addis.presentation.MetaBenefitRiskPresentation;
import org.drugis.addis.presentation.SMAAPresentation;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.MCMCModel.ExtendSimulation;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import fi.smaa.common.RandomUtil;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.CriterionMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.PerCriterionMeasurements;
import fi.smaa.jsmaa.model.RelativeGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.RelativeLogitGaussianCriterionMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.simulator.SMAA2Simulation;

/**
 * Test results of MetaBenefitRisk implementation against results of our published analysis.
 * This test calculates the MTC results in ADDIS using the underlying trial data.
 * @see <a href="http://drugis.org/network-br">network-br paper</a>
 */
public class NetworkBenefitRiskIT extends NetworkBenefitRiskTestBase {

	private static DomainManager d_domainManager;
	private static MetaBenefitRiskAnalysis d_br;
	private static MetaBenefitRiskPresentation d_brpm;
	private static SMAAModel d_model;

	@BeforeClass
	public static void setUp() throws IOException, InterruptedException {
		System.out.println("BeforeClass -- Running models");
		d_domainManager = new DomainManager();
		d_domainManager.loadXMLDomain(NetworkBenefitRiskIT.class.getResourceAsStream("network-br.addis"), 1);

		final Domain domain = d_domainManager.getDomain();
		d_br = (MetaBenefitRiskAnalysis) domain.getBenefitRiskAnalyses().get(0);

		d_brpm = new MetaBenefitRiskPresentation(d_br, null);
		for (final AddisMCMCPresentation model : d_brpm.getWrappedModels()) {
			System.out.println("Running " + model);
			model.getModel().setSimulationIterations(50000);
			model.getModel().setExtendSimulation(ExtendSimulation.FINISH);
			TaskUtil.run(model.getModel().getActivityTask());
		}
		// Build SMAA model
		SMAAPresentation<TreatmentDefinition, MetaBenefitRiskAnalysis> smaapm = d_brpm.getSMAAPresentation();
		d_model = smaapm.getSMAAFactory().createSMAAModel();
	}

	@AfterClass
	public static void cleanUp() {
		d_domainManager = null;
		d_br = null;
		d_brpm = null;
		d_model = null;
	}

	/**
	 * Test SMAA using measurements derived using the internal MTC models.
	 */
	@Test
	public void testNetworkBRBaseline() {
		// Test results of underlying models
		verifyBaselineMeasurement(d_br, "HAM-D Responders", -0.17, 0.11);
		verifyBaselineMeasurement(d_br, "Diarrhea", -2.19, 0.21);
		verifyBaselineMeasurement(d_br, "Dizziness", -2.23, 0.61);
		verifyBaselineMeasurement(d_br, "Headache", -1.20, 0.29);
		verifyBaselineMeasurement(d_br, "Insomnia", -2.61, 0.19);
		verifyBaselineMeasurement(d_br, "Nausea", -2.02, 0.19);
	}

	@Test
	public void testNetworkBRRelativeEffects() throws FileNotFoundException, IOException, InterruptedException {
		List<Alternative> alternatives = new ArrayList<Alternative>(d_model.getAlternatives());
		alternatives = movePlacebo(alternatives, findPlacebo(alternatives), 0); // The build* expect placebo first
		verifyRelativeEffects(d_brpm, d_model, "HAM-D Responders", buildHAMD(alternatives));
		verifyRelativeEffects(d_brpm, d_model, "Diarrhea", buildDiarrhea(alternatives));
		verifyRelativeEffects(d_brpm, d_model, "Dizziness", buildDizziness(alternatives));
		verifyRelativeEffects(d_brpm, d_model, "Headache", buildHeadache(alternatives));
		verifyRelativeEffects(d_brpm, d_model, "Insomnia", buildInsomnia(alternatives));
		verifyRelativeEffects(d_brpm, d_model, "Nausea", buildNausea(alternatives));
	}

	@Test
	public void testNetworkBRModel() throws InterruptedException {
		// Reorder criteria
		final List<Criterion> newCrit = new ArrayList<Criterion>(d_model.getCriteria());
		final Criterion hamd = newCrit.remove(0);
		assertEquals("HAM-D Responders", hamd.getName());
		newCrit.add(2, hamd);
		d_model.reorderCriteria(newCrit);

		// Run SMAA
		final RandomUtil random = RandomUtil.createWithRandomSeed();
		final SMAA2Simulation simulation = new SMAA2Simulation(d_model, random, 10000);
		TaskUtil.run(simulation.getTask());

		checkResults(d_model, simulation, 2.0);
	}

	private static void verifyRelativeEffects(final MetaBenefitRiskPresentation brpm, final SMAAModel model,
			final String name, final CriterionMeasurement expected) {
		final PerCriterionMeasurements measurements = (PerCriterionMeasurements) model.getMeasurements();

		final CriterionMeasurement actual = measurements.getCriterionMeasurement(brpm.getSMAAPresentation().getSMAAFactory().getCriterion(EntityUtil.findByName(brpm.getBean().getCriteria(), name)));
		actual.reorderAlternatives(movePlacebo(actual.getAlternatives(), findPlacebo(actual.getAlternatives()), 2)); // Reorder alternatives
		expected.reorderAlternatives(movePlacebo(expected.getAlternatives(), findPlacebo(expected.getAlternatives()), 2)); // Reorder alternatives
		assertRelativeLogitEquals((RelativeLogitGaussianCriterionMeasurement) expected, (RelativeLogitGaussianCriterionMeasurement) actual, 0.05);
	}


	private static void assertRelativeLogitEquals(
			final RelativeLogitGaussianCriterionMeasurement expected,
			final RelativeLogitGaussianCriterionMeasurement actual, final double d) {
		assertRelativeGaussian(expected.getGaussianMeasurement(), actual.getGaussianMeasurement(), d);
	}

	private static void assertRelativeGaussian(
			final RelativeGaussianCriterionMeasurement expected,
			final RelativeGaussianCriterionMeasurement actual, final double d) {

		final GaussianMeasurement expectedBaseline = expected.getBaselineMeasurement();
		final GaussianMeasurement actualBaseline = actual.getBaselineMeasurement();

		assertEquals(expectedBaseline.getMean(), actualBaseline.getMean(), d);
		assertEquals(expectedBaseline.getStDev(), actualBaseline.getStDev(), d);

		final double[] expectedCovariance = flatten(expected.getRelativeMeasurement().getCovarianceMatrix().getData());
		final double[] actualCovariance = flatten(actual.getRelativeMeasurement().getCovarianceMatrix().getData());

		assertArrayEquals(expectedCovariance, actualCovariance, d);

		final RealVector expectedMeanVector = expected.getRelativeMeasurement().getMeanVector();
		final RealVector actualMeanVector = actual.getRelativeMeasurement().getMeanVector();

		assertArrayEquals(expectedMeanVector.toArray(), actualMeanVector.toArray(), d);
	}

	private static void verifyBaselineMeasurement(final MetaBenefitRiskAnalysis br,
			final String name, final double mu, final double sigma) {
		final OutcomeMeasure om = EntityUtil.findByName(br.getCriteria(), name);
		assertEquals(sigma, br.getBaselineDistribution(om).getSigma(), 0.05);
		assertEquals(mu, br.getBaselineDistribution(om).getMu(), 0.05);
	}

	private static int findPlacebo(final List<Alternative> alternatives) {
		int expPlaceboIdx = 0;
		for(int idx = 0; idx < alternatives.size(); idx++)  {
			if(alternatives.get(idx).getName().equals("Placebo")) {
				expPlaceboIdx = idx;
			}
		}
		return expPlaceboIdx;
	}

	private static double[] flatten(final double[][] matrix2d) {
		final double[] newArray = new double[2 * matrix2d[0].length];
		int index = 0;
		for (int n = 0; n < matrix2d[0].length; n++) {
		    newArray[index++] = matrix2d[0][n];
		    newArray[index++] = matrix2d[1][n];
		}
		return newArray;
	}

}
