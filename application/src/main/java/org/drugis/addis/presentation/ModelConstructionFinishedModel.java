package org.drugis.addis.presentation;

import org.drugis.common.threading.Task;
import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.mtc.MixedTreatmentComparison;

public class ModelConstructionFinishedModel extends UnmodifiableHolder<Boolean> implements TaskListener {
	private static final long serialVersionUID = 8539051214083665626L;
	private Task d_task;
	public ModelConstructionFinishedModel(MixedTreatmentComparison model) {
		super(model.getActivityTask().getModel().getStartState().isFinished());
		d_task = model.getActivityTask().getModel().getStartState();
		d_task.addTaskListener(this);
	}

	public void taskEvent(TaskEvent event) {
		if (event.getType().equals(EventType.TASK_FINISHED)) {
			fireValueChange(false, true);
		}
	}

	public Boolean getValue() {
		return d_task.isFinished();
	}
}