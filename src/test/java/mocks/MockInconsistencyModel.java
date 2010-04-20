package mocks;


import java.util.ArrayList;
import java.util.List;

import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyModel;
import org.drugis.mtc.InconsistencyParameter;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.Treatment;



public class MockInconsistencyModel implements InconsistencyModel {

	boolean d_ready = false;
	
	public class MockEstimate implements Estimate {
		public double getStandardDeviation() {
			return 0.33333;
		}
		public double getMean() {
			return 1.0;
		}
	}
	
	public Estimate getInconsistency(InconsistencyParameter param) {
		return new MockEstimate();
	}

	@SuppressWarnings("unchecked")
	public List<InconsistencyParameter> getInconsistencyFactors() {

		List<Treatment> cycle = new ArrayList<Treatment>();
		cycle.add(new Treatment("Fluoxetine"));
		cycle.add(new Treatment("Sertraline"));
		cycle.add(new Treatment("Paroxetine"));
		cycle.add(new Treatment("Fluoxetine"));

		scala.collection.jcl.BufferWrapper<Treatment> wrapper =
			(scala.collection.jcl.BufferWrapper<Treatment>)
			scala.collection.jcl.Conversions$.MODULE$.convertList(cycle);
		scala.List<Treatment> scalaCycle = scala.List$.MODULE$.fromIterator(wrapper.elements());

		List<InconsistencyParameter> inFac = new ArrayList<InconsistencyParameter>();
		inFac.add(new InconsistencyParameter(scalaCycle));

		return inFac;
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
	
}