package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.OddsRatio;
import org.junit.Before;
import org.junit.Test;


public class OddsRatioTableModelTest extends RatioTableModelBase {
	@Before
	public void setUp() {
		baseSetUp();	
		d_stdModel = new OddsRatioTableModel(d_standardStudy, d_endpoint, d_pmf);
		d_threeArmModel = new OddsRatioTableModel(d_threeArmStudy, d_endpoint, d_pmf);
		d_ratioClass = OddsRatio.class;
	}

	@Test
	public void testGetTitle() {
		String title = "Odds-Ratio Table";
		assertEquals(title, d_threeArmModel.getTitle());
		
	}
}
