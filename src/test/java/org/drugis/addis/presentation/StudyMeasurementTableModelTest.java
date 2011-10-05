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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.addis.entities.Study.StudyOutcomeMeasure;
import org.drugis.addis.presentation.wizard.MissingMeasurementPresentation;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyMeasurementTableModelTest {

	private Study d_standardStudy;
	private PresentationModelFactory d_pmf;
	private StudyMeasurementTableModel d_model;
	private PopulationCharTableModel d_popcharTablemodel;
	private Study d_popcharStudy;
	
	@Before
	public void setUp() {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_pmf = new PresentationModelFactory(domain);
		d_standardStudy = ExampleData.buildStudyDeWilde();
		d_model = new StudyMeasurementTableModel(d_standardStudy, d_pmf, Endpoint.class, false);
		
		// init for specific PopulationCharacteristic tests
		List<StudyOutcomeMeasure<PopulationCharacteristic>> chars = new ArrayList<StudyOutcomeMeasure<PopulationCharacteristic>>();
		chars.add(new StudyOutcomeMeasure<PopulationCharacteristic>(ExampleData.buildGenderVariable(), d_standardStudy.defaultMeasurementMoment()));
		chars.add(new StudyOutcomeMeasure<PopulationCharacteristic>(ExampleData.buildAgeVariable(), d_standardStudy.defaultMeasurementMoment()));
		d_popcharStudy = ExampleData.buildStudyDeWilde().clone();
		d_popcharStudy.getPopulationChars().clear();
		d_popcharStudy.getPopulationChars().addAll(chars);
		d_popcharTablemodel = new PopulationCharTableModel(d_popcharStudy, d_pmf);
	}
		
	@Test
	public void testGetColumnCount() {
		assertEquals(d_standardStudy.getArms().size() + 2, d_model.getColumnCount());
	}
	
	@Test
	public void testGetPopcharColumnCount() {
		assertEquals(d_popcharStudy.getArms().size() + 3, d_popcharTablemodel.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(Study.extractVariables(d_standardStudy.getEndpoints()).size(), d_model.getRowCount());
	}
	
	@Test
	public void testGetPopcharRowCount() {
		assertEquals(d_popcharStudy.getVariables(PopulationCharacteristic.class).size(), d_popcharTablemodel.getRowCount());
	}

	@Test
	public void testGetValueAt() {
		int index = 0;
		for (Variable v : Study.extractVariables(d_standardStudy.getEndpoints())) {
			assertEquals(v.getName(), d_model.getValueAt(index, 0));

			presentMeasurementCorrectlyBound(v, d_standardStudy.getArms().get(0), (MissingMeasurementPresentation)d_model.getValueAt(index, 2));
			presentMeasurementCorrectlyBound(v, d_standardStudy.getArms().get(1), (MissingMeasurementPresentation)d_model.getValueAt(index, 3));
			index++;
		}
	}
	
	@Test
	public void testPopcharGetValueAt() {
		int index = 0;
		for (Variable v : d_popcharStudy.getVariables(PopulationCharacteristic.class)) {
			assertEquals(v.getName(), d_popcharTablemodel.getValueAt(index, 0));
			
			missingMeasurementCorrectlyBound(v, d_popcharStudy.getArms().get(0), (MissingMeasurementPresentation)d_popcharTablemodel.getValueAt(index, 2));
			missingMeasurementCorrectlyBound(v, d_popcharStudy.getArms().get(1), (MissingMeasurementPresentation)d_popcharTablemodel.getValueAt(index, 3));
			missingMeasurementCorrectlyBound(v, null, (MissingMeasurementPresentation)d_popcharTablemodel.getValueAt(index, 4));
			++index;
		}
	}
	
	private void missingMeasurementCorrectlyBound(Variable v, Arm a, MissingMeasurementPresentation mmp) {
		assertEquals(Boolean.TRUE, mmp.getMissingModel().getValue());
		mmp.getMissingModel().setValue(false);
		assertSame(mmp.getMeasurement(), d_popcharStudy.getMeasurement(v, a));
		mmp.getMissingModel().setValue(true);
		assertEquals(null, d_popcharStudy.getMeasurement(v, a));
	}
	
	
	private void presentMeasurementCorrectlyBound(Variable v, Arm a, MissingMeasurementPresentation mmp) {
		assertEquals(Boolean.FALSE, mmp.getMissingModel().getValue());
		assertSame(mmp.getMeasurement(), d_standardStudy.getMeasurement(v, a));
		mmp.getMissingModel().setValue(true);
		assertEquals(null, d_standardStudy.getMeasurement(v, a));
		assertEquals(Boolean.TRUE, mmp.getMissingModel().getValue());
		mmp.getMissingModel().setValue(false);
	}	
	
	@Test
	public void testGetColumnName() {
		assertEquals("Endpoint", d_model.getColumnName(0));
		for (int i = 0; i < d_standardStudy.getArms().size(); ++i) {
			String exp = d_pmf.getLabeledModel(d_standardStudy.getArms().get(i)).getLabelModel().getString();
			String cname = d_model.getColumnName(i + 2);
			assertEquals(exp, cname);
		}
	}
	
	@Test
	public void testGetPopcharColumnName() {
		assertEquals("Population characteristic", d_popcharTablemodel.getColumnName(0));
		for (int i = 0; i < d_popcharStudy.getArms().size(); i++) {
			String exp = d_pmf.getLabeledModel(d_popcharStudy.getArms().get(i)).getLabelModel().getString();
			String cname= d_popcharTablemodel.getColumnName(i + 2);
			assertEquals(exp, cname);
		}
		assertEquals("Overall", d_popcharTablemodel.getColumnName(4));
	}
	
	@Test
	public void testGetWhenTaken() {
		assertEquals("0 weeks from start of Main phase", d_model.getValueAt(0, 1).toString());
		assertEquals("0 weeks before end of Main phase", d_model.getValueAt(0, 1).toString());
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
	public void testAllMeasurementsChangesFireTableDataChanged() {
		TableModelListener mock = createMock(TableModelListener.class);
		d_model.addTableModelListener(mock);		
		mock.tableChanged((TableModelEvent)JUnitUtil.eqEventObject(new TableModelEvent(d_model)));
		expectLastCall().times(2);
		
		replay(mock);
		
		BasicRateMeasurement meas = (BasicRateMeasurement) d_standardStudy.getMeasurement(
				d_standardStudy.getOutcomeMeasures().get(0),
				d_standardStudy.getArms().get(0));
		meas.setSampleSize(667);
		meas.setRate(666);
		verify(mock);
	}
	
	@Test 
	public void testTotalPopcharMeasurementsChangesFireTableDataChanged() {
		((MissingMeasurementPresentation)d_popcharTablemodel.getValueAt(0, d_popcharTablemodel.getColumnCount()-1)).getMissingModel().setValue(false);
		TableModelListener mock = createMock(TableModelListener.class);
		d_popcharTablemodel.addTableModelListener(mock);		
		mock.tableChanged((TableModelEvent)JUnitUtil.eqEventObject(new TableModelEvent(d_popcharTablemodel)));
		expectLastCall().times(3);
		
		replay(mock);
		
		FrequencyMeasurement meas = (FrequencyMeasurement) d_popcharStudy.getMeasurement(
				Study.extractVariables(d_popcharStudy.getPopulationChars()).get(0));
		// 1 tableupdate
		meas.setSampleSize(667);
		// 2 tableupdates
		meas.setFrequency("Male", 50);
		verify(mock);
		// 2! 2 tableupdates! HA HA HA! *KABOOM*
	}	
}
