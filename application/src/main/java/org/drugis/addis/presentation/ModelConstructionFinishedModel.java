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

package org.drugis.addis.presentation;

import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.mtc.MixedTreatmentComparison;

public class ModelConstructionFinishedModel extends UnmodifiableHolder<Boolean> implements TaskListener {
	private static final long serialVersionUID = 8539051214083665626L;
	private Task d_task;
	public ModelConstructionFinishedModel(MixedTreatmentComparison model) {
		super(model.getActivityTask().getModel().getStartState().isFinished());
		d_task = model.getActivityTask().getModel().getStartState();
		d_task.addTaskListener(this);
	}

	public void taskEvent(TaskEvent event) {
		if (event.getType().equals(EventType.TASK_FINISHED)) {
			fireValueChange(false, true);
		}
	}

	public Boolean getValue() {
		return d_task.isFinished();
	}
}