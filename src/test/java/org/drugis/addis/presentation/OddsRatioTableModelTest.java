package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.PatientGroup;
import org.drugis.addis.entities.Ratio;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class OddsRatioTableModelTest {
	Study d_standardStudy;
	Study d_threeArmStudy;
	OddsRatioTableModel d_stdModel;
	OddsRatioTableModel d_threeArmModel;
	Endpoint d_endpoint;
	
	@Before
	public void setUp() {
		d_standardStudy = ExampleData.buildDefaultStudy2();
		d_threeArmStudy = ExampleData.buildAdditionalStudyThreeArm();
		d_endpoint = ExampleData.buildEndpointHamd();
		DomainImpl domain = new DomainImpl();
		PresentationModelManager manager = new PresentationModelManager(domain);	
		d_stdModel = new OddsRatioTableModel(d_standardStudy, d_endpoint, manager);
		d_threeArmModel = new OddsRatioTableModel(d_threeArmStudy, d_endpoint, manager);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_standardStudy.getPatientGroups().size(), d_stdModel.getColumnCount());
		assertEquals(d_threeArmStudy.getPatientGroups().size(), d_threeArmModel.getColumnCount());
	}
	
	@Test
	public void testGetRowCount() {
		assertEquals(d_standardStudy.getPatientGroups().size(), d_stdModel.getRowCount());
		assertEquals(d_threeArmStudy.getPatientGroups().size(), d_threeArmModel.getRowCount());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtUpperRightPart() {
		assertEquals(3, d_threeArmStudy.getPatientGroups().size());
		PatientGroup pg0 = d_threeArmStudy.getPatientGroups().get(0);
		PatientGroup pg1 = d_threeArmStudy.getPatientGroups().get(1);
		PatientGroup pg2 = d_threeArmStudy.getPatientGroups().get(2);
		
		PresentationModel<Ratio> val01 = (PresentationModel<Ratio>)d_threeArmModel.getValueAt(0, 1);
		assertTrue(val01.getBean() instanceof OddsRatio);
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg0), val01.getBean().getDenominator());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val01.getBean().getNumerator());
		
		PresentationModel<Ratio> val12 = (PresentationModel<Ratio>)d_threeArmModel.getValueAt(1, 2);
		assertTrue(val12.getBean() instanceof OddsRatio);
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val12.getBean().getDenominator());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val12.getBean().getNumerator());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtLowerLeftPart() {
		assertEquals(3, d_threeArmStudy.getPatientGroups().size());
		PatientGroup pg0 = d_threeArmStudy.getPatientGroups().get(0);
		PatientGroup pg1 = d_threeArmStudy.getPatientGroups().get(1);
		PatientGroup pg2 = d_threeArmStudy.getPatientGroups().get(2);
		
		PresentationModel<Ratio> val20 = (PresentationModel<Ratio>)d_threeArmModel.getValueAt(2, 0);
		assertTrue(val20.getBean() instanceof OddsRatio);
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val20.getBean().getDenominator());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg0), val20.getBean().getNumerator());
		
		PresentationModel<Ratio> val21 = (PresentationModel<Ratio>)d_threeArmModel.getValueAt(2, 1);
		assertTrue(val21.getBean() instanceof OddsRatio);
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg2), val21.getBean().getDenominator());
		assertEquals(d_threeArmStudy.getMeasurement(d_endpoint, pg1), val21.getBean().getNumerator());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtDiagonal() {
		for (int i = 0; i < d_standardStudy.getPatientGroups().size(); ++i) {
			Object val = d_stdModel.getValueAt(i, i);
			assertTrue("Instance of PresentationModel", val instanceof PresentationModel);
			assertEquals(((PresentationModel) val).getBean(), d_standardStudy.getPatientGroups().get(i));
		}
		for (int i = 0; i < d_threeArmStudy.getPatientGroups().size(); ++i) {
			Object val = d_threeArmModel.getValueAt(i, i);
			assertTrue("Instance of PresentationModel", val instanceof PresentationModel);
			assertEquals(((PresentationModel) val).getBean(), d_threeArmStudy.getPatientGroups().get(i));
		}
	}
}
