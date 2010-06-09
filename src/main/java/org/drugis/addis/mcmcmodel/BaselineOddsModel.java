package org.drugis.addis.mcmcmodel;

import gov.lanl.yadas.ArgumentMaker;
import gov.lanl.yadas.BasicMCMCBond;
import gov.lanl.yadas.Binomial;
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

import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.relativeeffect.LogGaussian;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;
import org.drugis.mtc.yadas.DirectParameter;

//FIXME: allow reuse of ProgressObservable from MTC
public class BaselineOddsModel implements MCMCModel {
	private int d_burnInIter = 10000;
	private int d_simulationIter = 20000;
	private int d_reportingInterval = 100;
	private boolean d_isReady = false;
	private final List<RateMeasurement> d_measurements;
	private List<MCMCUpdate> d_updates;
	private DirectParameter d_mu;
	private List<ProgressListener> d_listeners;
	
	public BaselineOddsModel(List<RateMeasurement> measurements) {
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
		notifyEvent(EventType.SIMULATION_FINISHED);
		
		d_isReady  = true;
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
	
	private void buildModel() {
		MCMCParameter lodds = new MCMCParameter(doubleArray(0.0), doubleArray(0.1), null);
		MCMCParameter mu = new MCMCParameter(new double[] {0.0}, new double[] {0.1}, null);
		d_mu = new DirectParameter(mu, 0);
		MCMCParameter sd = new MCMCParameter(new double[] {0.25}, new double[] {0.1}, null);

		// data bond
		new BasicMCMCBond(new MCMCParameter[] {lodds},
				new ArgumentMaker[] {
					new ConstantArgument(rateArray()),
					new ConstantArgument(sampleSizeArray()),  
					new InverseLogitArgumentMaker(0)
				}, new Binomial());
		
		// lodds bond
		new BasicMCMCBond(new MCMCParameter[] {lodds, mu, sd},
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
					new ConstantArgument(2.0) // FIXME
				}, new Uniform());
		
		List<MCMCParameter> parameters = new ArrayList<MCMCParameter>();
		parameters.add(lodds);
		parameters.add(mu);
		parameters.add(sd);
		
		d_updates = new ArrayList<MCMCUpdate>();
		for (MCMCParameter param : parameters) {
			d_updates.add(new UpdateTuner(param, d_burnInIter / 50, 30, 1, Math.exp(-1)));
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
	
	public LogGaussian getResult() {
		return new LogGaussian(d_mu.getMean(), d_mu.getStandardDeviation());
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
	
	private int[] intArray(int val) {
		int[] arr = new int[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = val;
		}
		return arr;
	}

	private double[] doubleArray(double val) {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = val;
		}
		return arr;
	}

	private double[] sampleSizeArray() {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = d_measurements.get(i).getSampleSize();
		}
		return arr;
	}

	private double[] rateArray() {
		double[] arr = new double[d_measurements.size()];
		for (int i = 0; i < arr.length; ++i) {
			arr[i] = d_measurements.get(i).getRate();
		}
		return arr;
	}
}
