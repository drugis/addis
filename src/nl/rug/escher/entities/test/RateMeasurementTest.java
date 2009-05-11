package nl.rug.escher.entities.test;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.RateMeasurement;

import org.junit.Before;
import org.junit.Test;

public class RateMeasurementTest {
	private PatientGroup d_patientGroup;
	private RateMeasurement d_measurement;
	
	@Before
	public void setUp() {
		d_patientGroup = new PatientGroup();
		d_patientGroup.setSize(101);
		d_measurement = new RateMeasurement();
		d_measurement.setPatientGroup(d_patientGroup);
		d_measurement.setRate(67);
	}
	
	@Test
	public void testSetRate() {
		Helper.testSetter(new RateMeasurement(), RateMeasurement.PROPERTY_RATE, null, new Integer(67));
	}
	
	@Test
	public void testToString() {
		assertEquals("67/101", d_measurement.toString());
	}
	
	@Test
	public void testGetMean() {
		assertEquals(67.0 / 101.0, d_measurement.getMean(), 0.001);
	}
	
	@Test
	public void testGetStdDev() {
		assertEquals((67.0 / 101.0) / Math.sqrt(101), d_measurement.getStdDev(), 0.0001);
	}
	
	@Test
	public void testFireLabelChanged() {
		PropertyChangeListener l = Helper.mockListener(
				d_measurement, Measurement.PROPERTY_LABEL, "67/101", "68/101");
		d_measurement.addPropertyChangeListener(l);
		d_measurement.setRate(68);
		verify(l);
		
		d_measurement.removePropertyChangeListener(l);
		l = Helper.mockListener(
				d_measurement, Measurement.PROPERTY_LABEL, "68/101", "68/102");
		d_measurement.addPropertyChangeListener(l);
		d_patientGroup.setSize(102);
		verify(l);
	}	
}
