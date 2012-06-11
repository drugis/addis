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


import java.util.Collections;

import org.drugis.addis.util.JSMAAintegration.NetworkBenefitRiskIT;
import org.drugis.common.threading.TaskUtil;
import org.junit.Test;

import fi.smaa.common.RandomUtil;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.CriterionMeasurement;
import fi.smaa.jsmaa.model.PerCriterionMeasurements;
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
}
