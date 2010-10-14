package org.drugis.addis.lyndobrien;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressEvent.EventType;
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
	public void testDataSetContentsAfterRun() {
		ScatterPlotDataset set = new ScatterPlotDataset(d_model);
		d_model.run();
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
		set.update(d_model, new ProgressEvent(EventType.SIMULATION_PROGRESS, 100, 3000));
    	verify(mock);
	}
	
	@Test
	public void testDataSetChangedOnSimulationFinished() {
		ScatterPlotDataset set = new ScatterPlotDataset(d_model);
		
		DatasetChangeListener mock = createStrictMock(DatasetChangeListener.class);
		mock.datasetChanged((DatasetChangeEvent)anyObject());
		replay(mock);

		set.addChangeListener(mock);
		set.update(d_model, new ProgressEvent(EventType.SIMULATION_FINISHED));
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

		set.update(d_model, new ProgressEvent(EventType.SIMULATION_PROGRESS, n, 3000));
	}
	
	@Test
	public void testInitializeDataSetWithFinishedSimulation() {
		d_model.run();
		ScatterPlotDataset set = new ScatterPlotDataset(d_model);
		assertEquals(3000, set.getItemCount(0));
	}
}
