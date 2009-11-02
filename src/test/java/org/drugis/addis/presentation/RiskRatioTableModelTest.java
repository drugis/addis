package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.RiskRatio;
import org.junit.Before;
import org.junit.Test;

public class RiskRatioTableModelTest extends RatioTableModelBase {
	@Before
	public void setUp() {
		baseSetUp();	
		d_stdModel = new RiskRatioTableModel(d_standardStudy, d_endpoint, d_pmf);
		d_threeArmModel = new RiskRatioTableModel(d_threeArmStudy, d_endpoint, d_pmf);
		d_ratioClass = RiskRatio.class;
	}

	@Test
	public void testGetTitle() {
		String title = "Risk-Ratio Table";
		assertEquals(title, d_threeArmModel.getTitle());
		
	}
}
