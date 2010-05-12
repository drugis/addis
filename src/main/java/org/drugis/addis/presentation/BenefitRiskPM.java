package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JProgressBar;

import org.drugis.addis.entities.BenefitRiskAnalysis;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;

import com.jgoodies.binding.PresentationModel;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.ExactMeasurement;
import fi.smaa.jsmaa.model.LogNormalMeasurement;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.ScaleCriterion;
import fi.smaa.jsmaa.simulator.ResultsEvent;
import fi.smaa.jsmaa.simulator.SMAA2Results;
import fi.smaa.jsmaa.simulator.SMAA2SimulationThread;
import fi.smaa.jsmaa.simulator.SMAAResultsListener;
import fi.smaa.jsmaa.simulator.SMAASimulator;

@SuppressWarnings("serial")
public class BenefitRiskPM extends PresentationModel<BenefitRiskAnalysis>{

	private final class ProgressListener implements SMAAResultsListener {
		private final SMAASimulator d_simulator;
		private final JProgressBar d_bar;

		public ProgressListener(SMAASimulator simulator, JProgressBar bar) {
			d_simulator = simulator;
			d_bar = bar;
		}
		
		public void resultsChanged(ResultsEvent ev) {
			int progress = (d_simulator.getCurrentIteration() *100) / d_simulator.getTotalIterations();
			d_bar.setValue(progress);
		}
	}
	
	private SMAA2Results d_smaaModelResults;
	private Map<OutcomeMeasure, Criterion> d_outcomeCriterionMap;
	private Map<Drug, Alternative> d_drugAlternativeMap;
	private PresentationModelFactory d_pmf;
	
	public BenefitRiskPM(BenefitRiskAnalysis bean, PresentationModelFactory pmf) {
		super(bean);
		
		d_outcomeCriterionMap = new HashMap<OutcomeMeasure, Criterion>();
		d_drugAlternativeMap  = new HashMap<Drug, Alternative>();
		d_pmf = pmf;
	}


	public SMAA2Results getSmaaModelResults(JProgressBar progressBar) {
		
		if (d_smaaModelResults != null)
			return d_smaaModelResults;

		BenefitRiskAnalysis brAnalysis = getBean();
		SMAAModel smaaModel = new SMAAModel(brAnalysis.getName());

		Alternative baseLineAlt = getAlternative(brAnalysis.getBaseline());
		smaaModel.addAlternative(baseLineAlt);

		for(OutcomeMeasure om : brAnalysis.getOutcomeMeasures()){ // endpoints
			Criterion crit = getCriterion(om);
			smaaModel.addCriterion(crit);
			smaaModel.setMeasurement(crit, baseLineAlt, new ExactMeasurement(1.0));		
			for(Drug d : brAnalysis.getDrugs()){ // drugs
				smaaModel.addAlternative(getAlternative(d));
				RelativeEffect<? extends Measurement> relativeEffect = brAnalysis.getRelativeEffect(d, om);
//			TODO: 
				//if(dichotomous) then
				fi.smaa.jsmaa.model.Measurement m = new LogNormalMeasurement(relativeEffect.getRelativeEffect(), relativeEffect.getError());
				//else if(continuous) then the baseline is 0, and:
//				fi.smaa.jsmaa.model.Measurement m = new GaussianMeasurement(relativeEffect.getRelativeEffect(), relativeEffect.getError());
				smaaModel.setMeasurement( crit, getAlternative(d), m);		
			}
		}

		SMAA2SimulationThread simulationThread = new SMAA2SimulationThread(smaaModel, 500000);
		SMAASimulator simulator = new SMAASimulator(smaaModel, simulationThread);
		d_smaaModelResults = (SMAA2Results)simulator.getResults();

		d_smaaModelResults.addResultsListener(new ProgressListener(simulator, progressBar));
		simulationThread.start();

		return d_smaaModelResults;
	}
	
	private Criterion getCriterion(OutcomeMeasure om) {
		if(d_outcomeCriterionMap.containsKey(om))
			return d_outcomeCriterionMap.get(om);
		ScaleCriterion c = new ScaleCriterion(om.getName());
		d_outcomeCriterionMap.put(om, c);
		return c;
	}
	
	private Alternative getAlternative(Drug d) {
		if(d_drugAlternativeMap.containsKey(d))
			return d_drugAlternativeMap.get(d);
		Alternative a = new Alternative(d.getName());
		d_drugAlternativeMap.put(d, a);
		return a;
	}

	public List<PresentationModel<MetaAnalysis>> getAnalysesPMList() {
		List<PresentationModel<MetaAnalysis>> entitiesPMs = new ArrayList<PresentationModel<MetaAnalysis>>();
		for (MetaAnalysis a : getBean().getMetaAnalyses())
			entitiesPMs.add(d_pmf.getModel(a));
		return entitiesPMs;
	}

	public BenefitRiskMeasurementTableModel getMeasurementTableModel() {
		BenefitRiskMeasurementTableModel brTableModel = new BenefitRiskMeasurementTableModel(getBean(), d_pmf);
		return brTableModel;
	}
}
