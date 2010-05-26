package org.drugis.addis.util.JSMAAintegration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JProgressBar;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.OutcomeMeasure.Direction;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.addis.entities.relativeeffect.Gaussian;
import org.drugis.addis.entities.relativeeffect.LogGaussian;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.GaussianMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.ScaleCriterion;
import fi.smaa.jsmaa.simulator.ResultsEvent;
import fi.smaa.jsmaa.simulator.SMAA2Results;
import fi.smaa.jsmaa.simulator.SMAA2SimulationThread;
import fi.smaa.jsmaa.simulator.SMAAResultsListener;
import fi.smaa.jsmaa.simulator.SMAASimulator;

public class SMAAEntityFactory {

	private final class ProgressListener implements SMAAResultsListener {
		private final SMAASimulator d_simulator;
		private final JProgressBar d_bar;

		public ProgressListener(SMAASimulator simulator, JProgressBar bar) {
			d_simulator = simulator;
			d_bar = bar;
		}
		
		public void resultsChanged(ResultsEvent ev) {
			if (d_bar != null) {
				int progress = (d_simulator.getCurrentIteration() *100) / d_simulator.getTotalIterations();
				d_bar.setValue(progress);
			}
		}
	}
	
	private Map<OutcomeMeasure, CardinalCriterion> d_outcomeCriterionMap;
	private Map<Drug, Alternative> d_drugAlternativeMap;
	private SMAA2Results d_smaaModelResults;
	
	public SMAAEntityFactory() {
		d_outcomeCriterionMap = new HashMap<OutcomeMeasure, CardinalCriterion>();
		d_drugAlternativeMap  = new HashMap<Drug, Alternative>();
	}
	
	public static CardinalMeasurement createCardinalMeasurement(Distribution re) {
		if (re instanceof LogGaussian)
			return new LogNormalMeasurement(((LogGaussian) re).getMu(), ((LogGaussian) re).getSigma());
		else if (re instanceof Gaussian){
			return new GaussianMeasurement(((Gaussian) re).getMu(), ((Gaussian) re).getSigma());
		} else
			throw new IllegalArgumentException("Unhandled distribution: " + re);
	}
	
	public SMAA2Results createSmaaModelResults(BenefitRiskAnalysis bra, JProgressBar progressBar) {
		if (d_smaaModelResults != null)
			return d_smaaModelResults;
		
		SMAAModel smaaModel = createSmaaModel(bra);
		runSMAAModel(progressBar, smaaModel);

		return d_smaaModelResults;
	}

	private void runSMAAModel(JProgressBar progressBar, SMAAModel smaaModel) {
		SMAA2SimulationThread simulationThread = new SMAA2SimulationThread(smaaModel, 500000);
		SMAASimulator simulator = new SMAASimulator(smaaModel, simulationThread);
		d_smaaModelResults = (SMAA2Results)simulator.getResults();
		d_smaaModelResults.addResultsListener(new ProgressListener(simulator, progressBar));
		simulationThread.start();
	}

	SMAAModel createSmaaModel(BenefitRiskAnalysis brAnalysis) {
		SMAAModel smaaModel = new SMAAModel(brAnalysis.getName());

		// FIXME: refactor BRAnalysis to have baseline in the set of drugs.
		Set<Drug> drugs = new HashSet<Drug>();
		drugs.addAll(brAnalysis.getDrugs());
		drugs.add(brAnalysis.getBaseline());
				
		for (Drug d: drugs) {
			smaaModel.addAlternative(getAlternative(d));
		}
		
		for (OutcomeMeasure om : brAnalysis.getOutcomeMeasures()) {
			CardinalCriterion crit = getCriterion(om);
			smaaModel.addCriterion(crit);
			
			for (Drug d : drugs) {
				CardinalMeasurement m = createCardinalMeasurement(brAnalysis.getRelativeEffectDistribution(d, om));
				smaaModel.setMeasurement(crit, getAlternative(d), m);
			}
		}
		return smaaModel;
	}
	
	CardinalCriterion getCriterion(OutcomeMeasure om) {
		if(d_outcomeCriterionMap.containsKey(om))
			return d_outcomeCriterionMap.get(om);
		ScaleCriterion c = new ScaleCriterion(om.getName());
		c.setAscending(om.getDirection() == Direction.HIGHER_IS_BETTER);
		d_outcomeCriterionMap.put(om, c);
		return c;
	}
	
	Alternative getAlternative(Drug d) {
		if(d_drugAlternativeMap.containsKey(d))
			return d_drugAlternativeMap.get(d);
		Alternative a = new Alternative(d.getName());
		d_drugAlternativeMap.put(d, a);
		return a;
	}
}