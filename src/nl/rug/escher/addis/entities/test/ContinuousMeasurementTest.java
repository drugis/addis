package nl.rug.escher.addis.entities.test;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import nl.rug.escher.addis.entities.ContinuousMeasurement;
import nl.rug.escher.addis.entities.Measurement;

import org.junit.Test;

public class ContinuousMeasurementTest {
	@Test
	public void testSetMean() {
		Helper.testSetter(new ContinuousMeasurement(), Measurement.PROPERTY_MEAN, null, 25.91);
	}
	
	@Test
	public void testSetStdDev() {
		Helper.testSetter(new ContinuousMeasurement(), Measurement.PROPERTY_STDDEV, null, 0.46);
	}
	
	@Test
	public void testToString() {
		ContinuousMeasurement m = new ContinuousMeasurement();
		assertEquals("INCOMPLETE", m.toString());
		m.setMean(0.0);
		m.setStdDev(1.0);
		assertEquals("0.0 \u00B1 1.0", m.toString());
	}
	
	@Test
	public void testFireLabelChanged() {
		ContinuousMeasurement measurement = new ContinuousMeasurement();
		measurement.setMean(25.5);
		PropertyChangeListener l = Helper.mockListener(
				measurement, Measurement.PROPERTY_LABEL, "INCOMPLETE", "25.5 \u00B1 1.1");
		measurement.addPropertyChangeListener(l);
		measurement.setStdDev(1.1);
		verify(l);
		
		measurement.removePropertyChangeListener(l);
		l = Helper.mockListener(
				measurement, Measurement.PROPERTY_LABEL, "25.5 \u00B1 1.1", "27.5 \u00B1 1.1");
		measurement.addPropertyChangeListener(l);
		measurement.setMean(27.5);
		verify(l);
	}
}
