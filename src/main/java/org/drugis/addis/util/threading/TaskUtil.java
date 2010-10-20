package org.drugis.addis.util.threading;

import org.drugis.common.threading.Task;
import org.drugis.common.threading.ThreadHandler;
import org.drugis.common.threading.activity.ActivityModel;
import org.drugis.common.threading.activity.ActivityTask;

public class TaskUtil {
	public static void run(ActivityModel model) throws InterruptedException {
		run(new ActivityTask(model));
	}
	
	public static void run(Task task) throws InterruptedException {
		ThreadHandler th = ThreadHandler.getInstance();
		th.scheduleTask(task);
		waitUntilReady(task);
	}
	
	public static void waitUntilReady(Task task) throws InterruptedException {
		while (!task.isFinished()) {
			Thread.sleep(100);
		}
	}
}
