package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.metaanalysis.MetaAnalysis;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class BenefitRiskAnalysisTest {
	private BenefitRiskAnalysis d_BRAnalysis;

	@Before
	public void setup(){
		d_BRAnalysis = ExampleData.buildBenefitRiskAnalysis();
	}
	
	@Test
	public void testGetSetName() {
		JUnitUtil.testSetter(d_BRAnalysis, BenefitRiskAnalysis.PROPERTY_NAME, 
				"testBenefitRiskAnalysis", "some new name");
	}
	@Test
	public void testGetSetIndication() {
		JUnitUtil.testSetter(d_BRAnalysis, BenefitRiskAnalysis.PROPERTY_INDICATION, 
				ExampleData.buildIndicationDepression(), ExampleData.buildIndicationChronicHeartFailure());
	}
	@Test
	public void testGetSetOutcomeMeasures() {
		ArrayList<OutcomeMeasure> newList = new ArrayList<OutcomeMeasure>();
		newList.add(ExampleData.buildEndpointCVdeath());
		newList.add(ExampleData.buildAdverseEventConvulsion());
		JUnitUtil.testSetter(d_BRAnalysis, BenefitRiskAnalysis.PROPERTY_OUTCOMEMEASURES, 
				d_BRAnalysis.getOutcomeMeasures(), newList);
	}
	@Test
	public void testGetSetDrugs() {
		ArrayList<Drug> newList = new ArrayList<Drug>();
		newList.add(ExampleData.buildDrugViagra());
		newList.add(ExampleData.buildDrugCandesartan());
		JUnitUtil.testSetter(d_BRAnalysis, BenefitRiskAnalysis.PROPERTY_DRUGS, 
				d_BRAnalysis.getDrugs(), newList);
	}
	@Test
	public void testGetSetBaseLine() {
		JUnitUtil.testSetter(d_BRAnalysis, BenefitRiskAnalysis.PROPERTY_BASELINE, 
				d_BRAnalysis.getBaseline(), ExampleData.buildDrugViagra());
	}
	@Test
	public void testGetSetMetaAnalyses() {
		ArrayList<MetaAnalysis> newList = new ArrayList<MetaAnalysis>();
		newList.add(ExampleData.buildNetworkMetaAnalysis());
		newList.add(ExampleData.buildNetworkMetaAnalysisAlternative());
		JUnitUtil.testSetter(d_BRAnalysis, BenefitRiskAnalysis.PROPERTY_METAANALYSES, 
				d_BRAnalysis.getMetaAnalyses(), newList);
	}
	
	@Test
	public void testEquals(){
		assertFalse(d_BRAnalysis.equals("nope, no meta Analysis"));
		BenefitRiskAnalysis otherBRAnalysis = ExampleData.buildBenefitRiskAnalysis();
		assertTrue(d_BRAnalysis.equals(otherBRAnalysis));
		otherBRAnalysis.setName("some new name");
		assertFalse(d_BRAnalysis.equals(otherBRAnalysis));
		assertTrue(d_BRAnalysis.equals(d_BRAnalysis));
	}
	
	@Test 
	public void testCompareTo(){
		assertEquals(1, d_BRAnalysis.compareTo(null));
		assertEquals(0, d_BRAnalysis.compareTo(d_BRAnalysis));
		BenefitRiskAnalysis otherBRAnalysis = ExampleData.buildBenefitRiskAnalysis();
		assertEquals(0, d_BRAnalysis.compareTo(otherBRAnalysis));
		otherBRAnalysis.setName("some new name");
		assertNotSame(0, d_BRAnalysis.compareTo(otherBRAnalysis));
	}
	
	@Test
	public void testToString() {
		assertEquals(d_BRAnalysis.getName(), d_BRAnalysis.toString());
	}
	
	@Test
	public void testGetRelativeEffect() {
		RelativeEffect<? extends Measurement> actual = d_BRAnalysis.getRelativeEffect(
				d_BRAnalysis.getDrugs().get(0), d_BRAnalysis.getOutcomeMeasures().get(0));
		RelativeEffect<? extends Measurement> expected = ExampleData.buildMetaAnalysisHamd().getRelativeEffect(
				ExampleData.buildDrugParoxetine(), ExampleData.buildDrugFluoxetine(), OddsRatio.class);
		assertEquals(expected.getRelativeEffect(), actual.getRelativeEffect());
		assertEquals(expected.getError(), actual.getError());
		assertEquals(expected.getConfidenceInterval(), actual.getConfidenceInterval());
	}
}
