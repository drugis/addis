package org.drugis.addis.presentation.test;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.presentation.IndicationPresentation;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

import static org.easymock.EasyMock.*;

public class IndicationPresentationTest {
	@Test
	public void testCodeFireLabelChanged() {
		Indication i = new Indication(5L, "Some indication");
		IndicationPresentation p = new IndicationPresentation(i);
		AbstractValueModel model = p.getLabelModel();
		PropertyChangeListener x = JUnitUtil.mockListener(model, "value", "5 Some indication", "6 Some indication");
		model.addPropertyChangeListener(x);
		i.setCode(6L);
		verify(x);
	}
	
	@Test
	public void testNameFireLabelChanged() {
		Indication i = new Indication(5L, "Some indication");
		IndicationPresentation p = new IndicationPresentation(i);
		AbstractValueModel model = p.getLabelModel();
		PropertyChangeListener x = JUnitUtil.mockListener(model, "value", "5 Some indication", "5 Other indication");
		model.addPropertyChangeListener(x);
		i.setName("Other indication");
		verify(x);
	}

}
