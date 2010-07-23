package org.drugis.addis.util.threading;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Test;

public class ThreadHandlerTest {

	class FakeModel extends AbstractSuspendableRunnable{
		
		private final int d_ms;
		boolean d_done;

		public FakeModel(int ms) {
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
				return ((SuspendableThreadWrapper) o).d_runnable.equals(this);
			else if (o instanceof FakeModel)
				return (((FakeModel) o).d_ms == d_ms && ((FakeModel) o).d_done == d_done);
			return false;
		}
	};
	
	@Test
	public void testQueueingOrder() {
		
		LinkedList<Runnable> ToDo1 = new LinkedList<Runnable>();
		LinkedList<Runnable> ToDo2 = new LinkedList<Runnable>();
		
		FakeModel tmp1 = new FakeModel(100);
		FakeModel tmp2 = new FakeModel(200);
		FakeModel tmp3 = new FakeModel(300);
		FakeModel tmp4 = new FakeModel(400);
		FakeModel tmp5 = new FakeModel(500);
		FakeModel tmp6 = new FakeModel(600);
		FakeModel tmp7 = new FakeModel(700);
		
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
		assertTrue(th.d_running.contains(tmp6));
		waitTillDone();
	}
	
	@Test
	public void testHighLoad() {
		
		
		final int NUMTHREADS = 100;
		LinkedList<Runnable> runnables = new LinkedList<Runnable>();
		ThreadHandler th = ThreadHandler.getInstance();
		ArrayList<FakeModel> threadList = new ArrayList<FakeModel>(NUMTHREADS);
		
		for (int i=0; i<NUMTHREADS; ++i) {
			FakeModel mod = new FakeModel((int) Math.random() * 100);
			threadList.add(mod);
			runnables.add(mod);
			if ((Math.random() > 0.75) || (i == (NUMTHREADS-1))) {
				th.scheduleTasks(runnables);
				runnables.clear();
			}
			sleep((int) (Math.random() * 50));
			assertTrue(Runtime.getRuntime().availableProcessors() >= th.d_running.size());
//			System.out.println("running: " + th.d_running.size() + " scheduled: "+th.d_scheduledTasks.size());
		}
		
		assertTrue(Runtime.getRuntime().availableProcessors() >= th.d_running.size());
		waitTillDone();
		for (FakeModel mod : threadList) {
//			System.out.println(mod + " " + mod.getDone());
			assertTrue(mod.getDone());
		}
	}
	
	private void waitTillDone() {
		ThreadHandler th = ThreadHandler.getInstance();
		while ((th.d_running.size() > 0) || (th.d_scheduledTasks.size() > 0)) {
//			System.out.println("running: " + th.d_running.size() + " scheduled: "+th.d_scheduledTasks.size());
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
