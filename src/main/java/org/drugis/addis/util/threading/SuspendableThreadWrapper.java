package org.drugis.addis.util.threading;

import java.lang.Thread.State;

public class SuspendableThreadWrapper {
	private Thread d_thread;
	private final Runnable d_runnable;

	SuspendableThreadWrapper(Runnable runnable) {
		d_runnable = runnable;
		//d_thread = new Thread(runnable);
	}
	
	public synchronized void start() {
		if (d_thread == null) {
			startAsNewThread();
		} else if (d_runnable instanceof SuspendableRunnable) {
			resumeThread();
		} else {
			throw new RuntimeException("Thread already running and not suspendable.");
		}
	}
	
	public synchronized boolean suspend() {
		if (d_thread == null) {
			throw new IllegalStateException("Thread not started yet");
		} else if (d_runnable instanceof SuspendableRunnable) {
			((SuspendableRunnable) d_runnable).suspend();
			return true;
		} else {
			return false;
		}
	}

	public boolean isTerminated() {
		if (d_thread == null)
			return false;
		return d_thread.getState() == State.TERMINATED ;
	}
	
	private void startAsNewThread() {
		d_thread = new Thread(d_runnable);
		d_thread.start();
		//d_thread.si
	}
	
	private void resumeThread() {
		SuspendableRunnable susRunnable = (SuspendableRunnable) d_runnable;
		if (susRunnable.isThreadSuspended())
			susRunnable.wakeUp();
		else {
			throw new RuntimeException("Thread already running.");
		}
	}
}
