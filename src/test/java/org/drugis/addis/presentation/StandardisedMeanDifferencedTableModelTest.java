package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.StandardisedMeanDifference;
import org.junit.Before;
import org.junit.Test;

public class StandardisedMeanDifferencedTableModelTest extends RatioTableModelBase {
	@Before
	public void setUp() {
		baseSetUpContinuous();	
		d_stdModel = new StandardisedMeanDifferenceTableModel(d_standardStudy, d_endpoint, d_pmf);
		d_threeArmModel = new StandardisedMeanDifferenceTableModel(d_threeArmStudy, d_endpoint, d_pmf);
		d_relativeEffectClass = StandardisedMeanDifference.class;
	}

	@Test
	public void testGetTitle() {
		String title = "Standardised Mean Difference Table";
		assertEquals(title, d_threeArmModel.getTitle());
		
	}
}
