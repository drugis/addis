package org.drugis.addis.presentation;

import static org.drugis.addis.presentation.BRATTableModel.COLUMN_BASELINE;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_BR;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_CRITERIA;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_DIFFERENCE;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_FOREST;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_OUTCOME_TYPE;
import static org.drugis.addis.presentation.BRATTableModel.COLUMN_SUBJECT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.forestplot.LinearScale;
import org.drugis.addis.forestplot.LogScale;
import org.drugis.addis.presentation.BRATTableModel.BRATDifference;
import org.drugis.addis.presentation.BRATTableModel.BRATForest;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;

public class BRATTableModelTest {

	private BRATTableModel<DrugSet, BenefitRiskAnalysis<DrugSet>> d_btmMeta;
	private BRATTableModel<Arm, BenefitRiskAnalysis<Arm>> d_btmMockStudy;
	private BRATTableModel<Arm, BenefitRiskAnalysis<Arm>> d_btmStudy;
	private StudyBenefitRiskAnalysis d_sba;
	private Arm d_baseline;
	private Arm d_subject;
	private MetaBenefitRiskAnalysis d_mba;

	@Before
	public void setUp() {
		d_mba = ExampleData.buildMetaBenefitRiskAnalysis();
		d_btmMeta = new BRATTableModel<DrugSet, BenefitRiskAnalysis<DrugSet>>(d_mba);
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
		assertEquals(4, d_btmMeta.getRowCount());
		assertEquals(4, d_btmMockStudy.getRowCount());
		assertEquals(5, d_btmStudy.getRowCount());
	}
	
	@Test
	public void testColumnCount() {
		assertEquals(7, d_btmStudy.getColumnCount());
	}
	
	@Test
	public void testGetBenefitRiskColumn() {
		assertEquals("Benefit", d_btmMeta.getValueAt(0, COLUMN_BR));
		assertEquals("Risk", d_btmMeta.getValueAt(1, COLUMN_BR));
		assertEquals("Benefit", d_btmStudy.getValueAt(0, COLUMN_BR));
		assertEquals("Benefit", d_btmStudy.getValueAt(1, COLUMN_BR));
		assertEquals("Risk", d_btmStudy.getValueAt(2, COLUMN_BR));
	}
	
	@Test
	public void testGetOutcomeMeasureName() {
		assertSame(ExampleData.buildEndpointHamd(), d_btmMeta.getValueAt(0, COLUMN_CRITERIA));
		assertSame(ExampleData.buildAdverseEventConvulsion(), d_btmMeta.getValueAt(1, COLUMN_CRITERIA));
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
		assertEquals("", d_btmMeta.getColumnName(COLUMN_BR));
		assertEquals("Outcome", d_btmMeta.getColumnName(COLUMN_CRITERIA));
		assertEquals("Type", d_btmMeta.getColumnName(COLUMN_OUTCOME_TYPE));
		assertEquals("Fluoxetine", d_btmMeta.getColumnName(COLUMN_BASELINE));
		assertEquals("Paroxetine", d_btmMeta.getColumnName(COLUMN_SUBJECT));
		assertEquals("Fluoxetine 27.5 mg/day", d_btmStudy.getColumnName(COLUMN_BASELINE));
		assertEquals("Paroxetine 25.5 mg/day", d_btmStudy.getColumnName(COLUMN_SUBJECT));
		assertEquals("Difference (95% CI)", d_btmMeta.getColumnName(COLUMN_DIFFERENCE));
		assertEquals("", d_btmMeta.getColumnName(COLUMN_BR));
	}
	
	@Test
	public void testColumnClasses() {
		assertEquals(String.class, d_btmMeta.getColumnClass(COLUMN_BR));
		assertEquals(Variable.class, d_btmMeta.getColumnClass(COLUMN_CRITERIA));
		assertEquals(VariableType.class, d_btmMeta.getColumnClass(COLUMN_OUTCOME_TYPE));
		assertEquals(Distribution.class, d_btmMeta.getColumnClass(COLUMN_BASELINE));
		assertEquals(Distribution.class, d_btmMeta.getColumnClass(COLUMN_SUBJECT));
		assertEquals(Distribution.class, d_btmStudy.getColumnClass(COLUMN_BASELINE));
		assertEquals(Distribution.class, d_btmStudy.getColumnClass(COLUMN_SUBJECT));
		assertEquals(BRATDifference.class, d_btmMeta.getColumnClass(COLUMN_DIFFERENCE));
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
		assertEquals(ratio.getDistribution(), ((BRATDifference)d_btmStudy.getValueAt(1, COLUMN_DIFFERENCE)).getDifference());
		
		BasicStandardisedMeanDifference diff = new BasicStandardisedMeanDifference(
				(ContinuousMeasurement) d_sba.getStudy().getMeasurement(d_sba.getCriteria().get(0), d_baseline), 
				(ContinuousMeasurement) d_sba.getStudy().getMeasurement(d_sba.getCriteria().get(0), d_subject));
		assertEquals(diff.getDistribution(), ((BRATDifference)d_btmStudy.getValueAt(0, COLUMN_DIFFERENCE)).getDifference());
	
		assertEquals(d_mba.getRelativeEffectDistribution(d_mba.getCriteria().get(0), d_mba.getAlternatives().get(0), d_mba.getAlternatives().get(1)),
				((BRATDifference)d_btmMeta.getValueAt(0, COLUMN_DIFFERENCE)).getDifference());
	}

	@Test
	public void testForestConfidenceIntervals() {
		GaussianBase relEff = d_mba.getRelativeEffectDistribution(d_mba.getCriteria().get(0), d_mba.getAlternatives().get(0), d_mba.getAlternatives().get(1));
		assertEquals((Double)relEff.getQuantile(0.025), ((BRATForest)d_btmMeta.getValueAt(0, COLUMN_FOREST)).ci.getLowerBound());
		assertEquals((Double)relEff.getQuantile(0.5), ((BRATForest)d_btmMeta.getValueAt(0, COLUMN_FOREST)).ci.getPointEstimate());
		assertEquals((Double)relEff.getQuantile(0.975), ((BRATForest)d_btmMeta.getValueAt(0, COLUMN_FOREST)).ci.getUpperBound());
		
		BasicStandardisedMeanDifference diff = new BasicStandardisedMeanDifference(
				(ContinuousMeasurement) d_sba.getStudy().getMeasurement(d_sba.getCriteria().get(0), d_baseline), 
				(ContinuousMeasurement) d_sba.getStudy().getMeasurement(d_sba.getCriteria().get(0), d_subject));
		assertEquals(diff.getConfidenceInterval(), ((BRATForest)d_btmStudy.getValueAt(0, COLUMN_FOREST)).ci);
	}
	
	
	@Test
	public void testForestScales() {
		// linear scale for study BR
		Distribution linVal = ((BRATDifference)d_btmStudy.getValueAt(0, COLUMN_DIFFERENCE)).getDifference();

		double linMin = linVal.getQuantile(0.025);
		double linMax = linVal.getQuantile(0.975);
		Interval<Double> linScale = ForestPlotPresentation.niceIntervalLinear(linMin, linMax);
		assertEquals(new LinearScale(linScale), d_btmStudy.getNiceLinearScale());
		
		Distribution logVal1 = ((BRATDifference)d_btmStudy.getValueAt(1, COLUMN_DIFFERENCE)).getDifference();
		Distribution logVal2 = ((BRATDifference)d_btmStudy.getValueAt(2, COLUMN_DIFFERENCE)).getDifference();
		double logMin = Math.min(logVal1.getQuantile(0.025), logVal2.getQuantile(0.025));
		double logMax = Math.max(logVal1.getQuantile(0.975), logVal2.getQuantile(0.975));
		Interval<Double> logScale = ForestPlotPresentation.niceIntervalLog(logMin, logMax);
		assertEquals(new LogScale(logScale), d_btmStudy.getNiceLogScale());
		
		double min = Math.min(linScale.getLowerBound(), Math.log(logScale.getLowerBound()));
		double max = Math.max(linScale.getUpperBound(), Math.log(logScale.getUpperBound()));		
		
		// log scale for study BR
		assertEquals(new LinearScale(new Interval<Double>(min, max)), ((BRATForest)d_btmStudy.getValueAt(0, COLUMN_FOREST)).scale.getScale());
		assertEquals(new LogScale(new Interval<Double>(Math.exp(min), Math.exp(max))), ((BRATForest)d_btmStudy.getValueAt(2, COLUMN_FOREST)).scale.getScale());
		assertEquals(new LogScale(new Interval<Double>(Math.exp(min), Math.exp(max))), ((BRATForest)d_btmStudy.getValueAt(1, COLUMN_FOREST)).scale.getScale());
		
		assertNull(d_btmMeta.getNiceLinearScale());
		assertEquals(d_btmMeta.getNiceLogScale(), d_btmMeta.getFullLogScale());
	}
	
	@Test
	public void testAxes() {
		assertEquals(d_btmStudy.getFullLogScale(), ((BRATForest)d_btmStudy.getValueAt(3, COLUMN_FOREST)).scale.getScale());
		assertEquals(d_btmStudy.getNiceLogScale(), ((BRATForest)d_btmStudy.getValueAt(3, COLUMN_FOREST)).axis);
		assertNull(((BRATForest)d_btmStudy.getValueAt(3, COLUMN_FOREST)).ci);
		assertEquals(d_btmStudy.getFullLinearScale(), ((BRATForest)d_btmStudy.getValueAt(4, COLUMN_FOREST)).scale.getScale());
		assertEquals(d_btmStudy.getNiceLinearScale(), ((BRATForest)d_btmStudy.getValueAt(4, COLUMN_FOREST)).axis);
		assertNull(((BRATForest)d_btmStudy.getValueAt(4, COLUMN_FOREST)).ci);
	}
}
