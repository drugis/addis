/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.entities.analysis.models;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.DrugSet;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.MCMCMultivariateNormalSummary;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.RankProbabilitySummary;

import edu.uci.ics.jung.graph.util.Pair;


public class SimulationConsistencyModel extends AbstractSimulationModel<ConsistencyModel> implements ConsistencyWrapper {
	protected final MultivariateNormalSummary d_relativeEffectsSummary;
	private RankProbabilitySummary d_rankProbabilitySummary;
	private final List<DrugSet> d_drugs;

	public SimulationConsistencyModel(NetworkBuilder<DrugSet> builder, ConsistencyModel model, List<DrugSet> drugs) {
		super(builder, model);
		d_drugs = drugs;
		List<Pair<DrugSet>> relEffects = getRelativeEffectsList();
		Parameter[] parameters = new Parameter[relEffects.size()]; 
		for (int i = 0; i < relEffects.size(); ++i) {
			Pair<DrugSet> relEffect = relEffects.get(i);
			parameters[i] = getRelativeEffect(relEffect.getFirst(), relEffect.getSecond());
		}
		d_relativeEffectsSummary = new MCMCMultivariateNormalSummary(d_nested.getResults(), parameters);

	}

	@Override
	public MultivariateNormalSummary getRelativeEffectsSummary() {
		return d_relativeEffectsSummary;
	}
	
	public RankProbabilitySummary getRankProbabilities() {
		if (d_rankProbabilitySummary == null) {
			d_rankProbabilitySummary = new RankProbabilitySummary(d_nested.getResults(), getTreatments(d_drugs));
		}
		return d_rankProbabilitySummary;
	}
	

	public List<Pair<DrugSet>> getRelativeEffectsList() {
		List<Pair<DrugSet>> list = new ArrayList<Pair<DrugSet>>(d_drugs.size() - 1); // first DrugSet is baseline-> excluded
		for (int i = 0; i < d_drugs.size() - 1; ++i) {
			Pair<DrugSet> relEffect = new Pair<DrugSet>(d_drugs.get(0), d_drugs.get(i + 1));
			list.add(relEffect);
		}
		return list;
	}
}
