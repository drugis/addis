/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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
import java.util.Date;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Characteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristics;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.value.ValueModel;

public class StudyCharTableModelTest {
	private Domain d_domain;
	private StudyCharTableModel d_model;
	private Indication d_ind;
	private IndicationPresentation d_pm;
	private PresentationModelFactory d_pmf;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		d_ind = d_domain.getIndications().get(0);
		d_pm = new IndicationPresentation(d_ind, d_domain.getStudies());
		d_pmf = new PresentationModelFactory(d_domain);
		d_model = new StudyCharTableModel(d_pm, d_pmf);
		
		for (Characteristic c : StudyCharacteristics.values()) {
			d_pm.getCharacteristicVisibleModel(c).setValue(true);
		}
	}	
	
	@Test
	public void testGetColumnCount() {
		assertEquals(StudyCharacteristics.values().size() + 1, d_model.getColumnCount());
		d_pm.getCharacteristicVisibleModel(StudyCharacteristics.values().get(0)).setValue(Boolean.FALSE);
		assertEquals(StudyCharacteristics.values().size(), d_model.getColumnCount());
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(d_pm.getIncludedStudies().size(), d_model.getRowCount());
	}
		
	@Test
	public void testGetValueAt() {
		int row = 0;
		for (Study s : d_pm.getIncludedStudies()) {
			assertEquals(s, d_model.getValueAt(row, 0));
			int column = 1;
			for (Characteristic c : StudyCharacteristics.values()) {
				StudyPresentation model = (StudyPresentation) d_pmf.getModel(s);
				assertEquals(model.getCharacteristicModel(c).getValue(), d_model.getValueAt(row, column));
				++column;
			}
			++row;
		}
	}
	
	@Test
	public void testGetValueAtColumnRemoved() {
		getFirstCharValueModel().setValue(false);
		int row = 0;
		for (Study s : d_pm.getIncludedStudies()) {
			assertEquals(s, d_model.getValueAt(row, 0));
			int column = 0;
			for (Characteristic c : StudyCharacteristics.values()) {
				if (column > 0) {
					StudyPresentation model = (StudyPresentation) d_pmf.getModel(s);
					assertEquals(model.getCharacteristicModel(c).getValue(), d_model.getValueAt(row, column));
				}
				++column;
			}
			++row;
		}
	}
	
	@Test
	public void testHideColumnFires() {
		ValueModel firstCharVisible = getFirstCharValueModel();
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(d_model));
		
		d_model.addTableModelListener(mock);
		firstCharVisible.setValue(Boolean.FALSE);
		verify(mock);
	}
	
	private ValueModel getFirstCharValueModel() {
		ValueModel firstCharVisible = d_pm.getCharacteristicVisibleModel(StudyCharacteristics.values().get(0));
		return firstCharVisible;
	}
	
	@Test
	public void testCorrectColumnsAreShownAfterConstructor() {
		getFirstCharValueModel().setValue(false);
		d_model = new StudyCharTableModel(d_pm, new PresentationModelFactory(d_domain));
		testGetColumnNameFirstMissingHelper();
	}
	
	@Test
	public void testGetColumnName() {
		assertEquals("Study ID", d_model.getColumnName(0));
		int column = 1;
		for (Characteristic c : StudyCharacteristics.values()) {
			assertEquals(c.getDescription(), d_model.getColumnName(column));
			++column;
		}
	}
	
	@Test
	public void testGetColumnNameRemoved() {
		getFirstCharValueModel().setValue(false);
		testGetColumnNameFirstMissingHelper();
	}

	@Test
	public void testChangeContentsFiresTableChanged() {
		ArrayListModel<Study> list = new ArrayListModel<Study>();
		DefaultStudyListPresentation model = new DefaultStudyListPresentation(list);
		TableModel tableModel = new StudyCharTableModel(model, new PresentationModelFactory(d_domain));
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(tableModel ));
		tableModel.addTableModelListener(mock);
		list.add(new Study());
		verify(mock);
	}

	private void testGetColumnNameFirstMissingHelper() {
		assertEquals("Study ID", d_model.getColumnName(0));
		int column = 0;
		for (Characteristic c : BasicStudyCharacteristic.values()) {
			if (column > 0) {
				assertEquals(c.getDescription(), d_model.getColumnName(column));
			}
			++column;
		}
	}
	
	@Test
	public void testGetColumnClass() {
		assertEquals(Study.class, d_model.getColumnClass(0));
		assertEquals(Integer.class, d_model.getColumnClass(StudyCharacteristics.values().indexOf(BasicStudyCharacteristic.CENTERS) + 1));
		assertEquals(Date.class, d_model.getColumnClass(StudyCharacteristics.values().indexOf(BasicStudyCharacteristic.STUDY_START) + 1));	}
}


