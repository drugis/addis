package org.drugis.addis.mcmcmodel;

import org.drugis.mtc.ProgressListener;

public interface MCMCModel extends Runnable {
	/**
	* Add a progress listener to this MTC.
	*/
	public void addProgressListener(ProgressListener l);
	/**
	* @return false if it's necessary to run() this model before calling any
	* getters.
	*/
	public boolean isReady();
	/**
	* @return the number of burn-in iterations
	*/
	public int getBurnInIterations();
	/**
	* @param it The number of burn-in iterations, a multiple of 100.
	* @throws IllegalArgumentException if it is not a multiple of 100, or
	* if it <= 0.
	*/
	public void setBurnInIterations(int it);
	/**
	* @return the number of simulation iterations
	*/
	public int getSimulationIterations();
	/**
	* @param it The number of simulation iterations, a multiple of 100.
	* @throws IllegalArgumentException if it is not a multiple of 100, or
	* if it <= 0.
	*/
	public void setSimulationIterations(int it);
}
