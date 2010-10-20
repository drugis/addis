package org.drugis.addis.lyndobrien;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.relativeeffect.AxisType;
import org.drugis.addis.lyndobrien.BenefitRiskDistribution.Sample;
import org.drugis.common.threading.IterativeComputation;
import org.drugis.common.threading.IterativeTask;
import org.drugis.common.threading.Task;

public class LyndOBrienModelImpl implements LyndOBrienModel, IterativeComputation {

	private BenefitRiskDistribution d_brd;
	private int d_simulationIter = 3000;
	private int d_reportingInterval = 100;
	private List<Sample> d_data;
	private int d_iter = 0;
	private IterativeTask d_task;

	public LyndOBrienModelImpl(BenefitRiskDistribution brd){
		d_brd = brd;
		d_data = new ArrayList<Sample>();
		d_task = new IterativeTask(this);
		d_task.setReportingInterval(d_reportingInterval);
	}

	public void setSimulationIterations(int it) {
		d_simulationIter = it;
	}
	
	public int getSimulationIterations() {
		return d_simulationIter;
	}

	public boolean isReady() {
		return d_task.isFinished();

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

	public void initialize() {}
	public void finish() {}

	public int getIteration() {
		return d_iter;
	}

	public int getTotalIterations() {
		return d_simulationIter;
	}

	public void step() {
		d_data.add(d_iter, d_brd.nextSample());
		++d_iter;
	}

	public Task getTask() {
		return d_task;
	}
}
