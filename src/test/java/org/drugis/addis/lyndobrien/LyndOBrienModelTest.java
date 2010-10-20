package org.drugis.addis.lyndobrien;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.drugis.addis.lyndobrien.BenefitRiskDistribution.Sample;
import org.drugis.addis.util.threading.TaskUtil;
import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.common.threading.event.TaskProgressEvent;
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
