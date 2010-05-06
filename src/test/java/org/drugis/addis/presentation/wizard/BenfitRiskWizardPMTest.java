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
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
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
		d_pm.getOutcomeSelectedModel(om).setValue(true);
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
	
	@Test
	public void testGetAlternativesListModel() {
		List<Drug> expected = new ArrayList<Drug>();
		for (MetaAnalysis ma : d_domain.getMetaAnalyses()) {
			if (ma.getIndication().equals(d_indication))
				expected.addAll(ma.getIncludedDrugs());
		}
		
		assertAllAndOnly(expected, d_pm.getAlternativesListModel().getValue());
	}
	
	@Test
	public void testGetAlternativeEnabledModel() {
		
		for (Drug d : d_pm.getAlternativesListModel().getValue()) {
			assertEquals(false, d_pm.getAlternativeEnabledModel(d).getValue());
		}
		
		d_pm.getIndicationModel().setValue(d_indication);
		Endpoint outcomeM = ExampleData.buildEndpointHamd();
		d_pm.getOutcomeSelectedModel(outcomeM).setValue(true);
		d_pm.getMetaAnalysesSelectedModel(outcomeM).setValue(ExampleData.buildNetworkMetaAnalysis());

		assertTrue(d_pm.getAlternativesListModel().getValue().size() > 0);
		
		for (Drug d : d_pm.getAlternativesListModel().getValue()) {
			boolean expected = true;
			for (ValueHolder<MetaAnalysis> mah : d_pm.getSelectedMetaAnalysisHolders())
				if (!mah.getValue().getIncludedDrugs().contains(d))
					expected = false;
			
			assertEquals(expected, d_pm.getAlternativeEnabledModel(d).getValue());
		}
	}
	
	@Test
	public void testGetAlternativeSelectedModel() {
		Drug d = ExampleData.buildDrugParoxetine();
		ValueHolder<Boolean> actual = d_pm.getAlternativeSelectedModel(d);
		assertEquals(false,actual.getValue());
		actual.setValue(true);
		assertEquals(true,actual.getValue());
	}
	
}
