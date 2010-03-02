package org.drugis.addis.entities.metaanalysis;

import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;

public class NetworkMetaAnalysis extends AbstractMetaAnalysis implements MetaAnalysis {
	private static final long serialVersionUID = -1646175155970420625L;

	public NetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<? extends Study> studies, List<Drug> drugs,
			Map<Study, Map<Drug, Arm>> armMap) throws IllegalArgumentException {
		super(name, indication, om, studies, drugs, armMap);
	}

	public String getType() {
		return "Markov Chain Monte Carlo Network Meta-Analysis";
	}
}
