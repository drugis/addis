/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class PopulationCharTableModelTest {
	private Study d_study;
	private PresentationModelFactory d_pmf;
	private PopulationCharTableModel d_model;
	
	@Before
	public void setUp() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_pmf = new PresentationModelFactory(domain);
		d_study = ExampleData.buildStudyDeWilde();
		List<PopulationCharacteristic> chars = new ArrayList<PopulationCharacteristic>();
		chars.add(ExampleData.buildGenderVariable());
		chars.add(ExampleData.buildAgeVariable());
		d_study.setPopulationCharacteristics(chars);
		d_study.initializeDefaultMeasurements();
		d_model = new PopulationCharTableModel(d_study, d_pmf);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_study.getArms().size() + 2, d_model.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_study.getVariables(Variable.class).size(), d_model.getRowCount());
	}

	@Test
	public void testGetValueAt() {
		
		int index = 0;
		for (Variable v : d_study.getVariables(Variable.class)) {
			assertEquals(v.getName(), d_model.getValueAt(index, 0));
			assertEquals(d_study.getMeasurement(v, d_study.getArms().get(0)), d_model.getValueAt(index, 1));
			assertEquals(d_study.getMeasurement(v, d_study.getArms().get(1)), d_model.getValueAt(index, 2));
			assertEquals(d_study.getMeasurement(v), d_model.getValueAt(index, 3));
			index++;
		}
	}
	
	@Test
	public void testGetColumnName() {
		assertEquals("Population characteristic", d_model.getColumnName(0));
		for (int i = 0; i < d_study.getArms().size(); i++) {
			String exp = d_pmf.getLabeledModel(d_study.getArms().get(i)).getLabelModel().getString();
			String cname= d_model.getColumnName(i + 1);
			assertEquals(exp, cname);
		}
		assertEquals("Overall", d_model.getColumnName(3));
	}
	
	@Test
	public void testIsCellEditable() {
		for (int i = 0; i < d_model.getRowCount(); i++) {
			for (int j = 0; j < d_model.getColumnCount(); j++) {
				assertFalse(d_model.isCellEditable(i, j));				
			}
		}
	}
	
	@Test 
	public void testTotalMeasurementsChangesFireTableDataChanged() {
		TableModelListener mock = createMock(TableModelListener.class);
		d_model.addTableModelListener(mock);		
		mock.tableChanged((TableModelEvent)JUnitUtil.eqEventObject(new TableModelEvent(d_model)));
		expectLastCall().times(3);
		
		replay(mock);
		
		FrequencyMeasurement meas = (FrequencyMeasurement) d_study.getMeasurement(
				d_study.getPopulationCharacteristics().get(0));
		// 1 tableupdate
		meas.setSampleSize(667);
		// 2 tableupdates
		meas.setFrequency("Male", 50);
		verify(mock);
	}	
}
