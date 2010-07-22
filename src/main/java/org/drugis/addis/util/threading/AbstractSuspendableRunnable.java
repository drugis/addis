package org.drugis.addis.util.threading;

public abstract class AbstractSuspendableRunnable implements SuspendableRunnable  {

	boolean d_threadSuspended = false;

	
	public synchronized boolean isThreadSuspended() {
		return d_threadSuspended;
	}
	
	public synchronized void suspend() {
		d_threadSuspended = true;
	}
	
	public synchronized void wakeUp() {
		d_threadSuspended = false;
		notify();
	}

	protected void waitIfSuspended() {
		while(isThreadSuspended()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}
}
