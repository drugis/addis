package org.drugis.addis.util.threading;

public interface SuspendableRunnable extends Runnable{

	public boolean isThreadSuspended();
	public void suspend();
	public void wakeUp();
	
}