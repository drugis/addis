package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Study;
import org.hamcrest.core.IsInstanceOf;
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
	
	@Test
	public void testGetValueAtUpperRightPart() {
		fail();
	}
	
	@Test
	public void testGetValueAtLowerLeftPart() {
		fail();
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
