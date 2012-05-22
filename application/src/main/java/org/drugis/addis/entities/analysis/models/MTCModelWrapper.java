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

import org.drugis.addis.entities.DrugSet;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.ConvergenceSummary;
import org.drugis.mtc.summary.QuantileSummary;

import com.jgoodies.binding.beans.Observable;

public interface MTCModelWrapper extends MCMCModel, Observable {
	
	public static final String PROPERTY_DESTROYED = "destroyed";
	
	public Parameter[] getParameters();
	
	public Parameter getRelativeEffect(DrugSet a, DrugSet b);
	
	public Parameter getRandomEffectsVariance();

	public ActivityTask getActivityTask();
	
	public MixedTreatmentComparison getModel();
	
	public ConvergenceSummary getConvergenceSummary(Parameter p);

	public QuantileSummary getQuantileSummary(Parameter ip);
	
	public boolean isReady();
	
	public boolean hasSavedResults();

	public boolean isSavable(); 
	
	/** 
	 * Whether or not the model should be cleaned up on the next invocation from NetworkMetaAnalysis.
	 * This will cause NetworkMetaAnalysis to create a new instance of a AbstractSimulationModel.
	 */
	public void selfDestruct(); 
	
	/**
	 * Returns true if selfDestruct called previously, false otherwise.
	 */
	public boolean getDestroyed();
	
	public String getName();
}