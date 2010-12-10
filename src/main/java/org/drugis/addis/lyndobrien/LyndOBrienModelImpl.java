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

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.lyndobrien.BenefitRiskDistribution.Sample;
import org.drugis.common.threading.AbstractIterativeComputation;
import org.drugis.common.threading.IterativeTask;
import org.drugis.common.threading.Task;

public class LyndOBrienModelImpl extends AbstractIterativeComputation implements LyndOBrienModel {
	private BenefitRiskDistribution d_brd;
	private static final int SIMULATION_ITERATIONS = 3000;
	private static final int REPORTING_INTERVAL = 100;
	private List<Sample> d_data;
	private IterativeTask d_task;

	public LyndOBrienModelImpl(BenefitRiskDistribution brd) {
		super(SIMULATION_ITERATIONS);
		d_brd = brd;
		d_data = new ArrayList<Sample>();
		d_task = new IterativeTask(this, "Lynd & O'Brien Simulation");
		d_task.setReportingInterval(REPORTING_INTERVAL);
	}

	public Sample getData(int arg0) {
		return d_data.get(arg0);
	}
	
	public String getXAxisName() {
		return d_brd.getBenefitAxisName();
	}
	
	public String getYAxisName() {
		return d_brd.getRiskAxisName();
	}

	public Double getPValue(double mu) {
		double belowMu = 0;
		for(Sample s: d_data) {
			if(s.benefit < 0) {
				if((s.risk / s.benefit) > mu) {
					++belowMu;
				}
			} else if (s.benefit > 0) {
				if((s.risk / s.benefit) < mu) {
					++belowMu;
				}
			} else {
				if (s.risk < 0) {
					++belowMu;
				}
			}
		}
		return belowMu / d_data.size();
	}

	public Task getTask() {
		return d_task;
	}

	@Override
	public void doStep() {
		d_data.add(d_brd.nextSample());		
	}

	public int getSimulationIterations() {
		return SIMULATION_ITERATIONS;
	}
}
