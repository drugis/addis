package org.drugis.addis.presentation;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;

@SuppressWarnings("serial")
public class NetworkMetaAnalysisPresentation extends AbstractMetaAnalysisPresentation<NetworkMetaAnalysis> {

	public NetworkMetaAnalysisPresentation(NetworkMetaAnalysis bean, PresentationModelFactory mgr) {
		super(bean, mgr);
		// TODO Auto-generated constructor stub
	}

	public StudyGraphModel getStudyGraphModel() {
		return new StudyGraphModel(new DefaultListHolder<Study>(getBean().getIncludedStudies()),
				new DefaultListHolder<Drug>(getBean().getIncludedDrugs()));
	}
}
