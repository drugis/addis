package org.drugis.addis.util.JSMAAintegration;

import javax.swing.JProgressBar;

import fi.smaa.jsmaa.simulator.ResultsEvent;
import fi.smaa.jsmaa.simulator.SMAAResultsListener;
import fi.smaa.jsmaa.simulator.SMAASimulator;

public class SMAASimulationProgressListener implements SMAAResultsListener {
	private final SMAASimulator d_simulator;
	private final JProgressBar d_bar;

	public SMAASimulationProgressListener(SMAASimulator simulator, JProgressBar bar) {
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
