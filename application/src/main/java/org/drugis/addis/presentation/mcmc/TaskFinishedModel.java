/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

package org.drugis.addis.presentation.mcmc;

import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;

import com.jgoodies.binding.value.AbstractValueModel;

@SuppressWarnings("serial")
public class TaskFinishedModel extends AbstractValueModel implements ValueHolder<Boolean>, TaskListener {
	private boolean d_val;

	public TaskFinishedModel(Task task) {
		d_val = task.isFinished() || task.isAborted();
		task.addTaskListener(this);
	}

	public Boolean getValue() {
		return d_val;
	}

	public void setValue(Object newValue) {
		throw new IllegalAccessError("MCMCModelFinished is read-only");
	}

	@Override
	public void taskEvent(TaskEvent event) {
		boolean oldval = d_val;
		Task t = event.getSource();
		d_val = t.isFinished() || t.isAborted();
		fireValueChange(oldval, d_val);
	}
}