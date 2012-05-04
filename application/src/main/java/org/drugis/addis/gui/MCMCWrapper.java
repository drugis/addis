package org.drugis.addis.gui;

import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.gui.task.TaskProgressModel;
import org.drugis.common.threading.activity.ActivityTask;
import org.drugis.mtc.MCMCModel;

public abstract class MCMCWrapper implements Comparable<MCMCWrapper> {
	private final MCMCModel d_model;
	private final String d_name;
	protected final OutcomeMeasure d_om;
	private TaskProgressModel d_taskProgressModel;

	public MCMCWrapper(MCMCModel model, OutcomeMeasure om, String name) { 
		d_model = model;
		d_om = om;
		d_taskProgressModel = new TaskProgressModel(getActivityTask());
		d_name = name;
	}
	
	public ActivityTask getActivityTask() {
		return d_model.getActivityTask();
	}

	public TaskProgressModel getProgressModel() {
		return d_taskProgressModel;
	}
	
	public MCMCModel getModel() {
		return d_model;
	} 

	public abstract ValueHolder<Boolean> isModelConstructed();	

	public OutcomeMeasure getOutcomeMeasure() {
		return d_om;
	}
	
	@Override
	public String toString() { 
		return d_name;
	}
}
