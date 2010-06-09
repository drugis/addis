package org.drugis.addis.mcmcmodel;

import gov.lanl.yadas.ArgumentMaker;
import gov.lanl.yadas.BasicMCMCBond;
import gov.lanl.yadas.ConstantArgument;
import gov.lanl.yadas.Gaussian;
import gov.lanl.yadas.GroupArgument;
import gov.lanl.yadas.IdentityArgument;
import gov.lanl.yadas.MCMCParameter;
import gov.lanl.yadas.MCMCUpdate;
import gov.lanl.yadas.Uniform;
import gov.lanl.yadas.UpdateTuner;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;
import org.drugis.mtc.yadas.DirectParameter;

abstract public class AbstractBaselineModel<T extends Measurement> implements MCMCModel {

	public abstract Distribution getResult();

	private int d_burnInIter = 10000;
	private int d_simulationIter = 20000;
	private int d_reportingInterval = 100;
	private boolean d_isReady = false;
	protected List<T> d_measurements;
	private List<MCMCUpdate> d_updates;
	private DirectParameter d_mu;
	private List<ProgressListener> d_listeners;
	
	public AbstractBaselineModel(List<T> measurements) {
		d_measurements = measurements;
		d_listeners = new ArrayList<ProgressListener>();
	}

	public void run() {
		notifyEvent(EventType.MODEL_CONSTRUCTION_STARTED);
		buildModel();
		notifyEvent(EventType.MODEL_CONSTRUCTION_FINISHED);
		
		notifyEvent(EventType.BURNIN_STARTED);
		burnIn();
		notifyEvent(EventType.BURNIN_FINISHED);
		
		notifyEvent(EventType.SIMULATION_STARTED);
		simulate();
		d_isReady  = true;
		notifyEvent(EventType.SIMULATION_FINISHED);
	}

	private void notifyEvent(EventType type) {
		for (ProgressListener l : d_listeners) {
			l.update(this, new ProgressEvent(type));
		}
	}

	private void notifyBurnInProgress(int iter) {
		for (ProgressListener l : d_listeners) {
			l.update(this, new ProgressEvent(EventType.BURNIN_PROGRESS, iter, d_burnInIter));
		}
	}

	private void notifySimulationProgress(int iter) {
		for (ProgressListener l : d_listeners) {
			l.update(this, new ProgressEvent(EventType.SIMULATION_PROGRESS, iter, d_simulationIter));
		}
	}

	private void burnIn() {
		for (int iter = 0; iter < d_burnInIter; ++iter) {
			if (iter > 0 && iter % d_reportingInterval == 0) notifyBurnInProgress(iter);
			update();
		}
	}

	private void simulate() {
		for (int iter = 0; iter < d_simulationIter; ++iter) {
			if (iter > 0 && iter % d_reportingInterval == 0) notifySimulationProgress(iter);
			update();
			output();
		}
	}

	private void output() {
		d_mu.update();
	}

	private void update() {
		for (MCMCUpdate u : d_updates) {
			u.update();
		}
	}

	public void addProgressListener(ProgressListener l) {
		d_listeners.add(l);		
	}

	public boolean isReady() {
		return d_isReady;
	}

	public int getBurnInIterations() {
		return d_burnInIter;
	}

	public int getSimulationIterations() {
		return d_simulationIter;
	}

	public void setBurnInIterations(int it) {
		d_burnInIter = it;
	}

	public void setSimulationIterations(int it) {
		d_simulationIter = it;
	}

	protected int[] intArray(int val) {
		int[] arr = new int[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = val;
		}
		return arr;
	}

	protected double[] doubleArray(double val) {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = val;
		}
		return arr;
	}

	protected abstract double getStdDevPrior();

	protected abstract void createDataBond(MCMCParameter studyMu);

	protected void buildModel() {
		MCMCParameter studyMu = new MCMCParameter(doubleArray(0.0), doubleArray(0.1), null);
		MCMCParameter mu = new MCMCParameter(new double[] {0.0}, new double[] {0.1}, null);
		d_mu = new DirectParameter(mu, 0);
		MCMCParameter sd = new MCMCParameter(new double[] {0.25}, new double[] {0.1}, null);
	
		// data bond
		createDataBond(studyMu);
		
		// studyMu bond
		new BasicMCMCBond(new MCMCParameter[] {studyMu, mu, sd},
				new ArgumentMaker[] {
					new IdentityArgument(0),
					new GroupArgument(1, intArray(0)),
					new GroupArgument(2, intArray(0))
				}, new Gaussian());
	
		// priors
		new BasicMCMCBond(new MCMCParameter[] {mu},
				new ArgumentMaker[] {
					new IdentityArgument(0),
					new ConstantArgument(0.0),
					new ConstantArgument(Math.sqrt(1000))
				}, new Gaussian());
		new BasicMCMCBond(new MCMCParameter[] {sd},
				new ArgumentMaker[] {
					new IdentityArgument(0),
					new ConstantArgument(0.0),
					new ConstantArgument(getStdDevPrior()) // FIXME
				}, new Uniform());
		
		List<MCMCParameter> parameters = new ArrayList<MCMCParameter>();
		parameters.add(studyMu);
		parameters.add(mu);
		parameters.add(sd);
		
		d_updates = new ArrayList<MCMCUpdate>();
		for (MCMCParameter param : parameters) {
			d_updates.add(new UpdateTuner(param, d_burnInIter / 50, 30, 1, Math.exp(-1)));
		}
	}

	protected double getStdDev() {
		return d_mu.getStandardDeviation();
	}

	protected double getMean() {
		return d_mu.getMean();
	}

}
