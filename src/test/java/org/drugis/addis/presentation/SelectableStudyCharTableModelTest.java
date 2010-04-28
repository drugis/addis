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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristics;
import org.junit.Before;
import org.junit.Test;

public class SelectableStudyCharTableModelTest {

	private Domain d_domain;
	private StudyCharTableModel d_model;
	private Indication d_ind;
	private SelectableStudyListPresentationModel d_pm;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		d_ind = d_domain.getIndications().first();
		d_pm = new DefaultSelectableStudyListPresentationModel(d_domain.getStudies(d_ind));
		d_model = new SelectableStudyCharTableModel(d_pm, new PresentationModelFactory(d_domain));
		for (Characteristic c : StudyCharacteristics.values()) {
			d_pm.getCharacteristicVisibleModel(c).setValue(true);
		}
	}	
	
	@Test
	public void testGetColumnCount() {
		assertEquals(StudyCharacteristics.values().size() + 2, d_model.getColumnCount());
	}
	
	@Test
	public void testGetValueAt() {
		int row = 0;
		for (Study s : d_pm.getIncludedStudies().getValue()) {
			assertTrue((Boolean)d_model.getValueAt(row, 0));			
			assertEquals(s, d_model.getValueAt(row, 1));
			int column = 2;
			for (Characteristic c : BasicStudyCharacteristic.values()) {
				assertEquals(s.getCharacteristic(c), d_model.getValueAt(row, column));
				++column;
			}
			++row;
		}
	}
		
	@Test
	public void testGetColumnName() {
		assertEquals("Study ID", d_model.getColumnName(1));
		assertEquals("", d_model.getColumnName(0));
		int column = 2;
		for (Characteristic c : BasicStudyCharacteristic.values()) {
			assertEquals(c.getDescription(), d_model.getColumnName(column));
			++column;
		}
	}

	@Test
	public void testIsCellEditable() {
		assertTrue(d_model.isCellEditable(0, 0));
		assertFalse(d_model.isCellEditable(0, 1));
		assertFalse(d_model.isCellEditable(1, 1));
	}
	
	@Test
	public void testSetValue() {
		assertTrue((Boolean)d_model.getValueAt(0, 0));
		d_model.setValueAt(false, 0, 0);
		assertFalse((Boolean) d_model.getValueAt(0, 0));
	}
}
