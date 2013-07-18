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
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristics;
import org.drugis.common.beans.FilteredObservableList;
import org.junit.Before;
import org.junit.Test;

public class SelectableStudyCharTableModelTest {

	private Domain d_domain;
	private SelectableStudyCharTableModel d_model;
	private Indication d_ind;
	private StudyListPresentation d_listPresentation;
	private FilteredObservableList<Study> d_studies;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_ind = ExampleData.buildIndicationDepression();
		d_studies = new FilteredObservableList<Study>(d_domain.getStudies(), new DomainImpl.IndicationFilter(d_ind));
		d_listPresentation = new StudyListPresentation(d_studies);
		d_model = new SelectableStudyCharTableModel(d_listPresentation, new PresentationModelFactory(d_domain));
		for (Characteristic c : StudyCharacteristics.values()) {
			d_listPresentation.getCharacteristicVisibleModel(c).setValue(true);
		}
	}
	
	
	@Test
	public void testGetSelectedBooleanModel() {
		assertTrue(d_model.getSelectedStudyBooleanModel(d_studies.get(0)).getValue());
		assertTrue(d_model.getSelectedStudyBooleanModel(d_studies.get(1)).getValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUnknownStudy() {
		d_model.getSelectedStudyBooleanModel(new Study("xxxx", d_ind));
	}
	
	@Test
	public void testGetSelectedModelOnChange() {
		d_model.getSelectedStudyBooleanModel(d_studies.get(0)).setValue(false);
		
		Study newStudy = new Study("new study", d_ind);
		d_studies.add(newStudy);
		
		assertFalse(d_model.getSelectedStudyBooleanModel(d_studies.get(0)).getValue());
		assertTrue(d_model.getSelectedStudyBooleanModel(d_studies.get(1)).getValue());
		assertTrue(d_model.getSelectedStudyBooleanModel(newStudy).getValue());
	}
	
	@Test
	public void testGetSelectedStudiesModel() {
		assertEquals(d_studies, d_model.getSelectedStudiesModel());
		for (int i = 0; i < d_studies.size(); ++i) {
			if (i != 1) {
				d_model.getSelectedStudyBooleanModel(d_studies.get(i)).setValue(false);
			}
		}
		assertEquals(Collections.singletonList(d_studies.get(1)), d_model.getSelectedStudiesModel());	
	}

	
	@Test
	public void testGetColumnCount() {
		assertEquals(StudyCharacteristics.values().size() + 2, d_model.getColumnCount());
	}
	
	@Test
	public void testGetValueAt() {
		int row = 0;
		for (Study s : d_model.getAvailableStudies()) {
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
