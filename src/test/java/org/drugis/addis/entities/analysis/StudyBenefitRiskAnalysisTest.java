package org.drugis.addis.entities.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Test;

public class StudyBenefitRiskAnalysisTest {
	private static final String NAME = "Je Moeder";
	private StudyBenefitRiskAnalysis d_analysis;

	@Before
	public void setUp() {
		Indication indication = ExampleData.buildIndicationDepression();
		Study study = ExampleData.buildStudyChouinard();
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointHamd());
		criteria.add(ExampleData.buildAdverseEventConvulsion());
		List<Arm> alternatives = study.getArms();
		d_analysis = new StudyBenefitRiskAnalysis(NAME, indication, study, criteria, alternatives);
	}
	
	@Test
	public void testInitialization() {
		assertEquals(NAME, d_analysis.getName());
		assertEquals(ExampleData.buildStudyChouinard(), d_analysis.getStudy());
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointHamd());
		criteria.add(ExampleData.buildAdverseEventConvulsion());
		assertEquals(criteria, d_analysis.getOutcomeMeasures());
		assertEquals(ExampleData.buildStudyChouinard().getArms(), d_analysis.getAlternatives());
	}
	
	@Test
	public void testDependencies() {
		Set<Entity> expected = new HashSet<Entity>();
		expected.add(d_analysis.getStudy());
		expected.addAll(d_analysis.getOutcomeMeasures());
		expected.add(d_analysis.getIndication());
		expected.addAll(d_analysis.getStudy().getDependencies());
		assertEquals(expected , d_analysis.getDependencies());
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(d_analysis.compareTo(null) > 0);
		assertEquals(0, d_analysis.compareTo(d_analysis));
		MetaBenefitRiskAnalysis otherBRAnalysis = ExampleData.buildMockBenefitRiskAnalysis();
		assertTrue(d_analysis.compareTo(otherBRAnalysis) < 0);
		otherBRAnalysis.setName("Je Loeder");
		assertTrue(d_analysis.compareTo(otherBRAnalysis) > 0);
	}
	
	@Test
	public void testMeasurements() {
		fail();
	}

}
