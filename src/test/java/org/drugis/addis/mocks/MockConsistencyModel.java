/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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


import org.drugis.common.threading.AbstractSuspendable;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.Treatment;



public class MockConsistencyModel extends AbstractSuspendable implements ConsistencyModel {

	boolean d_ready = false;
	
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

	public void addProgressListener(ProgressListener l) {
	}

	public Estimate getRelativeEffect(Treatment base, Treatment subj) {
		return new MockEstimate();
	}

	public boolean isReady() {
		return d_ready;
	}

	public void run() {
		d_ready = true;
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
	
}