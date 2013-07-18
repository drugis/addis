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

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.lyndobrien.BenefitRiskDistribution.Sample;
import org.drugis.common.threading.AbstractIterativeComputation;
import org.drugis.common.threading.IterativeTask;
import org.drugis.common.threading.Task;

import fi.smaa.common.RandomUtil;

public class LyndOBrienModelImpl extends AbstractIterativeComputation implements LyndOBrienModel {
	private BenefitRiskDistribution d_brd;
	private static final int SIMULATION_ITERATIONS = 3000;
	private static final int REPORTING_INTERVAL = 100;
	private List<Sample> d_data;
	private IterativeTask d_task;
	private RandomUtil d_random;

	public LyndOBrienModelImpl(BenefitRiskDistribution brd) {
		super(SIMULATION_ITERATIONS);
		d_brd = brd;
		d_random = RandomUtil.createWithRandomSeed();
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
		final int n = d_data.size();
		for(int i = 0; i < n; ++i) {
			Sample s = d_data.get(i);
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
		return belowMu / n;
	}

	public Task getTask() {
		return d_task;
	}

	@Override
	public void doStep() {
		d_data.add(d_brd.nextSample(d_random));
	}

	public int getSimulationIterations() {
		return SIMULATION_ITERATIONS;
	}
}
