package org.drugis.addis.lyndobrien;

import org.drugis.mtc.MCMCModel;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.ProgressEvent.EventType;
import org.jfree.data.xy.AbstractXYDataset;

@SuppressWarnings("serial")
public class ScatterPlotDataset extends AbstractXYDataset implements ProgressListener {

	private final LyndOBrienModel d_model;
	private int d_itemCount = 0;
	private int d_seriesCount = 1;

	public ScatterPlotDataset(LyndOBrienModel model){
		d_model = model;
		model.addProgressListener(this);
	}
	
	@Override
	public int getSeriesCount() {
		return d_seriesCount ;
	}

	@Override
	public Comparable<?> getSeriesKey(int series) {
		return 0;
	}

	public int getItemCount(int series) {
		return d_itemCount;
	}

	public Number getX(int series, int item) {
		return d_model.getData(item).benefit;
	}

	public Number getY(int series, int item) {
		return d_model.getData(item).risk;
	}

	public void update(MCMCModel mtc, ProgressEvent event) {
		if(event.getType() == EventType.SIMULATION_PROGRESS) {
			d_itemCount = event.getIteration();
			fireDatasetChanged();
		} else if (event.getType() == EventType.SIMULATION_FINISHED) {
			d_itemCount = d_model.getSimulationIterations();
			fireDatasetChanged();
		}
	}

}
