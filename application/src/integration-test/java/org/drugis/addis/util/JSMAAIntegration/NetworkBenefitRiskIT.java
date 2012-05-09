package org.drugis.addis.util.JSMAAIntegration;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.gui.MCMCWrapper;
import org.drugis.addis.presentation.MetaBenefitRiskPresentation;
import org.drugis.addis.presentation.SMAAPresentation;
import org.drugis.addis.util.JSMAAintegration.NetworkBenefitRiskTestBase;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.MixedTreatmentComparison.ExtendSimulation;
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
	@Test
	public void testNetworkBR() throws FileNotFoundException, IOException, InterruptedException {
		DomainManager domainManager = new DomainManager();
		domainManager.loadXMLDomain(NetworkBenefitRiskIT.class.getResourceAsStream("network-br.addis"), 1);
		
		MetaBenefitRiskAnalysis br = (MetaBenefitRiskAnalysis) domainManager.getDomain().getBenefitRiskAnalyses().get(0);
		
		// Run required models
		MetaBenefitRiskPresentation brpm = new MetaBenefitRiskPresentation(br, null);
		for (MCMCWrapper model : brpm.getWrappedModels()) {
			if (model.getModel() instanceof MixedTreatmentComparison) {
				MixedTreatmentComparison mtc = (MixedTreatmentComparison) model.getModel();
				mtc.setSimulationIterations(20000);
				mtc.setExtendSimulation(ExtendSimulation.FINISH);
			}
			TaskUtil.run(model.getActivityTask());
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
