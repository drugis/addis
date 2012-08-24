/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
