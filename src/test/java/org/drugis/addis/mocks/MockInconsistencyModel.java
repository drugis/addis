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


import java.util.ArrayList;
import java.util.List;

import org.drugis.common.threading.AbstractSuspendable;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.InconsistencyParameter;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.Treatment;



public class MockInconsistencyModel extends AbstractSuspendable implements InconsistencyModel {

	boolean d_ready = false;
	
	public class MockEstimate implements Estimate {
		public double getStandardDeviation() {
			return 0.33333;
		}
		public double getMean() {
			return 1.0;
		}
	}
	
	public Estimate getInconsistency(InconsistencyParameter param) {
		return new MockEstimate();
	}

	@SuppressWarnings("unchecked")
	public List<InconsistencyParameter> getInconsistencyFactors() {

		List<Treatment> cycle = new ArrayList<Treatment>();
		cycle.add(new Treatment("Fluoxetine"));
		cycle.add(new Treatment("Sertraline"));
		cycle.add(new Treatment("Paroxetine"));
		cycle.add(new Treatment("Fluoxetine"));

		scala.collection.jcl.BufferWrapper<Treatment> wrapper =
			(scala.collection.jcl.BufferWrapper<Treatment>)
			scala.collection.jcl.Conversions$.MODULE$.convertList(cycle);
		scala.List<Treatment> scalaCycle = scala.List$.MODULE$.fromIterator(wrapper.elements());

		List<InconsistencyParameter> inFac = new ArrayList<InconsistencyParameter>();
		inFac.add(new InconsistencyParameter(scalaCycle));

		return inFac;
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
	
}