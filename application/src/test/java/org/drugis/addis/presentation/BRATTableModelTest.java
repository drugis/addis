package org.drugis.addis.presentation;

import static org.drugis.addis.entities.AssertEntityEquals.assertEntityEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.junit.Before;
import org.junit.Test;

public class BRATTableModelTest {

	private BRATTableModel<DrugSet, BenefitRiskAnalysis<DrugSet>> d_btmMockMeta;
	private BRATTableModel<Arm, BenefitRiskAnalysis<Arm>> d_btmMockStudy;
	private BRATTableModel<Arm, BenefitRiskAnalysis<Arm>> d_btmStudy;
	private StudyBenefitRiskAnalysis d_sba;

	@Before
	public void setUp() {
		d_btmMockMeta = new BRATTableModel<DrugSet, BenefitRiskAnalysis<DrugSet>>(ExampleData.buildMetaBenefitRiskAnalysis());
		d_btmMockStudy = new BRATTableModel<Arm, BenefitRiskAnalysis<Arm>>(ExampleData.buildStudyBenefitRiskAnalysis());
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointHamd());
		criteria.add(ExampleData.buildAdverseEventConvulsion());
		criteria.add(ExampleData.buildAdverseEventSexualDysfunction());
		d_sba = new StudyBenefitRiskAnalysis("Test SBA", ExampleData.buildIndicationDepression(), 
				ExampleData.buildStudyFava2002(), criteria, ExampleData.buildStudyFava2002().getArms(), AnalysisType.SMAA);
		d_btmStudy = new BRATTableModel<Arm, BenefitRiskAnalysis<Arm>>(d_sba);
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(2, d_btmMockMeta.getRowCount());
		assertEquals(2, d_btmMockStudy.getRowCount());
	}
	
	@Test
	public void testGetBenefitRiskColumn() {
		assertEquals("Benefit", d_btmMockMeta.getValueAt(0, 0));
		assertEquals("Risk", d_btmMockMeta.getValueAt(1, 0));
	}
	
	@Test
	public void testGetOutcomeMeasureName() {
		assertEntityEquals(ExampleData.buildEndpointHamd(), (Endpoint) d_btmMockMeta.getValueAt(0, 1));
		assertEntityEquals(ExampleData.buildAdverseEventConvulsion(), (AdverseEvent) d_btmMockMeta.getValueAt(1, 1));
	}
	
	@Test
	public void testColumnNames() {
		assertEquals("Fluoxetine", d_btmMockMeta.getColumnName(2));
		assertEquals("Paroxetine", d_btmMockMeta.getColumnName(3));
	}
	
	@Test
	public void testRisks() {
		assertEquals(ExampleData.buildStudyFava2002().getMeasurement(d_sba.getCriteria().get(0), d_sba.getArms().get(0)), d_btmStudy.getValueAt(0, 2));
		assertEquals(ExampleData.buildStudyFava2002().getMeasurement(d_sba.getCriteria().get(1), d_sba.getArms().get(1)), d_btmStudy.getValueAt(1, 2));
	}
}
