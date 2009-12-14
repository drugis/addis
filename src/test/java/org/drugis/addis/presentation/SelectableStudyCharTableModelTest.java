package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
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
		d_model = new SelectableStudyCharTableModel(d_pm);
	}	
	
	@Test
	public void testGetColumnCount() {
		assertEquals(StudyCharacteristic.values().size() + 2, d_model.getColumnCount());
	}
	
	@Test
	public void testGetValueAt() {
		int row = 0;
		for (Study s : d_pm.getIncludedStudies().getValue()) {
			assertTrue((Boolean)d_model.getValueAt(row, 0));			
			assertEquals(s, d_model.getValueAt(row, 1));
			int column = 2;
			for (StudyCharacteristic c : StudyCharacteristic.values()) {
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
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
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
