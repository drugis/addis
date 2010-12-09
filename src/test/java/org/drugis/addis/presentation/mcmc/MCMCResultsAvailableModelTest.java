package org.drugis.addis.presentation.mcmc;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.beans.PropertyChangeListener;

import org.drugis.addis.util.FakeResults;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class MCMCResultsAvailableModelTest {

	@Test
	public void testInitalisation() {
		FakeResults res = new FakeResults(3, 50, 2, false);
		assertEquals(false, new MCMCResultsAvailableModel(res).getValue());
		res.makeResultsAvailable();
		assertEquals(true, new MCMCResultsAvailableModel(res).getValue());
	}
	
	@Test
	public void testDynamicValue() {
		FakeResults res = new FakeResults(3, 50, 2, false);
		MCMCResultsAvailableModel model = new MCMCResultsAvailableModel(res);
		res.makeResultsAvailable();
		assertEquals(true, model.getValue());
		res.clear();
		assertEquals(false, model.getValue());
	}

	@Test
	public void testFireValueChange() {
		FakeResults res = new FakeResults(3, 50, 2, false);
		MCMCResultsAvailableModel model = new MCMCResultsAvailableModel(res);
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", false, true);
		model.addValueChangeListener(mock);
		
		res.makeResultsAvailable();
		verify(mock);
	}
}
