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

package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Entity;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;

public class EntityTableModelTest {
	Domain d_domain;
	EntityTableModel d_tableModel;
	List<String> d_properties;
	private PresentationModelFactory d_pmf;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_properties = new ArrayList<String>();
		d_properties.add("name");
		d_properties.add("atcCode");
		d_pmf = new PresentationModelFactory(d_domain);
		d_tableModel = new EntityTableModel(Drug.class, 
				d_domain.getCategoryContents(d_domain.getCategory(Drug.class)),
				d_properties, d_pmf);
	}
	
	@Test
	public void testEmptyEntityList() {
		new EntityTableModel(Drug.class, new ArrayListModel<Drug>(), d_properties, d_pmf);
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
		assertEquals(d_domain.getDrugs().get(0), d_tableModel.getValueAt(0, 0));
		assertEquals(d_domain.getDrugs().get(0).getAtcCode(), d_tableModel.getValueAt(0, 1));
	}
	
	@Test
	public void testGetColumnNames() {
		assertEquals("Name", d_tableModel.getColumnName(0));
		assertEquals("Atc Code", d_tableModel.getColumnName(1));
	}

	@Test
	public void testGetColumnClass() {
		assertEquals(Drug.class, d_tableModel.getColumnClass(0));
		EntityTableModel differentTableModel = new EntityTableModel(Study.class, d_domain.getStudies(), 
				Arrays.asList(Study.PROPERTY_ARMS, Study.PROPERTY_INDICATION), d_pmf);
		assertEquals(Indication.class, differentTableModel.getColumnClass(1));
		assertEquals(Object.class, differentTableModel.getColumnClass(0));
		EntityTableModel interfaceTableModel = new EntityTableModel(Endpoint.class, d_domain.getStudies(), 
				Arrays.asList(Endpoint.PROPERTY_VARIABLE_TYPE), d_pmf);
		assertEquals(Entity.class, interfaceTableModel.getColumnClass(0));	
	}
	
	@Test
	public void testDrugAddedUpdatesTable() {
		int prevSize = d_tableModel.getRowCount();
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(d_tableModel));
		d_tableModel.addTableModelListener(mock);
		d_domain.getDrugs().add(ExampleData.buildDrugViagra());
		verify(mock);
		assertEquals(prevSize + 1, d_tableModel.getRowCount());
	}
}
