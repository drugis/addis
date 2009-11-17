package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.RiskDifference;
import org.junit.Before;
import org.junit.Test;

public class RiskDifferenceTableModelTest extends RelativeEffectTableModelBaseTest {
	@Before
	public void setUp() {
		baseSetUpRate();	
		d_stdModel = new RiskDifferenceTableModel(d_standardStudy, d_endpoint, d_pmf);
		d_threeArmModel = new RiskDifferenceTableModel(d_threeArmStudy, d_endpoint, d_pmf);
		d_relativeEffectClass = RiskDifference.class;
	}

	@Test
	public void testGetTitle() {
		String title = "Risk-Difference Table";
		assertEquals(title, d_threeArmModel.getTitle());
		
	}
}
