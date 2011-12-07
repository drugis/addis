package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drugis.addis.presentation.ValueHolder;
import org.drugis.common.JUnitUtil;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class OptionsEnabledModelTest {

	private SelectableOptionsModel<String> d_select;
	private OptionsEnabledModel<String> d_enabled;

	@Before
	public void setUp() {
		d_select = new SelectableOptionsModel<String>();
		d_select.addOptions(Arrays.asList("pick1", "pick2", "icepick"), false);
		d_enabled = new OptionsEnabledModel<String>(d_select, false) {
			public boolean optionShouldBeEnabled(String option) {
				return option.equals("pick2");
			}
		};
	}
	
	@Test
	public void testInit() {
		assertFalse(d_enabled.getEnabledModel("pick1").getValue());
		assertTrue(d_enabled.getEnabledModel("pick2").getValue());
		assertFalse(d_enabled.getEnabledModel("icepick").getValue());
		assertNull(d_enabled.getEnabledModel("nonsense"));
	}
	
	@Test
	public void testSync() {
		d_select.clear();
		assertNull(d_enabled.getEnabledModel("pick1"));
		d_select.addOptions(Arrays.asList("nick", "jay-Z", "boyz2men"), false);
		assertNotNull(d_enabled.getEnabledModel("nick"));
		assertNotNull(d_enabled.getEnabledModel("jay-Z"));
		assertNotNull(d_enabled.getEnabledModel("boyz2men"));
	}
	
	@Test
	public void testUpdateOnSelection() {
		d_enabled = new OptionsEnabledModel<String>(d_select, true) {
			public boolean optionShouldBeEnabled(String option) {
				return d_select.getSelectedOptions().size() < 2 || d_select.getSelectedModel(option).getValue();
			}
		};
		ValueHolder<Boolean> pick1Enabled = d_enabled.getEnabledModel("pick1");
		ValueHolder<Boolean> pick2Enabled = d_enabled.getEnabledModel("pick2");
		
		PropertyChangeListener listener1 = EasyMock.createStrictMock(PropertyChangeListener.class);
		PropertyChangeListener listener2 = EasyMock.createStrictMock(PropertyChangeListener.class);

		listener2.propertyChange(JUnitUtil.eqPropertyChangeEvent(new PropertyChangeEvent(pick2Enabled, "value", true, false)));
		EasyMock.replay(listener1, listener2);
		
		pick1Enabled.addValueChangeListener(listener1);
		pick2Enabled.addValueChangeListener(listener2);
		
		d_select.getSelectedModel("pick1").setValue(true);
		d_select.getSelectedModel("icepick").setValue(true);
		assertTrue(d_enabled.getEnabledModel("pick1").getValue());
		assertFalse(d_enabled.getEnabledModel("pick2").getValue());
		assertTrue(d_enabled.getEnabledModel("icepick").getValue());
		assertNull(d_enabled.getEnabledModel("nonsense"));
		EasyMock.verify(listener1, listener2);
	}
		
	@Test
	public void testDeselectOnDisable() {
		final List<String> allowed = new ArrayList<String>(Arrays.asList("pick1", "icepick"));
		d_enabled = new OptionsEnabledModel<String>(d_select, true) {
			public boolean optionShouldBeEnabled(String option) {
				return allowed.contains(option);
			}
		};
		d_select.getSelectedModel("pick1").setValue(true);
		d_select.getSelectedModel("icepick").setValue(true);
		allowed.remove("icepick");
		d_enabled.update();
		assertFalse(d_select.getSelectedModel("icepick").getValue());
	}
}
