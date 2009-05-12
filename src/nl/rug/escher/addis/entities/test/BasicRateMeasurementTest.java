package nl.rug.escher.addis.entities.test;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.BasicRateMeasurement;

import org.junit.Before;
import org.junit.Test;

public class BasicRateMeasurementTest {
	private PatientGroup d_patientGroup;
	private BasicRateMeasurement d_measurement;
	
	@Before
	public void setUp() {
		d_patientGroup = new PatientGroup();
		d_patientGroup.setSize(101);
		d_measurement = new BasicRateMeasurement();
		d_measurement.setPatientGroup(d_patientGroup);
		d_measurement.setRate(67);
	}
	
	@Test
	public void testSetRate() {
		Helper.testSetter(new BasicRateMeasurement(), BasicRateMeasurement.PROPERTY_RATE, null, new Integer(67));
	}
	
	@Test
	public void testToString() {
		assertEquals("67/101", d_measurement.toString());
	}
	
	@Test
	public void testFireSizeChanged() {
		PropertyChangeListener l = Helper.mockListener(
				d_measurement, BasicRateMeasurement.PROPERTY_SIZE, 101, 102);
		d_measurement.addPropertyChangeListener(l);
		d_patientGroup.setSize(102);
		verify(l);
		
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
