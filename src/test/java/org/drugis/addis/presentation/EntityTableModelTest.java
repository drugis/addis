/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class EntityTableModelTest {
	Domain d_domain;
	EntityTableModel d_tableModel;
	List<String> d_properties;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_properties = new ArrayList<String>();
		d_properties.add("name");
		d_properties.add("atcCode");
//		List<PresentationModel<? extends Entity>> pm = new ArrayList<PresentationModel<? extends Entity>>();
//		PresentationModelFactory pmf = new PresentationModelFactory(d_domain);
//		for (Drug d : d_domain.getDrugs())
//			pm.add(pmf.getModel(d));
		d_tableModel = new EntityTableModel(
				d_domain.getCategoryContentsModel(d_domain.getCategory(Drug.class)),
				d_properties, new PresentationModelFactory(d_domain), "TestTitle");
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_properties.size(), d_tableModel.getColumnCount());
	}
	
	@Test 
	public void testGetRowCount() {
		assertEquals(d_domain.getDrugs().size(), d_tableModel.getRowCount());
	}
	
	@Test
	public void testGetValueAt() {
		assertEquals(d_domain.getDrugs().first(), d_tableModel.getValueAt(0, 0));
		assertEquals(d_domain.getDrugs().first().getAtcCode(), d_tableModel.getValueAt(0, 1));
	}
	
	@Test
	public void testGetColumnNames() {
		assertEquals("Name", d_tableModel.getColumnName(0));
		assertEquals("Atc Code", d_tableModel.getColumnName(1));
	}

	@Test
	public void testDrugAddedUpdatesTable() {
		int prevSize = d_tableModel.getRowCount();
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(d_tableModel));
		d_tableModel.addTableModelListener(mock);
		d_domain.addDrug(ExampleData.buildDrugViagra());
		verify(mock);
		assertEquals(prevSize + 1, d_tableModel.getRowCount());
	}
}
