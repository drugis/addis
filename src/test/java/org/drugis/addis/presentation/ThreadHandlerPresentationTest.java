package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.util.threading.ThreadHandler;
import org.drugis.addis.util.threading.ThreadHandlerIT;
import org.drugis.addis.util.threading.ThreadHandlerIT.SuspendableTestThread;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class ThreadHandlerPresentationTest{
	class QueueListener implements PropertyChangeListener {
		int d_curVal = 0;

		public void propertyChange(PropertyChangeEvent evt) {
			d_curVal = (Integer) evt.getNewValue();
		}
	}
	
	ThreadHandlerPresentation d_pmThreadHandler = new ThreadHandlerPresentation();
	
	@Test
	public void testRunningThreads(){
		ThreadHandlerIT.waitTillDone();
		
		ValueModel queuedValueModel = d_pmThreadHandler.getThreadsInQueue();
		ValueModel runningValueModel = d_pmThreadHandler.getRunningThreads();
		
		QueueListener runningQueueListener = new QueueListener();
		runningValueModel.addValueChangeListener(runningQueueListener);
		QueueListener waitingQueueListener = new QueueListener();
		queuedValueModel.addValueChangeListener(waitingQueueListener);
		
		int numCores = Runtime.getRuntime().availableProcessors();
		
		for(int i=0; i < numCores+2; i++)
			ThreadHandler.getInstance().scheduleTask(new SuspendableTestThread(50));
		
		assertEquals(numCores,runningValueModel.getValue());
		assertEquals(runningValueModel.getValue(), runningQueueListener.d_curVal);
		
		assertEquals(2,queuedValueModel.getValue());
		assertEquals(queuedValueModel.getValue(), waitingQueueListener.d_curVal);
		
		ThreadHandlerIT.waitTillDone();
		
		assertEquals(0,runningValueModel.getValue());
		assertEquals(runningValueModel.getValue(), runningQueueListener.d_curVal);
		
		assertEquals(0,queuedValueModel.getValue());
		assertEquals(queuedValueModel.getValue(), waitingQueueListener.d_curVal);
	}
	
}
