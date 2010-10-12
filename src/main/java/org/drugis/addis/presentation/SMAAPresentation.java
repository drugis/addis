package org.drugis.addis.presentation;

import java.io.FileOutputStream;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.util.JSMAAintegration.BRSMAASimulationBuilder;
import org.drugis.addis.util.JSMAAintegration.SMAAEntityFactory;

import fi.smaa.jsmaa.gui.components.SimulationProgressBar;
import fi.smaa.jsmaa.gui.jfreechart.CentralWeightsDataset;
import fi.smaa.jsmaa.gui.jfreechart.RankAcceptabilitiesDataset;
import fi.smaa.jsmaa.gui.presentation.CentralWeightTableModel;
import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel;
import fi.smaa.jsmaa.gui.presentation.RankAcceptabilityTableModel;
import fi.smaa.jsmaa.gui.presentation.SMAA2ResultsTableModel;
import fi.smaa.jsmaa.model.CardinalCriterion;
import fi.smaa.jsmaa.model.ModelChangeEvent;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.SMAAModelListener;
import fi.smaa.jsmaa.model.xml.JSMAABinding;
import fi.smaa.jsmaa.simulator.BuildQueue;
import fi.smaa.jsmaa.simulator.SMAA2Results;

public class SMAAPresentation<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> 
{

	private AnalysisType d_a;

	public SMAAPresentation(AnalysisType a) {
		d_a = a;
		d_buildQueue = new BuildQueue();
		d_progressBar = new SimulationProgressBar();
	}

	protected RankAcceptabilityTableModel d_rankAccepTM;
	protected RankAcceptabilitiesDataset d_rankAccepDS;
	protected BuildQueue d_buildQueue;
	protected CentralWeightsDataset d_cwDS;
	protected CentralWeightTableModel d_cwTM;
	protected PreferencePresentationModel d_prefPresModel;
	protected SMAAModel d_smaaModel;
	protected SimulationProgressBar d_progressBar;
	protected SMAAEntityFactory<Alternative> d_smaaf;
	
	public void startSMAA() {
		d_smaaf = new SMAAEntityFactory<Alternative>();
		d_smaaModel = d_smaaf.createSmaaModel(d_a);
		SMAA2Results emptyResults = new SMAA2Results(d_smaaModel.getAlternatives(), d_smaaModel.getCriteria(), 10);
		d_rankAccepDS = new RankAcceptabilitiesDataset(emptyResults);
		d_rankAccepTM = new RankAcceptabilityTableModel(emptyResults);
		d_cwTM = new CentralWeightTableModel(emptyResults);
		d_cwDS = new CentralWeightsDataset(emptyResults);
		d_prefPresModel = new PreferencePresentationModel(d_smaaModel, false);
	
		d_smaaModel.addModelListener(new SMAAModelListener() {
			public void modelChanged(ModelChangeEvent type) {
				startSimulation();
			}			
		});
		startSimulation();
	}

	public SimulationProgressBar getSmaaSimulationProgressBar() {
		return d_progressBar;
	}

	protected void startSimulation() {
		d_buildQueue.add(new BRSMAASimulationBuilder(d_smaaModel,
				d_rankAccepTM, d_rankAccepDS, d_cwTM, d_cwDS, d_progressBar));
	}

	public PreferencePresentationModel getSmaaPreferenceModel() {
		return null;
	}

	public PreferencePresentationModel getPreferencePresentationModel() {
		return d_prefPresModel;
	}

	public SMAA2ResultsTableModel getRankAcceptabilitiesTableModel() {
		return d_rankAccepTM;
	}

	public RankAcceptabilitiesDataset getRankAcceptabilityDataSet() {
		return d_rankAccepDS;
	}

	public CentralWeightsDataset getCentralWeightsDataSet() {
		return d_cwDS;
	}

	public CentralWeightTableModel getCentralWeightsTableModel() {
		return d_cwTM;
	}
	

	public OutcomeMeasure getOutcomeMeasureForCriterion(CardinalCriterion crit) {
		return d_smaaf.getOutcomeMeasure(crit);
	}

	public void saveSmaa(String filename) {
		try {
			FileOutputStream os = new FileOutputStream(filename);
			JSMAABinding.writeModel(d_smaaModel, os);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
}
