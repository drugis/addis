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

package org.drugis.addis.entities.mtcwrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.treatment.TreatmentCategorySet;
import org.drugis.mtc.MCMCSettingsCache;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.MultivariateNormalSummary;
import org.drugis.mtc.summary.QuantileSummary;
import org.drugis.mtc.summary.RankProbabilitySummary;

import edu.uci.ics.jung.graph.util.Pair;

public class SavedConsistencyWrapper extends AbstractSavedWrapper implements ConsistencyWrapper {

	private final MultivariateNormalSummary d_relativeEffectsSummary;
	private final RankProbabilitySummary d_rankProbabilitySummary;
	private List<TreatmentCategorySet> d_drugs;

	public SavedConsistencyWrapper(NetworkBuilder<TreatmentCategorySet> builder,
			MCMCSettingsCache settings,
			Map<Parameter, QuantileSummary> quantileSummaries,
			Map<Parameter, ConvergenceSummary> convergenceSummaries, 
			MultivariateNormalSummary relativeEffectsSummary, 
			RankProbabilitySummary rankProbabilitySummary, 
			List<TreatmentCategorySet> drugs) {
		super(builder, settings, quantileSummaries, convergenceSummaries);
		d_relativeEffectsSummary = relativeEffectsSummary;
		d_rankProbabilitySummary = rankProbabilitySummary;
		
		d_drugs = drugs;
		List<Pair<TreatmentCategorySet>> relEffects = getRelativeEffectsList();
		Parameter[] parameters = new Parameter[relEffects.size()]; 
		for (int i = 0; i < relEffects.size(); ++i) {
			Pair<TreatmentCategorySet> relEffect = relEffects.get(i);
			parameters[i] = getRelativeEffect(relEffect.getFirst(), relEffect.getSecond());
		}
	}

	@Override
	public MultivariateNormalSummary getRelativeEffectsSummary() {
		return d_relativeEffectsSummary;
	}

	@Override
	public RankProbabilitySummary getRankProbabilities() {
		return d_rankProbabilitySummary;
	}

	@Override
	public List<Pair<TreatmentCategorySet>> getRelativeEffectsList() {
		List<Pair<TreatmentCategorySet>> list = new ArrayList<Pair<TreatmentCategorySet>>(d_drugs.size() - 1); // first TreatmentCategorySet is baseline-> excluded
		for (int i = 0; i < d_drugs.size() - 1; ++i) {
			Pair<TreatmentCategorySet> relEffect = new Pair<TreatmentCategorySet>(d_drugs.get(0), d_drugs.get(i + 1));
			list.add(relEffect);
		}
		return list;
	}

	@Override
	public String getDescription() {
		return "Consistency Model";
	}
}
