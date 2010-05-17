package org.drugis.addis.mocks;

import java.util.List;
import java.util.Map;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.mtc.ConsistencyModel;
import org.drugis.mtc.InconsistencyModel;


public class MockNetworkMetaAnalysis extends NetworkMetaAnalysis {
	
	private InconsistencyModel d_mockInconsistencyModel;
	private ConsistencyModel d_mockConsistencyModel;

	public MockNetworkMetaAnalysis(String name, Indication indication,
			OutcomeMeasure om, List<? extends Study> studies, List<Drug> drugs,
			Map<Study, Map<Drug, Arm>> armMap) throws IllegalArgumentException {
		super(name, indication, om, studies, drugs, armMap);
		d_mockInconsistencyModel = new MockInconsistencyModel();
		d_mockConsistencyModel = new MockConsistencyModel();
	}
	
	public InconsistencyModel getInconsistencyModel() {
		return d_mockInconsistencyModel;
	}
	
	public ConsistencyModel getConsistencyModel() {
		return d_mockConsistencyModel;
	}
}
