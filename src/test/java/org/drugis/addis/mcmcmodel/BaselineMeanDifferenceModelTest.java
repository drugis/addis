package org.drugis.addis.mcmcmodel;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;
import org.junit.Test;

public class BaselineMeanDifferenceModelTest {
	@Test
	public void testBurnInIterations() {
		BaselineMeanDifferenceModel model = new BaselineMeanDifferenceModel(new ArrayList<ContinuousMeasurement>());
		assertTrue(model.getBurnInIterations() > 1000);
		int newIter = model.getBurnInIterations() * 2;
		model.setBurnInIterations(newIter);
		assertEquals(newIter, model.getBurnInIterations());
	}
	
	@Test
	public void testSimulationIterations() {
		BaselineMeanDifferenceModel model = new BaselineMeanDifferenceModel(new ArrayList<ContinuousMeasurement>());
		assertTrue(model.getSimulationIterations() > 1000);
		int newIter = model.getSimulationIterations() * 2;
		model.setSimulationIterations(newIter);
		assertEquals(newIter, model.getSimulationIterations());
	}
	
	@Test
	public void testEvents() {
		BaselineMeanDifferenceModel model = new BaselineMeanDifferenceModel(
				Collections.<ContinuousMeasurement>singletonList(new BasicContinuousMeasurement(0.5, 0.2, 100)));
		model.setBurnInIterations(500);
		model.setSimulationIterations(1000);
    	ProgressListener mock = createStrictMock(ProgressListener.class);
    	mock.update(model, new ProgressEvent(EventType.MODEL_CONSTRUCTION_STARTED));
    	mock.update(model, new ProgressEvent(EventType.MODEL_CONSTRUCTION_FINISHED));
    	mock.update(model, new ProgressEvent(EventType.BURNIN_STARTED));
    	for (int i = 100; i < model.getBurnInIterations(); i += 100) {
	    	mock.update(model, new ProgressEvent(EventType.BURNIN_PROGRESS, i, model.getBurnInIterations()));
    	}
    	mock.update(model, new ProgressEvent(EventType.BURNIN_FINISHED));
    	mock.update(model, new ProgressEvent(EventType.SIMULATION_STARTED));
    	for (int i = 100; i < model.getSimulationIterations(); i += 100) {
	    	mock.update(model, new ProgressEvent(EventType.SIMULATION_PROGRESS, i, model.getSimulationIterations()));
    	}
    	mock.update(model, new ProgressEvent(EventType.SIMULATION_FINISHED));
    	replay(mock);
    	model.addProgressListener(mock);
    	model.run();
    	verify(mock);
	}
}
