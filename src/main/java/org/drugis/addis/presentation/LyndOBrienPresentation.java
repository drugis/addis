package org.drugis.addis.presentation;

import javax.swing.JProgressBar;

import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.lyndobrien.BenefitRiskDistributionImpl;
import org.drugis.addis.lyndobrien.LyndOBrienModel;
import org.drugis.addis.lyndobrien.LyndOBrienModelImpl;
import org.drugis.addis.util.threading.ThreadHandler;
import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;

public class LyndOBrienPresentation<Alternative extends Entity, AnalysisType extends BenefitRiskAnalysis<Alternative>> {

	private class AnalysisProgressListener implements ProgressListener {
		JProgressBar d_progBar;
		private MCMCModel d_networkModel;

		public AnalysisProgressListener(MCMCModel networkModel) {
			networkModel.addProgressListener(this);
			d_networkModel = networkModel;
		}
		
		public void attachBar(JProgressBar bar) {
			d_progBar = bar;
			bar.setVisible(!d_networkModel.isReady());
		}

		public void update(MCMCModel mtc, ProgressEvent event) {
			if(event.getType() == EventType.SIMULATION_PROGRESS && d_progBar != null){
				d_progBar.setString("Simulating: " + event.getIteration()/(event.getTotalIterations()/100) + "%");
				d_progBar.setValue(event.getIteration()/(event.getTotalIterations()/100));
			} else if(event.getType() == EventType.SIMULATION_FINISHED && d_progBar != null) {
				d_progBar.setString("Done!");
				d_progBar.setValue(100);
			}
		}
	}
	
	AnalysisType d_a;
	StudyBenefitRiskAnalysis sbr;
	private LyndOBrienModelImpl d_model;
	private ValueHolder<Boolean> d_initializedModel = new ModifiableHolder<Boolean>(false);
	private AnalysisProgressListener d_progListener;
	
	public LyndOBrienPresentation(AnalysisType at) {
		d_a = at;
	}
	
	public LyndOBrienModel getModel() {
		return d_model;
	}
	
	public ValueHolder<Boolean> getInitializedModel() {
		return d_initializedModel;
	}

	public void startLyndOBrien() {
		d_model = new LyndOBrienModelImpl(new BenefitRiskDistributionImpl<Alternative>(d_a));
		d_initializedModel.setValue(true);
		d_progListener = new AnalysisProgressListener(d_model);
		ThreadHandler.getInstance().scheduleTask(d_model);
	}

	public void attachProgBar(JProgressBar bar) {
		d_progListener.attachBar(bar);
	}
}
