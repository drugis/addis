/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import fi.smaa.jsmaa.gui.components.SimulationProgressBar;
import fi.smaa.jsmaa.gui.jfreechart.CentralWeightsDataset;
import fi.smaa.jsmaa.gui.jfreechart.RankAcceptabilitiesDataset;
import fi.smaa.jsmaa.gui.presentation.CentralWeightTableModel;
import fi.smaa.jsmaa.gui.presentation.RankAcceptabilityTableModel;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.simulator.SMAA2Results;
import fi.smaa.jsmaa.simulator.SMAA2SimulationThread;
import fi.smaa.jsmaa.simulator.SimulationBuilder;

public class BRSMAASimulationBuilder extends SimulationBuilder<SMAAModel, SMAA2Results, SMAA2SimulationThread>{

	private RankAcceptabilityTableModel d_resTableModel;
	private RankAcceptabilitiesDataset d_resDataset;
	private CentralWeightTableModel d_cwTableModel;
	private CentralWeightsDataset d_cwDataset;
	private SimulationProgressBar d_progressBar;

	public BRSMAASimulationBuilder(SMAAModel model, RankAcceptabilityTableModel resTableModel, RankAcceptabilitiesDataset dataSet,
				CentralWeightTableModel cwTableModel, CentralWeightsDataset cwDataSet, SimulationProgressBar progressBar) {
		super(model);
		d_resTableModel = resTableModel;
		d_resDataset = dataSet;
		d_cwTableModel = cwTableModel;
		d_cwDataset = cwDataSet;
		d_progressBar = progressBar;
	}

	@Override
	protected SMAA2SimulationThread generateSimulationThread() {
		return new SMAA2SimulationThread(model, 10000);
	}

	@Override
	protected void prepareSimulation(SMAA2Results results) {
		setResults(results);
		d_progressBar.setSimulator(simulator);
	}

	private void setResults(SMAA2Results results) {
		d_resTableModel.setResults(results);
		d_resDataset.setResults(results);
		d_cwTableModel.setResults(results);
		d_cwDataset.setResults(results);
	}
}
