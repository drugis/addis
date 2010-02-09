package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.junit.Before;
import org.junit.Ignore;
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
		List<Variable> chars = new ArrayList<Variable>();
		chars.add(ExampleData.buildGenderVariable());
		chars.add(ExampleData.buildAgeVariable());
		d_study.setPopulationCharacteristics(chars);
		d_model = new PopulationCharTableModel(d_study, d_pmf);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_study.getArms().size() + 2, d_model.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_study.getPopulationCharacteristics().size(), d_model.getRowCount());
	}

	@Test
	public void testGetValueAt() {
		
		int index = 0;
		for (Variable v : d_study.getPopulationCharacteristics()) {
			assertEquals(v.getName(), d_model.getValueAt(index, 0));
			assertEquals(d_study.getMeasurement(v, d_study.getArms().get(0)), d_model.getValueAt(index, 1));
			assertEquals(d_study.getMeasurement(v, d_study.getArms().get(1)), d_model.getValueAt(index, 2));
			assertEquals(d_study.getMeasurement(v), d_model.getValueAt(index, 3));
			index++;
		}
	}
	
	@Test
	public void testGetColumnName() {
		assertEquals("Variable", d_model.getColumnName(0));
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
	
	@Ignore
	@Test
	public void fireDataChangedOnEdit() {
		
	}
}
