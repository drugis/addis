package org.drugis.addis.presentation;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.CategoricalPopulationCharacteristic;
import org.drugis.addis.entities.FrequencyMeasurement;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class FrequencyMeasurementPresentationTest {
	private CategoricalPopulationCharacteristic d_variable;
	private FrequencyMeasurement d_measurement;
	private FrequencyMeasurementPresentation d_pm;
	
	@Before
	public void setUp() {
		d_variable = new CategoricalPopulationCharacteristic("Gender", new String[] {"Male", "Female"});
		d_measurement = d_variable.buildMeasurement();
		d_pm = new FrequencyMeasurementPresentation(d_measurement);
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("Male = 0 / Female = 0", d_pm.getLabelModel().getValue());
	}
	
	@Test
	public void testFireLabelMaleChanged() {
		AbstractValueModel lm = d_pm.getLabelModel();
		PropertyChangeListener mock = createMock(PropertyChangeListener.class);
		PropertyChangeEvent event = new PropertyChangeEvent(
				lm, "value", null, "Male = 1 / Female = 0");
		mock.propertyChange(JUnitUtil.eqPropertyChangeEvent(event));
		expectLastCall().anyTimes();
		replay(mock);
		PropertyChangeListener l = mock;
		lm.addPropertyChangeListener(l);
		d_measurement.setFrequency("Male", 1);
		verify(l);
	}
	
	@Test
	public void testFireLabelFemaleChanged() {
		AbstractValueModel lm = d_pm.getLabelModel();
		PropertyChangeListener mock = createMock(PropertyChangeListener.class);
		PropertyChangeEvent event = new PropertyChangeEvent(
				lm, "value", null, "Male = 0 / Female = 100");
		mock.propertyChange(JUnitUtil.eqPropertyChangeEvent(event));
		expectLastCall().anyTimes();
		replay(mock);
		PropertyChangeListener l = mock;
		lm.addPropertyChangeListener(l);
		d_measurement.setFrequency("Female", 100);
		verify(l);
	}
	
	@Test
	public void testGetFrequencyModel() {
		AbstractValueModel vm = d_pm.getFrequencyModel("Male");
		PropertyChangeListener l = JUnitUtil.mockListener(vm, "value", null, 50);
		vm.addPropertyChangeListener(l);
		d_measurement.setFrequency("Male", 50);
		verify(l);
	}

}
