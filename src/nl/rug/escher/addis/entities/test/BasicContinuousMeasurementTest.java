package nl.rug.escher.addis.entities.test;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import nl.rug.escher.addis.entities.BasicContinuousMeasurement;
import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Test;

public class BasicContinuousMeasurementTest {
	@Test
	public void testSetMean() {
		JUnitUtil.testSetter(new BasicContinuousMeasurement(), BasicContinuousMeasurement.PROPERTY_MEAN, null, 25.91);
	}
	
	@Test
	public void testSetStdDev() {
		JUnitUtil.testSetter(new BasicContinuousMeasurement(), BasicContinuousMeasurement.PROPERTY_STDDEV, null, 0.46);
	}
	
	@Test
	public void testToString() {
		BasicContinuousMeasurement m = new BasicContinuousMeasurement();
		assertEquals("INCOMPLETE", m.toString());
		m.setMean(0.0);
		m.setStdDev(1.0);
		assertEquals("0.0 \u00B1 1.0", m.toString());
	}
	
	@Test
	public void testFireLabelChanged() {
		BasicContinuousMeasurement measurement = new BasicContinuousMeasurement();
		measurement.setMean(25.5);
		PropertyChangeListener l = JUnitUtil.mockListener(
				measurement, Measurement.PROPERTY_LABEL, "INCOMPLETE", "25.5 \u00B1 1.1");
		measurement.addPropertyChangeListener(l);
		measurement.setStdDev(1.1);
		verify(l);
		
		measurement.removePropertyChangeListener(l);
		l = JUnitUtil.mockListener(
				measurement, Measurement.PROPERTY_LABEL, "25.5 \u00B1 1.1", "27.5 \u00B1 1.1");
		measurement.addPropertyChangeListener(l);
		measurement.setMean(27.5);
		verify(l);
	}
}
