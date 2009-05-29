package nl.rug.escher.addis.entities.test;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.common.JUnitUtil;

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
	public void testSerialization() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(d_measurement);
		ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(bos.toByteArray()));
		d_measurement = (BasicRateMeasurement) ois.readObject();
		
		String oldLabel = d_measurement.getLabel();
		String newLabel = "67/105";
		PropertyChangeListener mock = JUnitUtil.mockListener(d_measurement, 
				BasicRateMeasurement.PROPERTY_LABEL, oldLabel, newLabel);
		d_measurement.addPropertyChangeListener(mock);
		d_measurement.getPatientGroup().setSize(105);
		verify(mock);
	}
	
	@Test
	public void testSetRate() {
		JUnitUtil.testSetter(new BasicRateMeasurement(), BasicRateMeasurement.PROPERTY_RATE, null, new Integer(67));
	}
	
	@Test
	public void testToString() {
		assertEquals("67/101", d_measurement.toString());
	}
	
	@Test
	public void testFireSizeChanged() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_measurement, BasicRateMeasurement.PROPERTY_SAMPLESIZE, 101, 102);
		d_measurement.addPropertyChangeListener(l);
		d_patientGroup.setSize(102);
		verify(l);
		
	}
	
	@Test
	public void testFireLabelChanged() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_measurement, Measurement.PROPERTY_LABEL, "67/101", "68/101");
		d_measurement.addPropertyChangeListener(l);
		d_measurement.setRate(68);
		verify(l);
		
		d_measurement.removePropertyChangeListener(l);
		l = JUnitUtil.mockListener(
				d_measurement, Measurement.PROPERTY_LABEL, "68/101", "68/102");
		d_measurement.addPropertyChangeListener(l);
		d_patientGroup.setSize(102);
		verify(l);
	}	
	

	@Test
	public void testEquals() {
		fail();
		// also test hashCode() for one case where equals is true
	}
}
