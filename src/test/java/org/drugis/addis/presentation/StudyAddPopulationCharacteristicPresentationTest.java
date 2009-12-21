package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.ContinuousVariable;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Test;

public class StudyAddPopulationCharacteristicPresentationTest {
	private DomainImpl d_domain;
	private StudyPresentationModel d_studyModel;
	private StudyAddPopulationCharacteristicPresentation d_model;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		PresentationModelFactory pmf = new PresentationModelFactory(d_domain);
		d_studyModel = new StudyPresentationModel(new Study("X", new Indication(0L, "Y")), pmf );
		d_model = new StudyAddPopulationCharacteristicPresentation(d_studyModel, d_domain);
	}
	
	@Test
	public void testGetList() {
		assertEquals(d_domain.getVariablesHolder().getValue(),
				d_model.getVariableList());
		ContinuousVariable age = new ContinuousVariable("Age");
		d_domain.addVariable(age);
		assertEquals(d_domain.getVariablesHolder().getValue(),
				d_model.getVariableList());
		d_studyModel.getBean().setPopulationCharacteristic(age, age.buildMeasurement());
		assertEquals(Collections.emptyList(), d_model.getVariableList());
	}
	
	@Test
	public void testGetVariableModel() {
		assertNotNull(d_model.getVariableModel());
		assertEquals(null, d_model.getVariableModel().getValue());
	}
	
	@Test
	public void testGetMeasurement() {
		ContinuousVariable v = new ContinuousVariable("Age");
		d_domain.addVariable(v);
		assertNull(d_model.getMeasurementModel().getValue());
		d_model.getVariableModel().setValue(v);
		assertTrue(d_model.getMeasurementModel().getValue() instanceof ContinuousMeasurement);
	}
	
	@Test
	public void testAddToStudy() {
		ContinuousVariable v = new ContinuousVariable("Age");
		d_domain.addVariable(v);
		d_model.getVariableModel().setValue(v);
		d_model.addToStudy();
		assertTrue(d_studyModel.getBean().getPopulationCharacteristics().keySet().contains(v));
	}
}
