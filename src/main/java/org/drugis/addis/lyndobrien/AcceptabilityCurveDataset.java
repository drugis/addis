/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.jfree.data.xy.AbstractXYDataset;

@SuppressWarnings("serial")
public class AcceptabilityCurveDataset extends AbstractXYDataset implements TaskListener {

	private final LyndOBrienModel d_model;
	private int d_itemCount = 400;
	private int d_seriesCount = 1;
	private double[] d_data;
	
	public AcceptabilityCurveDataset(LyndOBrienModel model) {
		d_model = model;
		d_data = new double[d_itemCount];
		model.getTask().addTaskListener(this);
		if(model.getTask().isFinished()) {
			calcPvalues();
		}
	}

	private void calcPvalues() {
		int i = 0;
		for(double mu = 0.01; mu < 4; mu += 0.01){
			d_data[i++] = d_model.getPValue(mu);
		}
	}
	
	@Override
	public int getSeriesCount() {
		return d_seriesCount;
	}

	@Override
	public Comparable<?> getSeriesKey(int arg0) {
		return 1;
	}

	public void taskEvent(TaskEvent event) {
		if(event.getType() == EventType.TASK_FINISHED) {
			calcPvalues();
			fireDatasetChanged();
		}
	}


	public int getItemCount(int arg0) {
		return d_itemCount;
	}

	public Number getX(int series, int i) {
		return 0.01 * i + 0.01;
	}

	public Number getY(int series, int i) {
		return d_data[i];
	}

}
