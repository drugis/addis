package org.drugis.addis.presentation;

import org.drugis.addis.util.threading.ThreadHandler;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;

@SuppressWarnings("serial")
public class ThreadHandlerPresentation extends PresentationModel<ThreadHandler> {

	public ThreadHandlerPresentation() {
		super(ThreadHandler.getInstance());
	}
	
	public ValueModel getRunningThreads(){
		return getModel(ThreadHandler.PROPERTY_RUNNING_THREADS);
	}
	
	public ValueModel getThreadsInQueue(){
		return getModel(ThreadHandler.PROPERTY_QUEUED_THREADS);
	}
	
}
