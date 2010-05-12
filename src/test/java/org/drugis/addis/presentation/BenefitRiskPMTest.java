package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JProgressBar;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BenefitRiskAnalysis;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.junit.*;
import static org.drugis.common.JUnitUtil.assertAllAndOnly;

import com.jgoodies.binding.PresentationModel;

import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.simulator.SMAA2Results;

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
	public void testGetSmaaModelResults() {
		// TODO: the measurements are not tested yet.
		
		/* Added twice to test the correct caching of Criteria and Alternatives.*/
		SMAA2Results actual = d_pm.getSmaaModelResults(new JProgressBar());
		actual = d_pm.getSmaaModelResults(new JProgressBar());
		
		List<Alternative> actualAlternatives = actual.getAlternatives();
		List<? extends Criterion> actualCriteria = actual.getCriteria();
		
		/* Check whether all and only the drugs in the model are present in the SMAA results.*/
		List<Drug> expectedDrugList = new ArrayList<Drug>(d_pm.getBean().getDrugs());
		expectedDrugList.add(d_pm.getBean().getBaseline());
		assertEquals(expectedDrugList.size(), actualAlternatives.size());
		for (Alternative a : actualAlternatives)
			assertTrue(expectedDrugList.contains(new Drug(a.getName(),"000")));
		
		/* Check whether all and only the OutcomeMeasures in the model are present in the SMAA results.*/
		assertEquals(d_pm.getBean().getOutcomeMeasures().size(),actualCriteria.size());
		for (OutcomeMeasure om : d_pm.getBean().getOutcomeMeasures()) {
			boolean isFound = false;
			for (Criterion c : actualCriteria) {
				if (c.getName().equals(om.getName()))
					isFound = true;
			}
			assertTrue(isFound);
		}
	}
	
	public void testGetAnalysesPMList() {
		List<PresentationModel<MetaAnalysis>> expected = new ArrayList<PresentationModel<MetaAnalysis>>();
		for (MetaAnalysis ma : d_pm.getBean().getMetaAnalyses())
			expected.add(d_pmf.getModel(ma));
		assertAllAndOnly(expected, d_pm.getAnalysesPMList());
	}
	
	

}
