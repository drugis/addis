package org.drugis.addis.util;

import org.drugis.addis.presentation.UnmodifiableHolder;

@SuppressWarnings("serial")
public class RunnableReadyModel extends UnmodifiableHolder<Boolean> implements Runnable {
	private final Runnable d_runnable;
	private Boolean d_ready;

	public RunnableReadyModel(Runnable runnable) {
		super(false);
		d_ready = false;
		d_runnable = runnable;
	}
	
	@Override
	public Boolean getValue() {
		return d_ready;
	}
	
	public void run() {
		d_runnable.run();
		d_ready = true;
		fireValueChange(false, true);
	}
}
