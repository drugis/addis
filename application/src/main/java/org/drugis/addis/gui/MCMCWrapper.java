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

package org.drugis.addis.gui;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.gui.task.TaskProgressModel;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCModel;

public abstract class MCMCWrapper implements Comparable<MCMCWrapper> {
	private final MCMCModel d_model;
	private final String d_name;
	protected final OutcomeMeasure d_om;
	private TaskProgressModel d_taskProgressModel;

	public MCMCWrapper(MCMCModel model, OutcomeMeasure om, String name) { 
		d_model = model;
		d_om = om;
		d_taskProgressModel = new TaskProgressModel(getActivityTask());
		d_name = name;
	}
	
	public ActivityTask getActivityTask() {
		return d_model.getActivityTask();
	}

	public TaskProgressModel getProgressModel() {
		return d_taskProgressModel;
	}
	
	public MCMCModel getModel() {
		return d_model;
	} 

	public abstract ValueHolder<Boolean> isModelConstructed();	

	public OutcomeMeasure getOutcomeMeasure() {
		return d_om;
	}
	
	@Override
	public String toString() { 
		return d_name;
	}
}
