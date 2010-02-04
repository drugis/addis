package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Test;

public class MeasurementTableModelTest {

	private Study d_standardStudy;
	private PresentationModelFactory d_pmf;
	private MeasurementTableModel model;
	
	@Before
	public void setUp() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_pmf = new PresentationModelFactory(domain);
		d_standardStudy = ExampleData.buildStudyDeWilde();
		model = new MeasurementTableModel(d_standardStudy, d_pmf);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_standardStudy.getArms().size(), model.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_standardStudy.getOutcomeMeasures().size(), model.getRowCount());
	}

	@Test
	public void testGetValueAt() {
		final int col = 0;
		final int row = 0;
		
		String expected = d_pmf.getLabeledModel(d_standardStudy.getMeasurement (
						new ArrayList<OutcomeMeasure>(d_standardStudy.getOutcomeMeasures()).get(row),d_standardStudy.getArms().get(col))).getLabelModel().getString();
		String actual = ((LabeledPresentationModel) model.getValueAt(row, col)).getLabelModel().getString();
		assertEquals(expected, actual);
	}
	
	@Test
	public void testGetColumnName() {
		for (int i=0;i<d_standardStudy.getArms().size();i++) {
			String exp = d_pmf.getLabeledModel(d_standardStudy.getArms().get(i)).getLabelModel().getString();
			String cname = model.getColumnName(i);
			assertEquals(exp, cname);
		}
	}
}
