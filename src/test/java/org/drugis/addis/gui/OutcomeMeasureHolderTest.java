package org.drugis.addis.gui;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.value.ValueModel;


public class OutcomeMeasureHolderTest {

	@Test
	public void testPropertyOutcomeMeasure() {
		Endpoint e = new Endpoint("Endpoint", Type.RATE);
		OutcomeMeasureHolder omh = new OutcomeMeasureHolder();
		omh.setOutcomeMeasure(e);
		PresentationModel<OutcomeMeasureHolder> pm = new PresentationModel<OutcomeMeasureHolder>(omh);
		JUnitUtil.testSetter( pm.getModel(OutcomeMeasureHolder.PROPERTY_OUTCOME_MEASURE), e ,  new Endpoint("test",Type.RATE));
	}
	
}
