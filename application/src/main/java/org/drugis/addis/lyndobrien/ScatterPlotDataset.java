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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

package org.drugis.addis.lyndobrien;

import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskProgressEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.jfree.data.xy.AbstractXYDataset;

@SuppressWarnings("serial")
public class ScatterPlotDataset extends AbstractXYDataset implements TaskListener {

	private final LyndOBrienModel d_model;
	private int d_itemCount = 0;
	private int d_seriesCount = 1;

	public ScatterPlotDataset(LyndOBrienModel model){
		d_model = model;
		model.getTask().addTaskListener(this);
		if(model.getTask().isFinished()) {
			d_itemCount = model.getSimulationIterations();
		}
	}
	
	@Override
	public int getSeriesCount() {
		return d_seriesCount ;
	}

	@Override
	public Comparable<?> getSeriesKey(int series) {
		return 0;
	}

	public int getItemCount(int series) {
		return d_itemCount;
	}

	public Number getX(int series, int item) {
		return d_model.getData(item).benefit;
	}

	public Number getY(int series, int item) {
		return d_model.getData(item).risk;
	}

	public void taskEvent(TaskEvent event) {
		if(event.getType() == EventType.TASK_PROGRESS) {
			d_itemCount = ((TaskProgressEvent)event).getIteration();
			fireDatasetChanged();
		}
	}

}
