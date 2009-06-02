package nl.rug.escher.addis.entities.test;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import nl.rug.escher.addis.entities.BasicContinuousMeasurement;
import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Before;
import org.junit.Ignore;
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
		Endpoint e1 = new Endpoint("e1");
		Endpoint e2 = new Endpoint("e2");
		Study s = new Study("STUDY");
		Drug drug1 = new Drug("Drug 1");
		Drug drug2 = new Drug("Drug 2");
		Dose dose = new Dose(8.0, SIUnit.MILLIGRAMS_A_DAY);
		PatientGroup g1 = new PatientGroup(s, drug1, dose, 8,
				new ArrayList<BasicMeasurement>());
		PatientGroup g2 = new PatientGroup(s, drug2, dose, 8,
				new ArrayList<BasicMeasurement>());
		
		JUnitUtil.assertNotEquals(g1, g2);
		
		BasicRateMeasurement m1 = new BasicRateMeasurement(e1);
		m1.setPatientGroup(g1);
		m1.setRate(10);
		BasicRateMeasurement m2 = new BasicRateMeasurement(e1);
		m2.setPatientGroup(g1);
		m2.setRate(50);
		
		assertEquals(m1, m2);
		assertEquals(m1.hashCode(), m2.hashCode());
		
		m2.setPatientGroup(g2);
		JUnitUtil.assertNotEquals(m1, m2);
		
		m2.setPatientGroup(g1);
		m2.setEndpoint(e2);
		JUnitUtil.assertNotEquals(m1, m2);
		
		BasicContinuousMeasurement m3 = new BasicContinuousMeasurement(e1);
		m3.setMean(0.0);
		m3.setStdDev(1.0);
		m3.setPatientGroup(g1);
		assertEquals(m1, m3);
	}
}
