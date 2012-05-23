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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.DrugSet;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCSettingsCache;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.model.Treatment;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

public abstract class AbstractSimulationWrapper<MTCType extends MixedTreatmentComparison> extends AbstractObservable implements MTCModelWrapper {
	protected final MTCType d_nested;
	private final Map<Parameter, QuantileSummary> d_quantileSummaryMap = new HashMap<Parameter, QuantileSummary>();
	protected final NetworkBuilder<DrugSet> d_builder;
	private final Map<Parameter, ConvergenceSummary> d_convergenceSummaryMap = new HashMap<Parameter, ConvergenceSummary>();
	private boolean d_destroy = false;
	
	protected AbstractSimulationWrapper(NetworkBuilder<DrugSet> builder, MTCType mtc) { 
		d_builder = builder;
		d_nested = mtc;
	}
	
	@Override
	public Parameter getRelativeEffect(DrugSet a, DrugSet b) {
		return d_nested.getRelativeEffect(getTreatment(a), getTreatment(b));
	}
	
	@Override
	public ActivityTask getActivityTask() {
		return d_nested.getActivityTask();
	}
	
	@Override
	public MixedTreatmentComparison getModel() {
		return d_nested;
	}
	
	@Override
	public Parameter getRandomEffectsVariance() {
		return d_nested.getRandomEffectsVariance();
	}
	
	@Override
	public boolean hasSavedResults() { 
		return false;
	}

	@Override
	public boolean isSavable() {
		return getActivityTask().isFinished();
	}

	protected List<Treatment> getTreatments(List<DrugSet> drugs) {
		List<Treatment> treatments = new ArrayList<Treatment>();
		for (DrugSet d : drugs) {
			treatments.add(getTreatment(d));
		}
		return treatments;
	}

	protected Treatment getTreatment(DrugSet d) {
		return d_builder.getTreatmentMap().get(d);
	}
	
	@Override
	public MCMCSettingsCache getSettings() {
		return d_nested.getSettings();
	}
	
	@Override
	public QuantileSummary getQuantileSummary(Parameter p) {
		if(d_quantileSummaryMap.get(p) == null) { 
			d_quantileSummaryMap.put(p, new QuantileSummary(d_nested.getResults(), p));
		}
		return d_quantileSummaryMap.get(p);
	}
	
	@Override
	public ConvergenceSummary getConvergenceSummary(Parameter p) {
		if(d_convergenceSummaryMap.get(p) == null) { 
			d_convergenceSummaryMap.put(p, new ConvergenceSummary(d_nested.getResults(), p));
		}
		return d_convergenceSummaryMap.get(p);
	}
	
	@Override
	public Parameter[] getParameters() { 
		return d_nested.getResults().getParameters();
	}
	
	@Override
	public void selfDestruct() {
		d_destroy  = true;
		firePropertyChange(PROPERTY_DESTROYED, false, true);
	}
	
	@Override
	public boolean getDestroyed() { 
		return d_destroy;
	}

	@Override
	public String getName() {
		return d_nested.toString();
	}
	
}
