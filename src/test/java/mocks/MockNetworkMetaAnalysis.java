package mocks;

import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.mtc.InconsistencyModel;


public class MockNetworkMetaAnalysis extends NetworkMetaAnalysis {
	
	private MockInconsistencyModel d_mockIncModel;

	public MockNetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<? extends Study> studies, List<Drug> drugs,
			Map<Study, Map<Drug, Arm>> armMap) throws IllegalArgumentException {
		super(name, indication, om, studies, drugs, armMap);
		d_mockIncModel = new MockInconsistencyModel();
	}
	
	public InconsistencyModel getInconsistencyModel() {
		return d_mockIncModel;
	}

}
