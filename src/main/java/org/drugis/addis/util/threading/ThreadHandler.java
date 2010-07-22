package org.drugis.addis.util.threading;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class ThreadHandler {
	
	/* Separate thread to check whether new threads can be started */
	private class RunQueueCleaner implements Runnable {
		public void run(){
			while(true) {
				synchronized (d_running) {
					//Iterator<SuspendableThreadWrapper> it = d_running.iterator();
					for (int i=0; i<d_running.size(); ++i) {
					//while(it.hasNext()) {
						SuspendableThreadWrapper t = d_running.get(i);
						if (t.isTerminated()) {
							//it.remove();
							d_running.remove(i);
							if (!d_scheduledTasks.isEmpty()) {
								SuspendableThreadWrapper newThread = d_scheduledTasks.pop();
								newThread.start();
								d_running.add(i, newThread);
								//d_running.push(newThread);
							}
						}
					}
				}
				try {

					Thread.sleep(200);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		}
	} 

	private final int d_numCores;
	LinkedList<SuspendableThreadWrapper> d_scheduledTasks;
	LinkedList<SuspendableThreadWrapper> d_running;
	Thread d_cleaner;
	private static ThreadHandler d_singleton;
	
	private ThreadHandler() {
		d_numCores = Runtime.getRuntime().availableProcessors();
		d_scheduledTasks = new LinkedList<SuspendableThreadWrapper>();
		d_running = new LinkedList<SuspendableThreadWrapper>();
		d_cleaner = new Thread(new RunQueueCleaner());
		d_cleaner.start();
	}
	
	public static ThreadHandler getInstance() {
		if (d_singleton == null)
			d_singleton = new ThreadHandler();
		return d_singleton;
	}
	
	public void scheduleTask(Runnable r) {
		scheduleTasks(Collections.singleton(r));
	}
	
	synchronized public void scheduleTasks(Collection<Runnable> t) {
		LinkedList<SuspendableThreadWrapper> toAdd = runnableToWrapper(t);

		synchronized (d_running) {
			// remove N=t.size() tasks from running and stack them in scheduledTasks (take various sizes into account)
			int toStack = Math.min(toAdd.size() - (d_numCores - d_running.size()), d_running.size()); // needed cores - available cores = cores that need to be pre-empted.
			for(int i=0 ; i < toStack ; ++i ) {
				if (d_running.peek().suspend()) {
					SuspendableThreadWrapper runningThread = d_running.pop();
					d_scheduledTasks.push(runningThread);
					System.out.println("moving back to scheduler " + runningThread); 
				}
			}

			// execute numCores tasks from t 
			toStack = Math.min(d_numCores - d_running.size() , toAdd.size() ) ;
			for(int i=0; i<toStack; ++i) {
				SuspendableThreadWrapper newRunning = toAdd.pop();
				newRunning.start();
				d_running.push(newRunning);
				System.out.println("executing " + newRunning); 
			}

			// stack remaining threads from t in scheduledTasks
			for(SuspendableThreadWrapper m : toAdd) {
				d_scheduledTasks.push(m);
				System.out.println("Scheduling " + m);
			}
		}
	}

	private LinkedList<SuspendableThreadWrapper> runnableToWrapper(
			Collection<Runnable> t) {
		LinkedList<SuspendableThreadWrapper> newList = new LinkedList<SuspendableThreadWrapper>();
		for (Runnable r : t)
			newList.add(new SuspendableThreadWrapper(r));
		return newList;
	}
	
	
}
