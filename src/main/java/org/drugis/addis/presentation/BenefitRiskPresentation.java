/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

package org.drugis.addis.presentation;

import java.io.FileOutputStream;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.util.JSMAAintegration.BRSMAASimulationBuilder;
import org.drugis.addis.util.JSMAAintegration.SMAAEntityFactory;

import com.jgoodies.binding.PresentationModel;

import fi.smaa.jsmaa.gui.components.SimulationProgressBar;
import fi.smaa.jsmaa.gui.jfreechart.CentralWeightsDataset;
import fi.smaa.jsmaa.gui.jfreechart.RankAcceptabilitiesDataset;
import fi.smaa.jsmaa.gui.presentation.CentralWeightTableModel;
import fi.smaa.jsmaa.gui.presentation.PreferencePresentationModel;
import fi.smaa.jsmaa.gui.presentation.RankAcceptabilityTableModel;
import fi.smaa.jsmaa.gui.presentation.SMAA2ResultsTableModel;
import fi.smaa.jsmaa.model.ModelChangeEvent;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.model.SMAAModelListener;
import fi.smaa.jsmaa.model.xml.JSMAABinding;
import fi.smaa.jsmaa.simulator.BuildQueue;
import fi.smaa.jsmaa.simulator.SMAA2Results;

@SuppressWarnings("serial")
public abstract class BenefitRiskPresentation<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> 
extends PresentationModel<AnalysisType> {

	protected PresentationModelFactory d_pmf;
	protected RankAcceptabilityTableModel d_rankAccepTM;
	protected RankAcceptabilitiesDataset d_rankAccepDS;
	protected BuildQueue d_buildQueue;
	protected CentralWeightsDataset d_cwDS;
	protected CentralWeightTableModel d_cwTM;
	protected PreferencePresentationModel d_prefPresModel;
	protected SMAAModel d_smaaModel;
	protected SimulationProgressBar d_progressBar;
	protected SMAAEntityFactory<Alternative> d_smaaf;

	public BenefitRiskPresentation(AnalysisType bean, PresentationModelFactory pmf) {
		super(bean);
		d_buildQueue = new BuildQueue();
		d_progressBar = new SimulationProgressBar();
		d_pmf = pmf;
	}
	
	public void startSMAA() {
		d_smaaf = new SMAAEntityFactory<Alternative>();
		d_smaaModel = d_smaaf.createSmaaModel(getBean());
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

	public void saveSmaa(String filename) {
		try {
			FileOutputStream os = new FileOutputStream(filename);
			JSMAABinding.writeModel(d_smaaModel, os);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public PresentationModelFactory getFactory() {
		return d_pmf;
	}

	public abstract void startAllSimulations();

	public abstract ValueHolder<Boolean> getMeasurementsReadyModel();


}
