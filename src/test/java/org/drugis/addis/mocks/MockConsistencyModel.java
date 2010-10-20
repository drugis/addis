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

package org.drugis.addis.mocks;


import java.util.Collections;

import org.drugis.common.threading.SimpleSuspendableTask;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.activity.ActivityModel;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.common.threading.activity.DirectTransition;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.Treatment;



public class MockConsistencyModel implements ConsistencyModel {

	boolean d_ready = false;
	private ActivityTask d_task;
	
	public MockConsistencyModel() {
		Task start = new SimpleSuspendableTask(new Runnable() { public void run() {} });
		Task end = new SimpleSuspendableTask(new Runnable() { public void run() {} });
		d_task = new ActivityTask(new ActivityModel(start, end, 
				Collections.singleton(new DirectTransition(start, end))));
	}
	
	public class MockEstimate implements Estimate {
		public double getStandardDeviation() {
			return 0.33333;
		}
		public double getMean() {
			return 1.0;
		}
	}
	
	public Estimate getConsistency() {
		return new MockEstimate();
	}

	public Estimate getRelativeEffect(Treatment base, Treatment subj) {
		return new MockEstimate();
	}

	public boolean isReady() {
		return d_task.isFinished();
	}

	public int getBurnInIterations() {
		return -1;
	}

	public int getSimulationIterations() {
		return -1;
	}

	public void setBurnInIterations(int it) {
	}

	public void setSimulationIterations(int it) {
	}

	public double rankProbability(Treatment t, int r) {
		// TODO Auto-generated method stub
		return 0;
	}

	public ActivityTask getActivityTask() {
		return d_task;
	}
	
}