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

import java.util.Arrays;
import java.util.Map;

import org.drugis.addis.entities.treatment.TreatmentDefinition;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.mtc.MCMCSettingsCache;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.parameterization.BasicParameter;
import org.drugis.mtc.parameterization.ParameterComparator;
import org.drugis.mtc.parameterization.RandomEffectsVariance;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

public abstract class AbstractSavedWrapper extends AbstractObservable implements MTCModelWrapper  {
	protected NetworkBuilder<TreatmentDefinition> d_builder;
	private final MCMCSettingsCache d_settings;
	protected final Map<Parameter, QuantileSummary> d_quantileSummaries;
	protected final Map<Parameter, ConvergenceSummary> d_convergenceSummaries;
	private boolean d_destroy;

	public AbstractSavedWrapper(NetworkBuilder<TreatmentDefinition> builder, MCMCSettingsCache settings, 
			Map<Parameter, QuantileSummary> quantileSummaries, Map<Parameter, ConvergenceSummary> convergenceSummaries) {
		d_builder = builder;
		d_settings = settings;
		d_quantileSummaries = quantileSummaries;
		d_convergenceSummaries = convergenceSummaries; 
	}

	@Override
	public Parameter getRelativeEffect(TreatmentDefinition a, TreatmentDefinition b) {
		return new BasicParameter(d_builder.getTreatmentMap().get(a), d_builder.getTreatmentMap().get(b));
	}
	
	@Override
	public boolean isSaved() {
		return true;
	}
	
	@Override
	public boolean isApproved() {
		return true;
	}
	
	@Override
	public QuantileSummary getQuantileSummary(Parameter p) {
		return d_quantileSummaries.get(p);
	}

	@Override
	public ConvergenceSummary getConvergenceSummary(Parameter p) {
		return d_convergenceSummaries.get(p);
	}
	
	@Override
	public Parameter getRandomEffectsVariance() {
		for(Parameter p : d_quantileSummaries.keySet()) { 
			if(p instanceof RandomEffectsVariance) {
				return p;
			}
		}
		return null;
	}
	
	@Override
	public Parameter[] getParameters() { 
		Parameter[] parameters = d_convergenceSummaries.keySet().toArray(new Parameter[] {});
		Arrays.sort(parameters, new ParameterComparator());
		return parameters;
	}

	@Override
	public MixedTreatmentComparison getModel() {
		throw new UnsupportedOperationException("Saved MTC models do not have a MixedTreatmentComparison model.");
	}
	
	@Override
	public MCMCSettingsCache getSettings() {
		return d_settings;
	}
	
	@Override
	public void selfDestruct() {
		d_destroy = true;
		firePropertyChange(PROPERTY_DESTROYED, false, true);
	}
	
	@Override
	public boolean getDestroyed() { 
		return d_destroy;
	}
}
