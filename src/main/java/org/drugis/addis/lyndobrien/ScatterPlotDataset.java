package org.drugis.addis.lyndobrien;

import org.drugis.common.threading.TaskListener;
import org.drugis.common.threading.event.TaskEvent;
import org.drugis.common.threading.event.TaskEvent.EventType;
import org.drugis.common.threading.event.TaskProgressEvent;
import org.jfree.data.xy.AbstractXYDataset;

@SuppressWarnings("serial")
public class ScatterPlotDataset extends AbstractXYDataset implements TaskListener {

	private final LyndOBrienModel d_model;
	private int d_itemCount = 0;
	private int d_seriesCount = 1;

	public ScatterPlotDataset(LyndOBrienModel model){
		d_model = model;
		model.getTask().addTaskListener(this);
		if(model.getTask().isFinished()) {
			d_itemCount = model.getSimulationIterations();
		}
	}
	
	@Override
	public int getSeriesCount() {
		return d_seriesCount ;
	}

	@Override
	public Comparable<?> getSeriesKey(int series) {
		return 0;
	}

	public int getItemCount(int series) {
		return d_itemCount;
	}

	public Number getX(int series, int item) {
		return d_model.getData(item).benefit;
	}

	public Number getY(int series, int item) {
		return d_model.getData(item).risk;
	}

	public void taskEvent(TaskEvent event) {
		if(event.getType() == EventType.TASK_PROGRESS) {
			TaskProgressEvent progress = (TaskProgressEvent)event;
			d_itemCount = progress.getIteration();
			fireDatasetChanged();
		}
	}

}
