package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.MeanDifference;
import org.junit.Before;
import org.junit.Test;

public class MeanDifferenceTableModelTest extends RelativeEffectTableModelBaseTest {
	@Before
	public void setUp() {
		baseSetUpContinuous();	
		d_stdModel = new MeanDifferenceTableModel(d_standardStudy, d_endpoint, d_pmf);
		d_threeArmModel = new MeanDifferenceTableModel(d_threeArmStudy, d_endpoint, d_pmf);
		d_relativeEffectClass = MeanDifference.class;
	}

	@Test
	public void testGetTitle() {
		String title = "Mean-Difference Table";
		assertEquals(title, d_threeArmModel.getTitle());
		
	}
}
