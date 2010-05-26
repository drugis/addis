package org.drugis.addis.presentation;

import static org.drugis.common.JUnitUtil.assertAllAndOnly;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class BenefitRiskPMTest {

	private PresentationModelFactory d_pmf;
	private BenefitRiskPM d_pm;

	@Before
	public void setUp() {
		d_pmf = new PresentationModelFactory(new DomainImpl());
		BenefitRiskAnalysis analysis = ExampleData.buildBenefitRiskAnalysis();
		d_pm = new BenefitRiskPM(analysis, d_pmf);
	}
	
	@Test
	public void testGetAnalysesPMList() {
		List<PresentationModel<MetaAnalysis>> expected = new ArrayList<PresentationModel<MetaAnalysis>>();
		for (MetaAnalysis ma : d_pm.getBean().getMetaAnalyses())
			expected.add(d_pmf.getModel(ma));
		assertAllAndOnly(expected, d_pm.getAnalysesPMList());
	}

}
