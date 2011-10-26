package org.drugis.addis.presentation;

import static org.drugis.addis.presentation.BRATTableModel.COLUMN_BASELINE;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_BR;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_CRITERIA;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_DIFFERENCE;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_OUTCOME_TYPE;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_SUBJECT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.DrugSet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.VariableType;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.drugis.addis.entities.relativeeffect.BasicOddsRatio;
import org.drugis.addis.entities.relativeeffect.BasicStandardisedMeanDifference;
import org.drugis.addis.entities.relativeeffect.Distribution;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;

public class BRATTableModelTest {

	private BRATTableModel<DrugSet, BenefitRiskAnalysis<DrugSet>> d_btmMockMeta;
	private BRATTableModel<Arm, BenefitRiskAnalysis<Arm>> d_btmMockStudy;
	private BRATTableModel<Arm, BenefitRiskAnalysis<Arm>> d_btmStudy;
	private StudyBenefitRiskAnalysis d_sba;
	private Arm d_baseline;
	private Arm d_subject;
	private MetaBenefitRiskAnalysis d_mba;

	@Before
	public void setUp() {
		d_mba = ExampleData.buildMetaBenefitRiskAnalysis();
		d_btmMockMeta = new BRATTableModel<DrugSet, BenefitRiskAnalysis<DrugSet>>(d_mba);
		d_btmMockStudy = new BRATTableModel<Arm, BenefitRiskAnalysis<Arm>>(ExampleData.buildStudyBenefitRiskAnalysis());
		List<OutcomeMeasure> criteria = new ArrayList<OutcomeMeasure>();
		criteria.add(ExampleData.buildEndpointHamd());
		criteria.add(ExampleData.buildEndpointCgi());
		criteria.add(ExampleData.buildAdverseEventConvulsion());
		ObservableList<Arm> arms = ExampleData.buildStudyChouinard().getArms();
		d_sba = new StudyBenefitRiskAnalysis("Test SBA", ExampleData.buildIndicationDepression(), 
				ExampleData.buildStudyChouinard(), criteria, arms, AnalysisType.SMAA);
		d_baseline = arms.get(1);
		d_subject = arms.get(0);
		d_btmStudy = new BRATTableModel<Arm, BenefitRiskAnalysis<Arm>>(d_sba, d_baseline, d_subject);
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(2, d_btmMockMeta.getRowCount());
		assertEquals(2, d_btmMockStudy.getRowCount());
		assertEquals(3, d_btmStudy.getRowCount());
	}
	
	@Test
	public void testColumnCount() {
		assertEquals(7, d_btmStudy.getColumnCount());
	}
	
	@Test
	public void testGetBenefitRiskColumn() {
		assertEquals("Benefit", d_btmMockMeta.getValueAt(0, COLUMN_BR));
		assertEquals("Risk", d_btmMockMeta.getValueAt(1, COLUMN_BR));
		assertEquals("Benefit", d_btmStudy.getValueAt(0, COLUMN_BR));
		assertEquals("Benefit", d_btmStudy.getValueAt(1, COLUMN_BR));
		assertEquals("Risk", d_btmStudy.getValueAt(2, COLUMN_BR));
	}
	
	@Test
	public void testGetOutcomeMeasureName() {
		assertSame(ExampleData.buildEndpointHamd(), d_btmMockMeta.getValueAt(0, COLUMN_CRITERIA));
		assertSame(ExampleData.buildAdverseEventConvulsion(), d_btmMockMeta.getValueAt(1, COLUMN_CRITERIA));
		assertSame(ExampleData.buildEndpointCgi(), d_btmStudy.getValueAt(0, COLUMN_CRITERIA));
		assertSame(ExampleData.buildEndpointHamd(), d_btmStudy.getValueAt(1, COLUMN_CRITERIA));
		assertSame(ExampleData.buildAdverseEventConvulsion(), d_btmStudy.getValueAt(2, COLUMN_CRITERIA));
	}
	
	@Test
	public void testColumnType() {
		assertEquals("Continuous", d_btmStudy.getValueAt(0, COLUMN_OUTCOME_TYPE).toString());
		assertEquals("Rate", d_btmStudy.getValueAt(1, COLUMN_OUTCOME_TYPE).toString());
	}
	
	@Test
	public void testColumnNames() {
		assertEquals("", d_btmMockMeta.getColumnName(COLUMN_BR));
		assertEquals("Outcome", d_btmMockMeta.getColumnName(COLUMN_CRITERIA));
		assertEquals("Type", d_btmMockMeta.getColumnName(COLUMN_OUTCOME_TYPE));
		assertEquals("Fluoxetine", d_btmMockMeta.getColumnName(COLUMN_BASELINE));
		assertEquals("Paroxetine", d_btmMockMeta.getColumnName(COLUMN_SUBJECT));
		assertEquals("Fluoxetine 27.5 mg/day", d_btmStudy.getColumnName(COLUMN_BASELINE));
		assertEquals("Paroxetine 25.5 mg/day", d_btmStudy.getColumnName(COLUMN_SUBJECT));
		assertEquals("Difference (95% CI)", d_btmMockMeta.getColumnName(COLUMN_DIFFERENCE));
		assertEquals("", d_btmMockMeta.getColumnName(COLUMN_BR));
	}
	
	@Test
	public void testColumnClasses() {
		assertEquals(String.class, d_btmMockMeta.getColumnClass(COLUMN_BR));
		assertEquals(Variable.class, d_btmMockMeta.getColumnClass(COLUMN_CRITERIA));
		assertEquals(VariableType.class, d_btmMockMeta.getColumnClass(COLUMN_OUTCOME_TYPE));
		assertEquals(Distribution.class, d_btmMockMeta.getColumnClass(COLUMN_BASELINE));
		assertEquals(Distribution.class, d_btmMockMeta.getColumnClass(COLUMN_SUBJECT));
		assertEquals(Distribution.class, d_btmStudy.getColumnClass(COLUMN_BASELINE));
		assertEquals(Distribution.class, d_btmStudy.getColumnClass(COLUMN_SUBJECT));
		assertEquals(Distribution.class, d_btmMockMeta.getColumnClass(COLUMN_DIFFERENCE));
//		assertEquals(Object.class, d_btmMockMeta.getColumnClass(COLUMN_FOREST)); should be a foresty thingy
	}
	
	@Test
	public void testRisks() {
		assertEquals(d_sba.getMeasurement(d_sba.getCriteria().get(0), d_sba.getArms().get(1)), d_btmStudy.getValueAt(0, COLUMN_BASELINE));
		assertEquals(d_sba.getMeasurement(d_sba.getCriteria().get(1), d_sba.getArms().get(1)), d_btmStudy.getValueAt(1, COLUMN_BASELINE));
		assertEquals(d_sba.getMeasurement(d_sba.getCriteria().get(0), d_sba.getArms().get(0)), d_btmStudy.getValueAt(0, COLUMN_SUBJECT));
		assertEquals(d_sba.getMeasurement(d_sba.getCriteria().get(1), d_sba.getArms().get(0)), d_btmStudy.getValueAt(1, COLUMN_SUBJECT));
	}
	
	@Test
	public void testDifferences() {
		BasicOddsRatio ratio = new BasicOddsRatio((RateMeasurement) d_sba.getStudy().getMeasurement(d_sba.getCriteria().get(1), d_baseline), 
				(RateMeasurement) d_sba.getStudy().getMeasurement(d_sba.getCriteria().get(1), d_subject));
		assertEquals(ratio.getDistribution(), d_btmStudy.getValueAt(1, COLUMN_DIFFERENCE));
		
		BasicStandardisedMeanDifference diff = new BasicStandardisedMeanDifference(
				(ContinuousMeasurement) d_sba.getStudy().getMeasurement(d_sba.getCriteria().get(0), d_baseline), 
				(ContinuousMeasurement) d_sba.getStudy().getMeasurement(d_sba.getCriteria().get(0), d_subject));
		assertEquals(diff.getDistribution(), d_btmStudy.getValueAt(0, COLUMN_DIFFERENCE));
	
		assertEquals(d_mba.getRelativeEffectDistribution(d_mba.getCriteria().get(0), d_mba.getAlternatives().get(0), d_mba.getAlternatives().get(1)), d_btmMockMeta.getValueAt(0, COLUMN_DIFFERENCE));
	}
}
