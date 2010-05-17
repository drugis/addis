package org.drugis.addis.mocks;


import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.Treatment;



public class MockConsistencyModel implements ConsistencyModel {

	boolean d_ready = false;
	
	public class MockEstimate implements Estimate {
		public double getStandardDeviation() {
			return 0.33333;
		}
		public double getMean() {
			return 1.0;
		}
	}
	
	public Estimate getConsistency() {
		return new MockEstimate();
	}

	public void addProgressListener(ProgressListener l) {
	}

	public Estimate getRelativeEffect(Treatment base, Treatment subj) {
		return new MockEstimate();
	}

	public boolean isReady() {
		return d_ready;
	}

	public void run() {
		d_ready = true;
	}

	public int getBurnInIterations() {
		return -1;
	}

	public int getSimulationIterations() {
		return -1;
	}

	public void setBurnInIterations(int it) {
	}

	public void setSimulationIterations(int it) {
	}

	public double rankProbability(Treatment t, int r) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}