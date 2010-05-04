package org.drugis.addis.presentation.wizard;

import static org.drugis.common.JUnitUtil.assertAllAndOnly;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.addis.presentation.ValueHolder;
import org.junit.Before;
import org.junit.Test;

public class BenfitRiskWizardPMTest {

	private DomainImpl d_domain;
	private BenefitRiskWizardPM d_pm;
	private Indication d_indication;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pm = new BenefitRiskWizardPM(d_domain); 
		d_indication = ExampleData.buildIndicationDepression();
		
		try {
			d_domain.addMetaAnalysis(ExampleData.buildNetworkMetaAnalysis());
			d_domain.addMetaAnalysis(ExampleData.buildNetworkMetaAnalysisAlternative());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
	}
	
	@Test
	public void testGetOutcomesListModel() {
		for (Indication indication : d_domain.getIndications()) {
			TreeSet<OutcomeMeasure> expected = new TreeSet<OutcomeMeasure>();
			for (Study s : d_domain.getStudies(indication).getValue()) 
				expected.addAll(s.getOutcomeMeasures());
					
			d_pm.getIndicationModel().setValue(indication);
			assertAllAndOnly(expected,d_pm.getOutcomesListModel().getValue());
		}
	}
	
	@Test
	public void testGetMetaAnalyses() {
		d_pm.getIndicationModel().setValue(d_indication);
		for (OutcomeMeasure om : d_pm.getOutcomesListModel().getValue()) {
			List<MetaAnalysis> analyses = new ArrayList<MetaAnalysis>();
			for (MetaAnalysis analysis : d_domain.getMetaAnalyses()) {
				if (om.equals(analysis.getOutcomeMeasure()))
					analyses.add(analysis);
			}
			assertAllAndOnly(analyses, d_pm.getMetaAnalyses(om));
		}
	}
	
	@Test
	public void testGetOutcomeSelectedModel() {
		OutcomeMeasure om = ExampleData.buildEndpointHamd();
		ValueHolder<Boolean> origModel = d_pm.getOutcomeSelectedModel(om);
		assertFalse(origModel.getValue());
		d_pm.getOutcomeSelectedModel(om).setValue(new Boolean(true));
		assertTrue(origModel.getValue());
	}
	
	@Test
	public void testGetMetaAnalysesSelectedModel() {
		ValueHolder<MetaAnalysis> metaAnal1 = d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd());
		assertNull(metaAnal1.getValue());
		
		metaAnal1.setValue(ExampleData.buildNetworkMetaAnalysis());
		assertNotNull(metaAnal1.getValue());
		
		ValueHolder<MetaAnalysis> metaAnal2 = d_pm.getMetaAnalysesSelectedModel(ExampleData.buildEndpointHamd());
		assertEquals(metaAnal1.getValue(), metaAnal2.getValue());
	}
}
