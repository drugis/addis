/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
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

import org.codehaus.jackson.JsonNode;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.util.JSMAAintegration.AbstractBenefitRiskSMAAFactory;
import org.drugis.addis.util.JSMAAintegration.BRSMAASimulationBuilder;
import org.drugis.addis.util.JSMAAintegration.SMAAEntityFactory;
import org.drugis.common.threading.NullTask;
import org.drugis.common.threading.status.TaskProgressModel;

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
import fi.smaa.jsmaa.simulator.BuildQueue;
import fi.smaa.jsmaa.simulator.SMAA2Results;

public class SMAAPresentation<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>>
{

	private AnalysisType d_a;

	protected RankAcceptabilityTableModel d_rankAccepTM;
	protected RankAcceptabilitiesDataset d_rankAccepDS;
	protected BuildQueue d_buildQueue;
	protected CentralWeightsDataset d_cwDS;
	protected CentralWeightTableModel d_cwTM;
	protected PreferencePresentationModel d_prefPresModel;
	protected SMAAModel d_smaaModel;
	protected AbstractBenefitRiskSMAAFactory<Alternative> d_smaaf;
	private ValueHolder<Boolean> d_initializedModel= new ModifiableHolder<Boolean>(false);

	private TaskProgressModel d_progressModel = new TaskProgressModel(new NullTask());

	private BRSMAASimulationBuilder d_simBuilder;

	public SMAAPresentation(AnalysisType a) {
		d_a = a;
		d_smaaf = SMAAEntityFactory.createFactory(d_a);
		d_buildQueue = new BuildQueue();
	}

	public void startSMAA() {
		d_smaaModel = d_smaaf.createSMAAModel();
		SMAA2Results emptyResults = new SMAA2Results(d_smaaModel.getAlternatives(), d_smaaModel.getCriteria(), 10);
		d_rankAccepDS = new RankAcceptabilitiesDataset(emptyResults);
		d_rankAccepTM = new RankAcceptabilityTableModel(emptyResults);
		d_cwTM = new CentralWeightTableModel(emptyResults);
		d_cwDS = new CentralWeightsDataset(emptyResults);
		d_prefPresModel = new PreferencePresentationModel(d_smaaModel, false);
		d_initializedModel.setValue(true);
		d_simBuilder = new BRSMAASimulationBuilder(d_smaaModel, d_rankAccepTM, d_rankAccepDS, d_cwTM, d_cwDS, d_progressModel);

		d_smaaModel.addModelListener(new SMAAModelListener() {
			public void modelChanged(ModelChangeEvent type) {
				startSimulation();
			}
		});
		startSimulation();
	}

	private BRSMAASimulationBuilder getBuilder() {
		return d_simBuilder;
	}

	public ValueHolder<Boolean> getInitializedModel() {
		return d_initializedModel;
	}

	protected void startSimulation() {
		getBuilder().resetModel();
		d_buildQueue.add(getBuilder());
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

	public TaskProgressModel getTaskProgressModel() {
		return d_progressModel;
	}

	public OutcomeMeasure getOutcomeMeasureForCriterion(CardinalCriterion crit) {
		return d_smaaf.getOutcomeMeasure(crit);
	}

	public AbstractBenefitRiskSMAAFactory<Alternative> getSMAAFactory() {
		return d_smaaf;
	}

	public JsonNode getJSON() {
		SMAASerializer<Alternative, AnalysisType> serializer = new SMAASerializer<Alternative, AnalysisType>(d_smaaf.createSMAAModel(), d_a, d_smaaf);
		return serializer.getRootNode();
	}

	public void saveSmaa(String filename) {
		try {
			FileOutputStream os = new FileOutputStream(filename);
			SMAASerializer<Alternative, AnalysisType> serializer = new SMAASerializer<Alternative, AnalysisType>(d_smaaf.createSMAAModel(), d_a, d_smaaf);
			serializer.serialize(os);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
