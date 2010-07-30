package org.drugis.addis.util.threading;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class ThreadHandlerTest {

	class SuspendableTestThread extends AbstractSuspendableRunnable{
		
		private final int d_ms;
		boolean d_done;

		public SuspendableTestThread(int ms) {
			d_ms = ms;
		}
		
		public synchronized boolean getDone() {
			return d_done;
		}
		
		@Override
		public String toString() {
			return ""+d_ms;
		}
		
		public void run() {
			if (d_done)
				throw new IllegalStateException("Thread already done.");
			try {
				Thread.sleep(d_ms);
				waitIfSuspended();
			} catch (InterruptedException e) {
			}
			d_done = true;
		}
		
		// FIXME: Hacky.
		@Override
		public boolean equals(Object o) {
			if (o instanceof SuspendableThreadWrapper)
				return ((SuspendableThreadWrapper) o).getRunnable().equals(this);
			else if (o instanceof SuspendableTestThread)
				return super.equals(o);
			return false;
		}
	};
	
	class NonSuspendableTestThread implements Runnable{
		
		private final int d_ms;
		boolean d_done;

		public NonSuspendableTestThread(int ms) {
			d_ms = ms;
		}
		
		public synchronized boolean getDone() {
			return d_done;
		}
		
		@Override
		public String toString() {
			return ""+d_ms;
		}
		
		public void run() {
			if (d_done)
				throw new IllegalStateException("Thread already done.");
			try {
				Thread.sleep(d_ms);
			} catch (InterruptedException e) {
			}
			d_done = true;
		}
	};
	
	@Test
	public void testQueueingOrder() {
		LinkedList<Runnable> ToDo1 = new LinkedList<Runnable>();
		LinkedList<Runnable> ToDo2 = new LinkedList<Runnable>();
		
		SuspendableTestThread tmp1 = new SuspendableTestThread(100);
		SuspendableTestThread tmp2 = new SuspendableTestThread(200);
		SuspendableTestThread tmp3 = new SuspendableTestThread(300);
		SuspendableTestThread tmp4 = new SuspendableTestThread(400);
		SuspendableTestThread tmp5 = new SuspendableTestThread(500);
		SuspendableTestThread tmp6 = new SuspendableTestThread(600);
		SuspendableTestThread tmp7 = new SuspendableTestThread(700);
		
		ToDo1.add(tmp1);
		ToDo1.add(tmp2);
		ToDo1.add(tmp3);
		ToDo1.add(tmp4);

		ToDo2.add(tmp5);
		ToDo2.add(tmp6);
		ToDo2.add(tmp7);

		ThreadHandler th = ThreadHandler.getInstance();
		
		th.scheduleTasks(ToDo1);
		th.scheduleTasks(ToDo2);
		assertTrue(th.d_scheduledTasks.contains(tmp1));
		assertTrue(th.d_runningTasks.contains(tmp6));
	}
	

	
	@Test
	public void testReprioritise() {
		LinkedList<Runnable> ToDo1 = new LinkedList<Runnable>();
						
		int numCores = Runtime.getRuntime().availableProcessors();
		final int NUMMODELS = numCores + 2;
		
		for(int i=0; i < NUMMODELS; ++i) {
			ToDo1.add(new SuspendableTestThread((i+1) * 300));
		}
		
		ThreadHandler th = ThreadHandler.getInstance();
		th.scheduleTasks(ToDo1);
		
		List<Runnable> nCoresHeadList = ToDo1.subList(0,numCores);
		List<Runnable> nCoresHeadListComplement = ToDo1.subList(numCores, numCores + (NUMMODELS - numCores));

		assertTrue(th.d_runningTasks.containsAll(nCoresHeadList));
		assertTrue(th.d_scheduledTasks.containsAll(nCoresHeadListComplement));
		
		// Note: NOP; rescheduling already-running tasks should not change anything
		th.scheduleTasks(nCoresHeadList);
		assertTrue(th.d_runningTasks.containsAll(nCoresHeadList));
		assertTrue(th.d_scheduledTasks.containsAll(nCoresHeadListComplement));
				

		// reprioritise scheduled tasks by re-adding them; should displace running tasks
		th.scheduleTasks(nCoresHeadListComplement);
//		System.out.println(nCoresHeadListComplement);
//		System.out.println(nCoresHeadList);		
		
		List<Runnable> nCoresTailList = ToDo1.subList((NUMMODELS - numCores), numCores + (NUMMODELS - numCores));
		List<Runnable> nCoresTailListComplement = ToDo1.subList(0,(NUMMODELS - numCores));
		assertTrue(th.d_runningTasks.containsAll(nCoresTailList));
		assertTrue(th.d_scheduledTasks.containsAll(nCoresTailListComplement));
	}
	
	@Test
	public void testHighLoad() {
		final int NUMTHREADS = 100;
		LinkedList<Runnable> runnables = new LinkedList<Runnable>();
		ThreadHandler th = ThreadHandler.getInstance();
		ArrayList<SuspendableTestThread> threadList = new ArrayList<SuspendableTestThread>(NUMTHREADS);
		
		for (int i=0; i<NUMTHREADS; ++i) {
			SuspendableTestThread mod = new SuspendableTestThread((int) (Math.random() * 100));
			threadList.add(mod);
			runnables.add(mod);
			if ((Math.random() > 0.75) || (i == (NUMTHREADS-1))) {
				th.scheduleTasks(runnables);
				runnables.clear();
			}
			sleep((int) (Math.random() * 50));
			assertTrue(Runtime.getRuntime().availableProcessors() >= th.d_runningTasks.size());
//			System.out.println("running: " + th.d_runningTasks.size() + " scheduled: "+th.d_scheduledTasks.size());
		}
		
		assertTrue(Runtime.getRuntime().availableProcessors() >= th.d_runningTasks.size());
		waitTillDone();
		for (SuspendableTestThread mod : threadList) {
//			System.out.println(mod + " " + mod.getDone());
			assertTrue(mod.getDone());
		}
	}
	
	@Test
	public void testTakeSuspendableSlot() {
		waitTillDone();
		ThreadHandler th = ThreadHandler.getInstance();
		//ArrayList<FakeModel> threadList = new ArrayList<FakeModel>();
		// This works.
		int numCores = Runtime.getRuntime().availableProcessors();
		
		th.scheduleTask(new SuspendableTestThread(100));
		for (int i=0; i<numCores -1; ++i)
			th.scheduleTask(new NonSuspendableTestThread(100));
		SuspendableTestThread newThread = new SuspendableTestThread(50);
		th.scheduleTask(newThread);
		assertTrue(th.d_runningTasks.contains(newThread));
		assertFalse(th.d_scheduledTasks.contains(newThread));
		waitTillDone();
		
		for (int i=0; i<numCores -1; ++i)
			th.scheduleTask(new NonSuspendableTestThread(100));
		th.scheduleTask(new SuspendableTestThread(100));
		newThread = new SuspendableTestThread(50);
		th.scheduleTask(newThread);
		assertTrue(th.d_runningTasks.contains(newThread));
		assertFalse(th.d_scheduledTasks.contains(newThread));
		waitTillDone();
	}
	
	private void waitTillDone() {
		ThreadHandler th = ThreadHandler.getInstance();
		while ((th.d_runningTasks.size() > 0) || (th.d_scheduledTasks.size() > 0)) {
//			System.out.println("running: " + th.d_runningTasks.size() + " scheduled: "+th.d_scheduledTasks.size());
			sleep(100);
		}
	}
	

	private void sleep(int ms ) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
