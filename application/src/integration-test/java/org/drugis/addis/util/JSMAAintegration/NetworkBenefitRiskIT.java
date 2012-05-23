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

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.gui.MCMCPresentation;
import org.drugis.addis.presentation.MetaBenefitRiskPresentation;
import org.drugis.addis.presentation.SMAAPresentation;
import org.drugis.addis.util.JSMAAintegration.NetworkBenefitRiskTestBase;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.MixedTreatmentComparison.ExtendSimulation;
import org.junit.Ignore;
import org.junit.Test;

import fi.smaa.common.RandomUtil;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.simulator.SMAA2Simulation;

/**
 * Test results of MetaBenefitRisk implementation against results of our published analysis.
 * This test calculates the MTC results in ADDIS using the underlying trial data.
 * @see <a href="http://drugis.org/network-br">network-br paper</a>
 */
public class NetworkBenefitRiskIT extends NetworkBenefitRiskTestBase {
	/**
	 * Test SMAA using measurements derived using the internal MTC models.
	 */
	@Ignore
	@Test
	public void testNetworkBR() throws FileNotFoundException, IOException, InterruptedException {
		DomainManager domainManager = new DomainManager();
		domainManager.loadXMLDomain(NetworkBenefitRiskIT.class.getResourceAsStream("network-br.addis"), 1);
		
		MetaBenefitRiskAnalysis br = (MetaBenefitRiskAnalysis) domainManager.getDomain().getBenefitRiskAnalyses().get(0);
		
		// Run required models
		MetaBenefitRiskPresentation brpm = new MetaBenefitRiskPresentation(br, null);
		for (MCMCPresentation model : brpm.getWrappedModels()) {
			if (model.getModel() instanceof MixedTreatmentComparison) {
				MixedTreatmentComparison mtc = (MixedTreatmentComparison) model.getModel();
				mtc.setSimulationIterations(20000);
				mtc.setExtendSimulation(ExtendSimulation.FINISH);
			}
			TaskUtil.run(model.getModel().getActivityTask());
		}
		
		// Build SMAA model
		SMAAPresentation<DrugSet, MetaBenefitRiskAnalysis> smaapm = brpm.getSMAAPresentation();
		SMAAModel model = smaapm.getSMAAFactory().createSMAAModel();

		// Reorder criteria
		List<Criterion> newCrit = new ArrayList<Criterion>(model.getCriteria());
		Criterion hamd = newCrit.remove(0);
		assertEquals("HAM-D Responders", hamd.getName());
		newCrit.add(2, hamd);
		model.reorderCriteria(newCrit);

		// Run SMAA
		RandomUtil random = RandomUtil.createWithRandomSeed();
		SMAA2Simulation simulation = new SMAA2Simulation(model, random, 10000);
		TaskUtil.run(simulation.getTask());
		
		checkResults(model, simulation, 2.0);
	}
}
