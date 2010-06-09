package org.drugis.addis.mcmcmodel;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.drugis.addis.entities.RateMeasurement;
import org.junit.Test;

public class BaselineOddsModelTest {
	@Test
	public void testBurnInIterations() {
		BaselineOddsModel model = new BaselineOddsModel(new ArrayList<RateMeasurement>());
		assertTrue(model.getBurnInIterations() > 1000);
		int newIter = model.getBurnInIterations() * 2;
		model.setBurnInIterations(newIter);
		assertEquals(newIter, model.getBurnInIterations());
	}
	
	@Test
	public void testSimulationIterations() {
		BaselineOddsModel model = new BaselineOddsModel(new ArrayList<RateMeasurement>());
		assertTrue(model.getSimulationIterations() > 1000);
		int newIter = model.getSimulationIterations() * 2;
		model.setSimulationIterations(newIter);
		assertEquals(newIter, model.getSimulationIterations());
	}
}
