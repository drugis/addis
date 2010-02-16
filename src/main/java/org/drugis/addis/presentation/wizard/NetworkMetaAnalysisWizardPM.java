package org.drugis.addis.presentation.wizard;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyGraphPresentation;

public class NetworkMetaAnalysisWizardPM extends AbstractMetaAnalysisWizardPM<SelectableStudyGraphPresentation>{

	public NetworkMetaAnalysisWizardPM(Domain d, PresentationModelFactory pmm) {
		super(d, pmm);
	}

	@Override
	protected SelectableStudyGraphPresentation buildStudyGraphPresentation() {
		return new SelectableStudyGraphPresentation(d_indicationHolder, d_endpointHolder, d_drugListHolder, d_domain);
	}

}
