package org.drugis.addis.lyndobrien;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.drugis.addis.lyndobrien.BenefitRiskDistribution.Sample;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;
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
	public void testDataIdenticalToSamples() {
		LyndOBrienModel model = new LyndOBrienModelImpl(d_mockDistr);
		model.run();
		for (int i = 0; i < model.getSimulationIterations(); ++i) {
			Sample s = model.getData(i);
			assertEquals(d_mockDistr.getSamples()[0][i], s.benefit, EPSILON);
			assertEquals(d_mockDistr.getSamples()[1][i], s.risk, EPSILON);
		}
	}
	
	@Test
	public void testModelFiresEvents() {
		LyndOBrienModel model = new LyndOBrienModelImpl(d_mockDistr);
    	ProgressListener mock = createStrictMock(ProgressListener.class);
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
	
	@Test
	public void testDataAfterEachInterval() {
		final LyndOBrienModel model = new LyndOBrienModelImpl(d_mockDistr);
		model.addProgressListener(new ProgressListener() {
			public void update(MCMCModel mtc, ProgressEvent event) {
				if (event.getType() == EventType.SIMULATION_PROGRESS) {
					assertFalse(model.isReady());
					for (int i = 0; i < event.getIteration(); ++i) {
						Sample s = model.getData(i);
						assertEquals(d_mockDistr.getSamples()[0][i], s.benefit, EPSILON);
						assertEquals(d_mockDistr.getSamples()[1][i], s.risk, EPSILON);
					}
					boolean exception = false;
					try {
						model.getData(event.getIteration());
					} catch (Exception e) {
						exception = true;
					}
					assertTrue(exception);
				}
			}
		});
		model.run();
	}
}
