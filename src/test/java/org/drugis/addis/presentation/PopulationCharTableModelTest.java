package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.TreeSet;

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
	private PopulationCharTableModel model;
	
	@Before
	public void setUp() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_pmf = new PresentationModelFactory(domain);
		d_study = ExampleData.buildStudyDeWilde();
		d_study.setPopulationCharacteristic(ExampleData.buildGenderVariable(), ExampleData.buildGenderVariable().buildMeasurement());
		d_study.setPopulationCharacteristic(ExampleData.buildAgeVariable(), ExampleData.buildGenderVariable().buildMeasurement());
		d_study.getArms().get(0).setPopulationCharacteristic(ExampleData.buildGenderVariable(), ExampleData.buildGenderVariable().buildMeasurement());
		d_study.getArms().get(0).setPopulationCharacteristic(ExampleData.buildAgeVariable(), ExampleData.buildGenderVariable().buildMeasurement());
		d_study.getArms().get(1).setPopulationCharacteristic(ExampleData.buildGenderVariable(), ExampleData.buildGenderVariable().buildMeasurement());
		d_study.getArms().get(1).setPopulationCharacteristic(ExampleData.buildAgeVariable(), ExampleData.buildGenderVariable().buildMeasurement());
		model = new PopulationCharTableModel(d_study, d_pmf);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_study.getArms().size() + 2, model.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_study.getPopulationCharacteristics().size(), model.getRowCount());
	}

	@Test
	public void testGetValueAt() {
		
		int index = 0;
		for (Variable v : new TreeSet<Variable>(d_study.getPopulationCharacteristics().keySet())) {
			assertEquals(v.getName(), model.getValueAt(index, 0));
			assertEquals(d_study.getArms().get(0).getPopulationCharacteristic(v), model.getValueAt(index, 1));
			assertEquals(d_study.getArms().get(1).getPopulationCharacteristic(v), model.getValueAt(index, 2));
			assertEquals(d_study.getPopulationCharacteristic(v), model.getValueAt(index, 3));
			index++;
		}
	}
	
	@Test
	public void testGetColumnName() {
		assertEquals("Variable", model.getColumnName(0));
		for (int i=0;i<d_study.getArms().size();i++) {
			String exp = d_pmf.getLabeledModel(d_study.getArms().get(i)).getLabelModel().getString();
			String cname= model.getColumnName(i+1);
			assertEquals(exp, cname);
		}
		assertEquals("Overall", model.getColumnName(3));
	}
	
	@Test
	public void testIsCellEditable() {
		for (int i=0;i<model.getRowCount();i++) {
			for (int j=0;j<model.getColumnCount();j++) {
				assertFalse(model.isCellEditable(i, j));				
			}
		}
	}
	
	@Ignore
	@Test
	public void fireDataChangedOnEdit() {
		
	}
}
