package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Variable;
import org.junit.Before;
import org.junit.Test;

public class EndpointCreationModelTest {
	private OutcomeMeasureCreationModel d_pm;

	@Before
	public void setUp() {
		Endpoint endpoint = new Endpoint("X", Variable.Type.RATE);
		d_pm = new OutcomeMeasureCreationModel(endpoint);
	}
	
	@Test
	public void testClearDefaultUOM() {
		assertEquals(Variable.UOM_DEFAULT_RATE, d_pm.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(Variable.Type.CONTINUOUS);
		assertEquals(Variable.UOM_DEFAULT_CONTINUOUS, d_pm.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
	}
	
	@Test
	public void testNotClearChangedUOM() {
		final String newValue = "Occurence of Headache (multiple per patient possible)";
		d_pm.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).setValue(newValue);
		assertEquals(newValue, d_pm.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(Variable.Type.CONTINUOUS);
		assertEquals(newValue, d_pm.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
	}
	
	@Test
	public void testNotClearContinuousUOM() {
		final String newValue = "Percentage lowering of blood pressure";
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(Variable.Type.CONTINUOUS);
		d_pm.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).setValue(newValue);
		assertEquals(newValue, d_pm.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(Variable.Type.RATE);
		assertEquals(newValue, d_pm.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
	}
	
	@Test
	public void testSetDefaultIfEmptyUOM() {
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(Variable.Type.CONTINUOUS);
		assertEquals(Variable.UOM_DEFAULT_CONTINUOUS, d_pm.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
		d_pm.getModel(Endpoint.PROPERTY_TYPE).setValue(Variable.Type.RATE);
		assertEquals(Variable.UOM_DEFAULT_RATE, d_pm.getModel(Variable.PROPERTY_UNIT_OF_MEASUREMENT).getValue());
	}
}
