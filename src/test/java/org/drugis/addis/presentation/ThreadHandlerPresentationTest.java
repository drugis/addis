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
