package org.drugis.addis.lyndobrien;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.lyndobrien.BenefitRiskDistribution.Sample;
import org.drugis.common.threading.AbstractSuspendable;
import org.drugis.common.threading.TerminatedException;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;

public class LyndOBrienModelImpl extends AbstractSuspendable implements LyndOBrienModel {

	private BenefitRiskDistribution d_brd;
	private List<ProgressListener> d_listeners;
	private int d_simulationIter = 3000;
	private int d_reportingInterval = 100;
	private boolean d_isReady = false;
	private List<Sample> d_data;

	public LyndOBrienModelImpl(BenefitRiskDistribution brd){
		d_brd = brd;
		d_listeners = new ArrayList<ProgressListener>();
		d_data = new ArrayList<Sample>();
	}
	
	public void addProgressListener(ProgressListener l) {
		d_listeners.add(l);
	}

	public int getBurnInIterations() {
		return 0;
	}

	public int getSimulationIterations() {
		return d_simulationIter;
	}

	public boolean isReady() {
		return d_isReady ;

	}

	public void setBurnInIterations(int it) {}

	public void setSimulationIterations(int it) {
		d_simulationIter = it;
	}

	public void run() {
	
		try {
			notifyEvent(EventType.SIMULATION_STARTED);
			simulate();
			d_isReady = true;
			notifyEvent(EventType.SIMULATION_FINISHED);	
		} catch (TerminatedException e) {
			
		}
	}

	private void simulate() throws TerminatedException {
		for (int iter = 0; iter < d_simulationIter ; ++iter) {
			if (iter > 0 && iter % d_reportingInterval == 0) {
				notifySimulationProgress(iter);
				waitIfSuspended();
			}
			waitIfSuspended();
			d_data.add(iter, d_brd.nextSample());
		}
	}

	private void notifyEvent(EventType type) {
		synchronized(d_listeners)  {
			for (ProgressListener l : d_listeners) {
				l.update(this, new ProgressEvent(type));
			}
		}
	}
	
	private void notifySimulationProgress(int iter) {
		synchronized(d_listeners)  {
			for (ProgressListener l : d_listeners) {
				l.update(this, new ProgressEvent(EventType.SIMULATION_PROGRESS, iter, d_simulationIter));
			}
		}
	}

	public Sample getData(int arg0) {
		return d_data.get(arg0);
	}
	
	public String getXAxisName() {
		return d_brd.getBenefitAxisName();
	}
	
	public String getYAxisName() {
		return d_brd.getRiskAxisName();
	}

	public AxisType getBenefitAxisType() {
		return d_brd.getBenefitAxisType();
	}

	public AxisType getRiskAxisType() {
		return d_brd.getRiskAxisType();
	}

	public double getPValue(double mu) {
		double belowMu = 0;
		for(Sample s: d_data) {
			if((s.risk / s.benefit) < mu) {
				++belowMu;
			}
		}
		return belowMu / d_data.size();
	}
}
