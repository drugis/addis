package org.drugis.addis.presentation.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.presentation.StudyCharTableModel;
import org.junit.Test;
import org.junit.Before;

import com.jgoodies.binding.beans.Model;

public class StudyCharTableModelTest {
	private List<Study> d_studies;
	private StudyCharTableModel d_model;
	
	@Before
	public void setUp() {
		Set<Endpoint> endpoints = Collections.emptySet();
		
		d_studies = new ArrayList<Study>();
		
		BasicStudy study1 = new BasicStudy("study1", new Indication(0L, "X"));
		Map<StudyCharacteristic, Model> chars = 
			new HashMap<StudyCharacteristic, Model>(study1.getCharacteristics());
		chars.put(StudyCharacteristic.DUMMY, new Indication(0L, "BLAH"));
		study1.setCharacteristics(chars);
		study1.setEndpoints(endpoints);
		
		BasicStudy study2 = new BasicStudy("study2", new Indication(0L, "X"));
		study2.setEndpoints(endpoints);
		
		d_studies.add(study1);
		d_studies.add(study2);
		
		d_model = new StudyCharTableModel(d_studies);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(StudyCharacteristic.values().length + 1, d_model.getColumnCount());
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(d_studies.size(), d_model.getRowCount());
	}
	
	@Test
	public void testGetValueAt() {
		int row = 0;
		for (Study s : d_studies) {
			assertEquals(s.getId(), d_model.getValueAt(row, 0));
			int column = 1;
			for (StudyCharacteristic c : StudyCharacteristic.values()) {
				assertEquals(s.getCharacteristics().get(c), d_model.getValueAt(row, column));
				++column;
			}
			++row;
		}
	}
}
