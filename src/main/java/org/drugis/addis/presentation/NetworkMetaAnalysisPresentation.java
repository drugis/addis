package org.drugis.addis.presentation;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.MixedTreatmentComparison;
import org.drugis.mtc.NetworkBuilder;
import org.drugis.mtc.ProgressEvent;
import org.drugis.mtc.ProgressListener;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.ProgressEvent.EventType;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisPresentation extends AbstractMetaAnalysisPresentation<NetworkMetaAnalysis> {

	private DefaultCategoryDataset d_dataset;

	public NetworkMetaAnalysisPresentation(NetworkMetaAnalysis bean, PresentationModelFactory mgr) {
		super(bean, mgr);
	}

	public StudyGraphModel getStudyGraphModel() {
		return new StudyGraphModel(new DefaultListHolder<Study>(getBean().getIncludedStudies()),
				new DefaultListHolder<Drug>(getBean().getIncludedDrugs()));
	}

	public CategoryDataset getRankProbabilityDataset() {

		d_dataset = new DefaultCategoryDataset();
		ConsistencyModel consistencyModel = getBean().getConsistencyModel();

		if (!consistencyModel.isReady()) {
			consistencyModel.addProgressListener(new ProgressListener() {
				public void update(MixedTreatmentComparison mtc, ProgressEvent event) {
					if (event.getType() == EventType.SIMULATION_FINISHED)
						fillDataSet();
				}
			});
		}
		else
			fillDataSet();

		return d_dataset;
	}

	private void fillDataSet() {
		NetworkBuilder<? extends org.drugis.mtc.Measurement> builder = getBean().getBuilder();
		ConsistencyModel consistencyModel = getBean().getConsistencyModel();
		for (Drug d : getBean().getIncludedDrugs()) {
			for (int rank = 1; rank <= getBean().getIncludedDrugs().size(); ++rank) {	
				Treatment treatment = builder.getTreatment(d.toString());
				double rankProb = consistencyModel.rankProbability(treatment, rank);
				d_dataset.addValue((Number) rankProb, d, rank);
			}	
		}
	}
}
