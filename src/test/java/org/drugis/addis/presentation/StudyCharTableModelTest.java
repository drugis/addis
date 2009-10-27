/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class StudyCharTableModelTest {
	private Domain d_domain;
	private StudyListPresentationModel d_pm;
	private StudyCharTableModel d_model;
	private MetaStudy d_study;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildDefaultStudy1());
		studies.add(ExampleData.buildDefaultStudy2());
		MetaAnalysis ma = new MetaAnalysis(ExampleData.buildEndpointHamd(), studies);
		d_study = new MetaStudy("Meta", ma);
		d_pm = new MetaStudyPresentationModel(d_study);
		d_model = new StudyCharTableModel(d_pm);
	}	
	
	@Test
	public void testGetColumnCount() {
		assertEquals(StudyCharacteristic.values().length + 1, d_model.getColumnCount());
		d_pm.getCharacteristicVisibleModel(StudyCharacteristic.values()[0]).setValue(Boolean.FALSE);
		assertEquals(StudyCharacteristic.values().length, d_model.getColumnCount());
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(d_pm.getIncludedStudies().getValue().size(), d_model.getRowCount());
	}
	
	@Test
	public void testGetValueAt() {
		int row = 0;
		for (Study s : d_pm.getIncludedStudies().getValue()) {
			assertEquals(s.getId(), d_model.getValueAt(row, 0));
			int column = 1;
			for (StudyCharacteristic c : StudyCharacteristic.values()) {
				assertEquals(s.getCharacteristic(c), d_model.getValueAt(row, column));
				++column;
			}
			++row;
		}
	}
	
	@Test
	public void testGetValueAtColumnRemoved() {
		getFirstCharValueModel().setValue(false);
		int row = 0;
		for (Study s : d_pm.getIncludedStudies().getValue()) {
			assertEquals(s.getId(), d_model.getValueAt(row, 0));
			int column = 0;
			for (StudyCharacteristic c : StudyCharacteristic.values()) {
				if (column > 0) {
					assertEquals(s.getCharacteristic(c), d_model.getValueAt(row, column));
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
		ValueModel firstCharVisible = d_pm.getCharacteristicVisibleModel(StudyCharacteristic.values()[0]);
		return firstCharVisible;
	}
	
	@Test
	public void testCorrectColumnsAreShownAfterConstructor() {
		getFirstCharValueModel().setValue(false);
		d_model = new StudyCharTableModel(d_pm);
		testGetColumnNameFirstMissingHelper();
	}
	
	@Test
	public void testGetColumnName() {
		assertEquals("Study ID", d_model.getColumnName(0));
		int column = 1;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			assertEquals(c.getDescription(), d_model.getColumnName(column));
			++column;
		}
	}
	
	@Test
	public void testGetColumnNameRemoved() {
		getFirstCharValueModel().setValue(false);
		testGetColumnNameFirstMissingHelper();
	}

	//It is not possible to change the contents of Valuemodel which contains the set of studies. 
	@Ignore
	@Test
	public void testChangeContentsFiresTableChanged() {
		TableModelListener mock = JUnitUtil.mockTableModelListener(new TableModelEvent(d_model));
		d_model.addTableModelListener(mock);
		
		verify(mock);
	}

	private void testGetColumnNameFirstMissingHelper() {
		assertEquals("Study ID", d_model.getColumnName(0));
		int column = 0;
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			if (column > 0) {
				assertEquals(c.getDescription(), d_model.getColumnName(column));
			}
			++column;
		}
	}
}
