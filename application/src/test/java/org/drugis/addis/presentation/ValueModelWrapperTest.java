package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.easymock.EasyMock.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class ValueModelWrapperTest {

	@Test
	public void testGetAndSetValue() {
		ValueModel nested = new ModifiableHolder<Object>(false);
		ValueModelWrapper<Boolean> wrapper = new ValueModelWrapper<Boolean>(nested);
		assertEquals(false, wrapper.getValue());
		nested.setValue(true);
		assertEquals(true, wrapper.getValue());
		wrapper.setValue(false);
		assertEquals(false, nested.getValue());
	}
	
	@Test
	public void testEventPropagation() {
		ValueModel nested = new ModifiableHolder<Object>(false);
		ValueModelWrapper<Boolean> wrapper = new ValueModelWrapper<Boolean>(nested);

		PropertyChangeListener listener = createStrictMock(PropertyChangeListener.class);
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(new PropertyChangeEvent(wrapper, "value", false, true)));
		listener.propertyChange(JUnitUtil.eqPropertyChangeEvent(new PropertyChangeEvent(wrapper, "value", true, false)));
		replay(listener);
		
		wrapper.addPropertyChangeListener(listener);
		nested.setValue(true);
		wrapper.setValue(false);
		verify(listener);
	}
}
