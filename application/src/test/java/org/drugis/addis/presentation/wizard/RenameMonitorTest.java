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

import org.drugis.addis.entities.Arm;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class RenameMonitorTest {

	static class RenameMonitorImpl extends RenameMonitor<Arm> {
		public int d_cnt = 0;

		public RenameMonitorImpl(AddListItemsPresentation<Arm> listPresentation) {
			super(listPresentation);
		}

		@Override
		protected void renameDetected() {
			++d_cnt;
		}
	}

	private Arm d_a;
	private ObservableList<Arm> d_l;
	private AddArmsPresentation d_listPresentation;

	@Before
	public void setUp() {
		d_a = new Arm("test", 0);
		d_l = new ArrayListModel<Arm>();
//		d_listPresentation = new AddArmsPresentation(null, d_l, null, 0);
	}
	
	@Test
	public void testFireChangeOnRename() {
		d_l.add(d_a);
		RenameMonitorImpl monitor = new RenameMonitorImpl(d_listPresentation); 
		d_a.setName("omg");
		assertEquals(1, monitor.d_cnt);
	}
	
	@Test
	public void testAddingWorks() {
		RenameMonitorImpl monitor = new RenameMonitorImpl(d_listPresentation);
		d_a.setName("Yo momma");
		assertEquals(0, monitor.d_cnt);
		d_l.add(d_a);
		assertEquals(0, monitor.d_cnt);
		d_a.setName("omg");
		assertEquals(1, monitor.d_cnt);
	}
	
	@Test
	public void testRemovingWorks() {
		d_l.add(d_a);
		RenameMonitorImpl monitor = new RenameMonitorImpl(d_listPresentation);

		d_a.setName("omg");
		assertEquals(1, monitor.d_cnt);
		d_l.remove(d_a);
		d_a.setName("back to the future");
		assertEquals(1, monitor.d_cnt);
	}
	
	@Test
	public void testReplacingListWorks() {
		d_l.add(d_a);
		RenameMonitorImpl monitor = new RenameMonitorImpl(d_listPresentation);
		Arm a2 = new Arm("Foo", 1);
		ArrayListModel<Arm> l2 = new ArrayListModel<Arm>();
		l2.add(a2);
		d_listPresentation.setList(l2);
		assertEquals(0, monitor.d_cnt);
		d_a.setName("Bar");
		assertEquals(0, monitor.d_cnt);
		a2.setName("Quz");
		assertEquals(1, monitor.d_cnt);
		l2.add(d_a);
		assertEquals(1, monitor.d_cnt);
		d_a.setName("Fqip-");
		assertEquals(2, monitor.d_cnt);
	}
	
}
