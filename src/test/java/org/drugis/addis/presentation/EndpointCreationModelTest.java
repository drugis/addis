package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.OutcomeMeasure;
import org.junit.Before;
import org.junit.Test;

public class EndpointCreationModelTest {
	private EndpointCreationModel d_pm;

	@Before
	public void setUp() {
		Endpoint endpoint = new Endpoint("X", OutcomeMeasure.Type.RATE);
		d_pm = new EndpointCreationModel(endpoint);
	}
	
	@Test
	public void testClearDefaultUOM() {
		assertEquals(OutcomeMeasure.UOM_DEFAULT_RATE, d_pm.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(OutcomeMeasure.Type.CONTINUOUS);
		assertEquals(OutcomeMeasure.UOM_DEFAULT_CONTINUOUS, d_pm.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
	}
	
	@Test
	public void testNotClearChangedUOM() {
		final String newValue = "Occurence of Headache (multiple per patient possible)";
		d_pm.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).setValue(newValue);
		assertEquals(newValue, d_pm.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(OutcomeMeasure.Type.CONTINUOUS);
		assertEquals(newValue, d_pm.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
	}
	
	@Test
	public void testNotClearContinuousUOM() {
		final String newValue = "Percentage lowering of blood pressure";
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(OutcomeMeasure.Type.CONTINUOUS);
		d_pm.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).setValue(newValue);
		assertEquals(newValue, d_pm.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(OutcomeMeasure.Type.RATE);
		assertEquals(newValue, d_pm.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
	}
	
	@Test
	public void testSetDefaultIfEmptyUOM() {
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(OutcomeMeasure.Type.CONTINUOUS);
		assertEquals(OutcomeMeasure.UOM_DEFAULT_CONTINUOUS, d_pm.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(OutcomeMeasure.Type.RATE);
		assertEquals(OutcomeMeasure.UOM_DEFAULT_RATE, d_pm.getModel(OutcomeMeasure.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
	}
}
