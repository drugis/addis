package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.relativeeffect.GaussianBase;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class BenefitRiskMeasurementTableModelTest {

	private PresentationModelFactory d_pmf;
	private BenefitRiskMeasurementTableModel d_pm;
	private BenefitRiskAnalysis d_brAnalysis;

	@Before
	public void setUp() {
		d_pmf = new PresentationModelFactory(new DomainImpl());
		d_brAnalysis = ExampleData.buildMockBenefitRiskAnalysis();
		d_pm = new BenefitRiskMeasurementTableModel(d_brAnalysis, d_pmf, true);
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
				Drug drug = d_brAnalysis.getDrugs().get(i);
				OutcomeMeasure om = d_brAnalysis.getOutcomeMeasures().get(j);
				Object expected = d_pmf.getLabeledModel(d_brAnalysis.getRelativeEffect(drug, om));
				Object actual = d_pm.getValueAt(i, j+1);
				assertEquals(expected.toString(), actual.toString());
			}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtAbsolute() {
		d_pm = new BenefitRiskMeasurementTableModel(d_brAnalysis, d_pmf, false);
		for (int i=0; i < d_brAnalysis.getDrugs().size(); ++i) {
			Drug drug = d_brAnalysis.getDrugs().get(i);
			for (int j=0; j < d_brAnalysis.getOutcomeMeasures().size(); ++j) {
				OutcomeMeasure om = d_brAnalysis.getOutcomeMeasures().get(j);
				GaussianBase expected = (GaussianBase)d_brAnalysis.getAbsoluteEffectDistribution(drug, om);
				GaussianBase actual = (GaussianBase)((RelativeEffect)((PresentationModel)d_pm.getValueAt(i, j+1)).getBean()).getDistribution();
				assertEquals(expected.getMu(), actual.getMu(), 0.000001);
				assertEquals(expected.getSigma(), actual.getSigma(), 0.000001);
			}
		}
	}
}
