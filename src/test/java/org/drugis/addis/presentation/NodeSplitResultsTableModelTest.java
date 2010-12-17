package org.drugis.addis.presentation;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristics;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class NodeSplitResultsTableModelTest {

	private Domain d_domain;
	private NodeSplitResultsTableModel d_model;
	private Indication d_ind;
	private NetworkMetaAnalysisPresentation d_pm;
	private PresentationModelFactory d_pmf;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		d_ind = d_domain.getIndications().first();
		d_pmf = new PresentationModelFactory(d_domain);
		d_pm = new NetworkMetaAnalysisPresentation(ExampleData.buildNetworkMetaAnalysisHamD(), d_pmf);
		d_model = new NodeSplitResultsTableModel(d_pm);
		for (Characteristic c : StudyCharacteristics.values()) {
			d_pm.getCharacteristicVisibleModel(c).setValue(true);
		}
	}
	
	@Test @Ignore
	public void testGetColumnName() {
		fail();
	}
}
