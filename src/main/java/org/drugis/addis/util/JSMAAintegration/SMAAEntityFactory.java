package org.drugis.addis.util.JSMAAintegration;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JProgressBar;

import org.drugis.addis.entities.BenefitRiskAnalysis;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RelativeEffect;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.CardinalMeasurement;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.ExactMeasurement;
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
	
	private Map<OutcomeMeasure, Criterion> d_outcomeCriterionMap;
	private Map<Drug, Alternative> d_drugAlternativeMap;
	private SMAA2Results d_smaaModelResults;
	
	public SMAAEntityFactory() {
		d_outcomeCriterionMap = new HashMap<OutcomeMeasure, Criterion>();
		d_drugAlternativeMap  = new HashMap<Drug, Alternative>();
	}
	
	public static CardinalMeasurement createCardinalMeasurement(RelativeEffect<? extends Measurement> re) {
		/* Beware, even though the axistype is logarithmic, the mean has been converted to a normal scale 
		 * by taking the exponent.
		 */
		if (re.getAxisType().equals(RelativeEffect.AxisType.LOGARITHMIC))
			return new LogNormalMeasurement(Math.log(re.getMedian()),re.getSigma());
		else if (re.getAxisType().equals(RelativeEffect.AxisType.LINEAR)){
			return new GaussianMeasurement(re.getMedian(),re.getSigma());
		} else
			throw new IllegalArgumentException("RelativeEffect has an unknown axis-type: " + re);
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

		Alternative baseLineAlt = getAlternative(brAnalysis.getBaseline());
		smaaModel.addAlternative(baseLineAlt);

		for(OutcomeMeasure om : brAnalysis.getOutcomeMeasures()){ // endpoints
			Criterion crit = getCriterion(om);
			smaaModel.addCriterion(crit);
			
			boolean baseLineSet = false;
			for(Drug d : brAnalysis.getDrugs()){ // drugs
				RelativeEffect<? extends Measurement> relativeEffect = (RelativeEffect<? extends Measurement>) brAnalysis.getRelativeEffect(d, om);
				
				// set the baseline // FIXME
				if (!baseLineSet) {
					//System.out.println(relativeEffect.getAxisType());
					if(relativeEffect.getAxisType() == RelativeEffect.AxisType.LOGARITHMIC)
						smaaModel.setMeasurement(crit, baseLineAlt, new ExactMeasurement(1d));	
					else if(relativeEffect.getAxisType() == RelativeEffect.AxisType.LINEAR)
						smaaModel.setMeasurement(crit, baseLineAlt, new ExactMeasurement(0d));	
					else throw new IllegalArgumentException("RelativeEffect has an unknown axis-type: " + relativeEffect);
					baseLineSet = true;
				}
				//smaaModel.setMeasurement(crit, baseLineAlt, new ExactMeasurement(1d));
				
				// set the alternatives measurements
				smaaModel.addAlternative(getAlternative(d));
				CardinalMeasurement m = createCardinalMeasurement(relativeEffect);
				smaaModel.setMeasurement( crit, getAlternative(d), m);		
			}
		}
		return smaaModel;
	}
	
	Criterion getCriterion(OutcomeMeasure om) {
		if(d_outcomeCriterionMap.containsKey(om))
			return d_outcomeCriterionMap.get(om);
		ScaleCriterion c = new ScaleCriterion(om.getName());
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


