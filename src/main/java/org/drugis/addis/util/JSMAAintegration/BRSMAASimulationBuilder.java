package org.drugis.addis.util.JSMAAintegration;

import fi.smaa.jsmaa.gui.jfreechart.CentralWeightsDataset;
import fi.smaa.jsmaa.gui.jfreechart.RankAcceptabilitiesDataset;
import fi.smaa.jsmaa.gui.presentation.CentralWeightTableModel;
import fi.smaa.jsmaa.gui.presentation.RankAcceptabilityTableModel;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.simulator.ResultsEvent;
import fi.smaa.jsmaa.simulator.SMAA2Results;
import fi.smaa.jsmaa.simulator.SMAA2SimulationThread;
import fi.smaa.jsmaa.simulator.SMAAResultsListener;
import fi.smaa.jsmaa.simulator.SimulationBuilder;

public class BRSMAASimulationBuilder extends SimulationBuilder<SMAAModel, SMAA2Results, SMAA2SimulationThread>{

	private RankAcceptabilityTableModel d_resTableModel;
	private RankAcceptabilitiesDataset d_resDataset;
	private CentralWeightTableModel d_cwTableModel;
	private CentralWeightsDataset d_cwDataset;

	public BRSMAASimulationBuilder(SMAAModel model, RankAcceptabilityTableModel resTableModel, RankAcceptabilitiesDataset dataSet,
				CentralWeightTableModel cwTableModel, CentralWeightsDataset cwDataSet) {
		super(model);
		d_resTableModel = resTableModel;
		d_resDataset = dataSet;
		d_cwTableModel = cwTableModel;
		d_cwDataset = cwDataSet;
	}

	@Override
	protected SMAA2SimulationThread generateSimulationThread() {
		return new SMAA2SimulationThread(model, 10000);
	}

	@Override
	protected void prepareSimulation(SMAA2Results results) {
		setResults(results);
		results.addResultsListener(new SMAAResultsListener() {
			public void resultsChanged(ResultsEvent ev) {
				
			}
		});
	}

	private void setResults(SMAA2Results results) {
		d_resTableModel.setResults(results);
		d_resDataset.setResults(results);
		d_cwTableModel.setResults(results);
		d_cwDataset.setResults(results);
	}
}
