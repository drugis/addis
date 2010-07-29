package org.drugis.addis.util.threading;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class ThreadHandler {
	
	/* Separate thread to check whether new threads can be started */
	private class RunQueueCleaner implements Runnable {
		public void run(){
			while(true) {
				synchronized (d_runningTasks) {
					for (int i=0; i<d_runningTasks.size(); ++i) {
						SuspendableThreadWrapper t = d_runningTasks.get(i);
						if (t.isTerminated()) {
							d_runningTasks.remove(i);
//							System.out.println("Task finished " + t);
							if (!d_scheduledTasks.isEmpty()) {
								SuspendableThreadWrapper newThread = d_scheduledTasks.removeFirst();
								newThread.start();
//								System.out.println("Executing from schedule " + newThread);
								d_runningTasks.addFirst(newThread);
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
	LinkedList<SuspendableThreadWrapper> d_runningTasks;
	Thread d_cleaner;
	private static ThreadHandler d_singleton;
	
	private ThreadHandler() {
		d_numCores = Runtime.getRuntime().availableProcessors();
		d_scheduledTasks = new LinkedList<SuspendableThreadWrapper>();
		d_runningTasks = new LinkedList<SuspendableThreadWrapper>();
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
	
	public synchronized void scheduleTasks(Collection<Runnable> newTasks) {
		synchronized (d_runningTasks) {
			LinkedList<SuspendableThreadWrapper> toAdd = getWrappers(newTasks);
			
//			System.out.println("newTasks " + newTasks);
			
			/* If tasks already present, reschedule to running or to head of queue  */
			toAdd.removeAll(d_runningTasks);
			d_scheduledTasks.removeAll(toAdd);
			
			
			// remove N=t.size() tasks from running and stack them in scheduledTasks (take various sizes into account)
			int toStack = Math.min(toAdd.size() - (d_numCores - d_runningTasks.size()), d_runningTasks.size()); // needed cores - available cores = cores that need to be pre-empted.
			for(int tasksToReplace=0 ; tasksToReplace < toStack ; ++tasksToReplace ) {
				for (int runQueIndex = d_runningTasks.size()-1; runQueIndex >= 0; --runQueIndex)
				{
					if (d_runningTasks.get(runQueIndex).suspend()) {
						SuspendableThreadWrapper runningThread = d_runningTasks.remove(runQueIndex);
						d_scheduledTasks.addFirst(runningThread);
						//	System.out.println("moving back to scheduler " + runningThread); 
						break;
					}
				}
			}

			// execute numCores tasks from t 
			toStack = Math.min(d_numCores - d_runningTasks.size() , toAdd.size() ) ;
			for(int i=0; i<toStack; ++i) {
				SuspendableThreadWrapper newRunning = toAdd.removeFirst();
				d_runningTasks.addFirst(newRunning);
				newRunning.start();
//				System.out.println("executing " + newRunning + " running size " + d_runningTasks.size()); 
			}

			// stack remaining threads from t in scheduledTasks
			for(SuspendableThreadWrapper m : toAdd) {
				d_scheduledTasks.addFirst(m);
//				System.out.println("Scheduling " + m);
			}
			
//			System.out.println("after Schedule queue " + d_scheduledTasks);
//			System.out.println("after Running queue " + d_runningTasks);
		}
	}
	
	// FIXME: Fix with Map, don't forget to use WeakReference for both keys and values.
	private LinkedList<SuspendableThreadWrapper> getWrappers(Collection<Runnable> newRunnables) {
		/* Check whether Runnable already exists in scheduled tasks, so we can reschedule*/
		LinkedList<SuspendableThreadWrapper> newList = new LinkedList<SuspendableThreadWrapper>();
		for (Runnable r : newRunnables) {
			boolean found = false;
			LinkedList<SuspendableThreadWrapper> examineList = new LinkedList<SuspendableThreadWrapper>();
			examineList.addAll(d_scheduledTasks);
			examineList.addAll(d_runningTasks);
			for (SuspendableThreadWrapper w : examineList) {
				if (w.getRunnable().equals(r)) {
					newList.add(w);
//					System.out.println("Adding already existing thread to wrapper list " + w);
					found = true;
				}
			}
			if (!found)
				newList.add(new SuspendableThreadWrapper(r));
		}
		
		return newList;
	}
}
