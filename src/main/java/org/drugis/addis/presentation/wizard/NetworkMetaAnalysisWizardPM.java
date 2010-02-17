package org.drugis.addis.presentation.wizard;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyGraphModel;

public class NetworkMetaAnalysisWizardPM extends AbstractMetaAnalysisWizardPM<SelectableStudyGraphModel>{

	public NetworkMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
	}

	@Override
	protected SelectableStudyGraphModel buildStudyGraphPresentation() {
		return new SelectableStudyGraphModel(d_indicationHolder, d_endpointHolder, d_drugListHolder, d_domain);
	}

}
