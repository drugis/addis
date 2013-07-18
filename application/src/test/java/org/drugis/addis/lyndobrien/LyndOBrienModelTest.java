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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.drugis.addis.lyndobrien.BenefitRiskDistribution.Sample;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.TaskUtil;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskProgressEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.junit.Before;
import org.junit.Test;

public class LyndOBrienModelTest {
	private static final double EPSILON = 0.00000000001;
	private MockBenefitRiskDistr d_mockDistr;
	
	@Before
	public void setUp() throws IOException {
		d_mockDistr = new MockBenefitRiskDistr();	
	}

	@Test
	public void testDataIdenticalToSamples() throws InterruptedException {
		LyndOBrienModel model = new LyndOBrienModelImpl(d_mockDistr);
		TaskUtil.run(model.getTask());
		for (int i = 0; i < model.getSimulationIterations(); ++i) {
			Sample s = model.getData(i);
			assertEquals(d_mockDistr.getSamples()[0][i], s.benefit, EPSILON);
			assertEquals(d_mockDistr.getSamples()[1][i], s.risk, EPSILON);
		}
	}
	
	@Test
	public void testPValuesCorrect() throws InterruptedException {
		LyndOBrienModel model = new LyndOBrienModelImpl(d_mockDistr);
		TaskUtil.run(model.getTask());
		double[] expectedpvals = {0.2876666666666667, 0.3506666666666667, 0.44433333333333336, 0.5286666666666666, 0.585};
		int i = 0;
		for(double mu = 0.25; mu < 8; mu *= 2) {
			assertEquals(expectedpvals[i++], model.getPValue(mu), EPSILON);
		}
	}
	
	@Test
	public void testDataAfterEachInterval() throws InterruptedException {
		final LyndOBrienModel model = new LyndOBrienModelImpl(d_mockDistr);
		Task task = model.getTask();
		task.addTaskListener(new TaskListener() {
			public void taskEvent(TaskEvent event) {
				if (event.getType() == EventType.TASK_PROGRESS) {
					TaskProgressEvent progress = (TaskProgressEvent)event;
					for (int i = 0; i < progress.getIteration(); ++i) {
						Sample s = model.getData(i);
						assertEquals(d_mockDistr.getSamples()[0][i], s.benefit, EPSILON);
						assertEquals(d_mockDistr.getSamples()[1][i], s.risk, EPSILON);
					}
					boolean exception = false;
					try {
						model.getData(progress.getIteration());
					} catch (Exception e) {
						exception = true;
					}
					assertTrue(exception);
				}
			}
		});
		TaskUtil.run(task);
	}
}
