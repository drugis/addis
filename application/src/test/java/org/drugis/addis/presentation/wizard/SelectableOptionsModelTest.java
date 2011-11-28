/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import junit.framework.Assert;

import org.drugis.addis.presentation.ModifiableHolder;
import org.drugis.common.event.ListDataEventMatcher;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;

public class SelectableOptionsModelTest {
	private SelectableOptionsModel<String> d_model;

	@Before
	public void setUp() {
		d_model = new SelectableOptionsModel<String>();
	}
	
	@Test
	public void testAddOptionDefaultValue() {
		assertEquals(true, d_model.addOption("Geitenkaas", true).getValue());
		assertEquals(false, d_model.addOption("Bladerdeeg", false).getValue());
	}
	
	@Test
	public void testAddOptionsDefaultValue() {
		List<ModifiableHolder<Boolean>> holders = d_model.addOptions(Arrays.asList("Geitenkaas", "Bladerdeeg"), true);
		assertEquals(2, holders.size());
		assertEquals(true, holders.get(0).getValue());
		assertEquals(true, holders.get(1).getValue());
	}
	
	@Test
	public void testSelectedOptions() {
		assertEquals(Collections.emptySet(), new HashSet<String>(d_model.getSelectedOptions()));
		List<String> optionsYes = new ArrayList<String>(Arrays.asList("Geitenkaas", "Bladerdeeg"));
		d_model.addOptions(optionsYes, true);
		Collections.sort(optionsYes);
		assertEquals(optionsYes, d_model.getSelectedOptions());
		
		d_model.addOption("Zongedroogde tomaat", false);
		assertEquals(optionsYes, d_model.getSelectedOptions());
		
		d_model.addOption("Bier", true);
		optionsYes.add(0, "Bier");
		assertEquals(optionsYes, d_model.getSelectedOptions());
	}
	
	
	@Test
	public void testSelectedOptionsEvents() {
		ObservableList<String> list = d_model.getSelectedOptions();
		ListDataListener mock = EasyMock.createStrictMock(ListDataListener.class);
		EasyMock.replay(mock);
		list.addListDataListener(mock);
		ModifiableHolder<Boolean> bladerdeeg = d_model.addOption("Bladerdeeg", false);
		EasyMock.verify(mock);
		list.removeListDataListener(mock);
		
		ListDataListener mock2 = EasyMock.createStrictMock(ListDataListener.class);
		mock2.intervalAdded(ListDataEventMatcher.eqListDataEvent(
				new ListDataEvent(list, ListDataEvent.INTERVAL_ADDED, 0, 0)));
		EasyMock.replay(mock2);
		list.addListDataListener(mock2);
		bladerdeeg.setValue(true);
		EasyMock.verify(mock2);
		list.removeListDataListener(mock2);
		
		ModifiableHolder<Boolean> ei = d_model.addOption("Ei", true);
		ModifiableHolder<Boolean> courgette = d_model.addOption("Courgette", true);
		
		ListDataListener mock3 = EasyMock.createStrictMock(ListDataListener.class);
		mock3.intervalRemoved(ListDataEventMatcher.eqListDataEvent(
				new ListDataEvent(list, ListDataEvent.INTERVAL_REMOVED, 1, 1)));
		mock3.intervalRemoved(ListDataEventMatcher.eqListDataEvent(
				new ListDataEvent(list, ListDataEvent.INTERVAL_REMOVED, 1, 1)));
		EasyMock.replay(mock3);
		list.addListDataListener(mock3);
		courgette.setValue(false);
		ei.setValue(false);
		EasyMock.verify(mock3);
		list.removeListDataListener(mock3);		
	}
	
	@Test
	public void testClear() {
		d_model.addOption("Bladerdeeg", false);
		d_model.addOption("Geitenkaas", true);
		d_model.addOption("Komijn", false);
		d_model.addOption("Makreel", true);
		
		final ModifiableHolder<Integer> count = new ModifiableHolder<Integer>(0);
		
		ObservableList<String> list = d_model.getSelectedOptions();
		ListDataListener counter = new ListDataListener() {
			
			@Override
			public void intervalRemoved(ListDataEvent e) {
				count.setValue(count.getValue() + (e.getIndex1() - e.getIndex0()) + 1);
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
				Assert.fail("Expected INTERVAL_REMOVED, but got " + e);
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
				Assert.fail("Expected INTERVAL_REMOVED, but got " + e);
			}
		};
		list.addListDataListener(counter);
		d_model.clear();
		list.removeListDataListener(counter);
		
		assertEquals(new Integer(2), count.getValue());
		assertEquals(Collections.emptyList(), d_model.getSelectedOptions());
	}
	
	@Test
	public void testGet() {
		ModifiableHolder<Boolean> bladerdeeg = d_model.addOption("Bladerdeeg", false);
		ModifiableHolder<Boolean> geitekaas = d_model.addOption("Geitenkaas", true);
		assertSame(bladerdeeg, d_model.getSelectedModel("Bladerdeeg"));
		assertNull(d_model.getSelectedModel("DITBESTAATTOCHNIET"));
		assertSame(geitekaas, d_model.getSelectedModel("Geitenkaas"));
	}
	
	@Test
	public void testSelectionsChangeList() {
		ModifiableHolder<Boolean> bdeeg = d_model.addOption("Bladerdeeg", false);
		ModifiableHolder<Boolean> gkaas = d_model.addOption("Geitenkaas", false);
		d_model.addOption("Komijn", false);
		d_model.addOption("Makreel", false);
		
		assertEquals(Collections.emptyList(), d_model.getSelectedOptions());
		bdeeg.setValue(true);
		gkaas.setValue(true);
		assertEquals(Arrays.asList("Bladerdeeg", "Geitenkaas"), d_model.getSelectedOptions());
		bdeeg.setValue(false);
		assertEquals(Arrays.asList("Geitenkaas"), d_model.getSelectedOptions());
	}
}
