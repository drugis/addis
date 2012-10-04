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

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.drugis.common.threading.TaskUtil;
import org.drugis.common.threading.event.TaskProgressEvent;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.junit.Before;
import org.junit.Test;

public class ScatterPlotDataSetTest {
	private MockBenefitRiskDistr d_mockDistr;
	private LyndOBrienModelImpl d_model;
	
	@Before
	public void setUp() throws IOException {
		d_mockDistr = new MockBenefitRiskDistr();		
		d_model = new LyndOBrienModelImpl(d_mockDistr);
	}

	@Test
	public void testInitialValues() {
		ScatterPlotDataset set = new ScatterPlotDataset(d_model);
		assertEquals(0, set.getItemCount(0));
		assertEquals(1, set.getSeriesCount());
	}
	
	@Test
	public void testDataSetContentsAfterRun() throws InterruptedException {
		ScatterPlotDataset set = new ScatterPlotDataset(d_model);
		TaskUtil.run(d_model.getTask());
		assertEquals(d_model.getSimulationIterations(), set.getItemCount(0));
		for(int i = 0; i < set.getItemCount(0); ++i) {
			assertEquals(d_model.getData(i).benefit, set.getX(0, i));
			assertEquals(d_model.getData(i).risk, set.getY(0, i));
		}
	}
	
	@Test
	public void testDataSetChangedOnSimulationProgress() {
		ScatterPlotDataset set = new ScatterPlotDataset(d_model);
		
		DatasetChangeListener mock = createStrictMock(DatasetChangeListener.class);
		mock.datasetChanged((DatasetChangeEvent)anyObject());
		replay(mock);

		set.addChangeListener(mock);
		set.taskEvent(new TaskProgressEvent(d_model.getTask(), 100, 3000));
    	verify(mock);
	}
	
	@Test
	public void testDataSetAfterEachInterval() {
		final ScatterPlotDataset set = new ScatterPlotDataset(d_model);

		final int n = 200;

		set.addChangeListener(new DatasetChangeListener() {
			public void datasetChanged(DatasetChangeEvent arg0) {
				assertEquals(n, set.getItemCount(0));
			}
		});

		set.taskEvent(new TaskProgressEvent(d_model.getTask(), n, 3000));
	}
	
	@Test
	public void testInitializeDataSetWithFinishedSimulation() throws InterruptedException {
		TaskUtil.run(d_model.getTask());
		ScatterPlotDataset set = new ScatterPlotDataset(d_model);
		assertEquals(3000, set.getItemCount(0));
	}
}
