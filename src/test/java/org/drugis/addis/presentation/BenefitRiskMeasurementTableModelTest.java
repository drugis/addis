package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.junit.Before;
import org.junit.Test;

public class BenefitRiskMeasurementTableModelTest {

	private PresentationModelFactory d_pmf;
	private BenefitRiskMeasurementTableModel d_pm;
	private BenefitRiskAnalysis d_brAnalysis;

	@Before
	public void setUp() {
		d_pmf = new PresentationModelFactory(new DomainImpl());
		d_brAnalysis = ExampleData.buildBenefitRiskAnalysis();
		d_pm = new BenefitRiskMeasurementTableModel(d_brAnalysis, d_pmf);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_brAnalysis.getOutcomeMeasures().size() + 1, d_pm.getColumnCount());
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(d_brAnalysis.getDrugs().size(), d_pm.getRowCount());
	}
	
	@Test
	public void testGetDrugNames() {
		for (int i=0; i<d_brAnalysis.getDrugs().size(); ++i)
			assertEquals(d_brAnalysis.getDrugs().get(i).getName(), d_pm.getValueAt(i, 0));
	}
	
	@Test
	public void testGetOutcomeNames() {
		List<OutcomeMeasure> outcomeMeasures = d_brAnalysis.getOutcomeMeasures();
		for (int j=0; j<outcomeMeasures.size(); ++j) {
			assertEquals(outcomeMeasures.get(j).toString(), d_pm.getColumnName(j+1));
		}
	}
	
	@Test
	public void testGetValueAt() {
		for (int i=0; i<d_brAnalysis.getDrugs().size(); ++i)
			for (int j=0; j<d_brAnalysis.getOutcomeMeasures().size(); ++j) {
				Object expected = d_pmf.getLabeledModel(d_brAnalysis.getRelativeEffect(d_brAnalysis.getDrugs().get(i), d_brAnalysis.getOutcomeMeasures().get(j)));
				Object actual = d_pm.getValueAt(i, j+1);
				assertEquals(expected.toString(), actual.toString());
			}
	}
}
