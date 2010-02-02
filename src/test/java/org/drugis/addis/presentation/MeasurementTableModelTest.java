package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import javax.swing.JDialog;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.junit.Test;

public class MeasurementTableModelTest {

	private Study d_standardStudy;
	private Study d_threeArmStudy;
	private Endpoint d_endpoint;
	private PresentationModelFactory d_pmf;
	
	@Test
	public void testGetColumnCount() {
		baseSetUpRate();
		MeasurementTable tm = new MeasurementTable(d_standardStudy,d_pmf, new JDialog());
		assertEquals(d_standardStudy.getArms().size(), tm.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		baseSetUpRate();
		MeasurementTable tm = new MeasurementTable(d_standardStudy,d_pmf, new JDialog());
		assertEquals(d_standardStudy.getOutcomeMeasures().size(), tm.getRowCount());

	}

	@Test
	public void testGetValueAt() {
		baseSetUpRate();
		final int col = 1;
		final int row = 1;
		
		MeasurementTable tm = new MeasurementTable(d_threeArmStudy,d_pmf, new JDialog());
		String expected = d_pmf.getLabeledModel(d_threeArmStudy.getMeasurement (new ArrayList<OutcomeMeasure>(d_threeArmStudy.getOutcomeMeasures()).get(row),d_threeArmStudy.getArms().get(col))).getLabelModel().getString();
		String actual = ((LabeledPresentationModel) tm.getValueAt(row, col)).getLabelModel().getString();
		assertEquals(expected, actual);
	}

	protected void baseSetUpRate() {
		d_standardStudy = ExampleData.buildStudyDeWilde();
		d_threeArmStudy = ExampleData.buildAdditionalStudyThreeArm();
		d_endpoint = ExampleData.buildEndpointHamd();
		DomainImpl domain = new DomainImpl();
		d_pmf = new PresentationModelFactory(domain);
	}
	
	protected void baseSetUpContinuous() {
		d_standardStudy = ExampleData.buildStudyChouinard();
		d_threeArmStudy = ExampleData.buildAdditionalStudyThreeArm();
		d_endpoint = ExampleData.buildEndpointCgi();
		DomainImpl domain = new DomainImpl();
		d_pmf = new PresentationModelFactory(domain);
	}
	
}
