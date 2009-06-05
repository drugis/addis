package nl.rug.escher.addis.entities.test;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import nl.rug.escher.addis.entities.BasicContinuousMeasurement;
import nl.rug.escher.addis.entities.BasicMeasurement;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.Measurement;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.common.JUnitUtil;

import org.junit.Before;
import org.junit.Test;

public class BasicContinuousMeasurementTest {
	private Endpoint d_endpoint;
	
	@Before
	public void setUp() {
		d_endpoint = new Endpoint("X");
	}
	
	@Test
	public void testSetMean() {
		JUnitUtil.testSetter(new BasicContinuousMeasurement(d_endpoint),
				BasicContinuousMeasurement.PROPERTY_MEAN, 0.0, 25.91);
	}
	
	@Test
	public void testSetStdDev() {
		JUnitUtil.testSetter(new BasicContinuousMeasurement(d_endpoint), BasicContinuousMeasurement.PROPERTY_STDDEV, 0.0, 0.46);
	}
	
	@Test
	public void testToString() {
		BasicContinuousMeasurement m = new BasicContinuousMeasurement(d_endpoint);
		assertEquals("0.0 \u00B1 0.0", m.toString());
	}
	
	@Test
	public void testFireLabelChanged() {
		BasicContinuousMeasurement measurement = new BasicContinuousMeasurement(d_endpoint);
		measurement.setMean(25.5);
		PropertyChangeListener l = JUnitUtil.mockListener(
				measurement, Measurement.PROPERTY_LABEL, "25.5 \u00B1 0.0", "25.5 \u00B1 1.1");
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
	
	@Test
	public void testEquals() {
		Endpoint e1 = new Endpoint("e1");
		Endpoint e2 = new Endpoint("e2");
		BasicStudy s = new BasicStudy("STUDY");
		Drug drug1 = new Drug("Drug 1");
		Drug drug2 = new Drug("Drug 2");
		Dose dose = new Dose(8.0, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroup g1 = new BasicPatientGroup(s, drug1, dose, 8,
				new ArrayList<BasicMeasurement>());
		BasicPatientGroup g2 = new BasicPatientGroup(s, drug2, dose, 8,
				new ArrayList<BasicMeasurement>());
		
		JUnitUtil.assertNotEquals(g1, g2);
		
		BasicContinuousMeasurement m1 = new BasicContinuousMeasurement(e1);
		m1.setPatientGroup(g1);
		m1.setMean(0.0);
		m1.setStdDev(0.0);
		BasicContinuousMeasurement m2 = new BasicContinuousMeasurement(e1);
		m2.setPatientGroup(g1);
		m2.setMean(3.0);
		m2.setStdDev(2.0);
		
		assertEquals(m1, m2);
		assertEquals(m1.hashCode(), m2.hashCode());
		
		m2.setPatientGroup(g2);
		JUnitUtil.assertNotEquals(m1, m2);
		
		m2.setPatientGroup(g1);
		m2.setEndpoint(e2);
		JUnitUtil.assertNotEquals(m1, m2);
		
		BasicRateMeasurement m3 = new BasicRateMeasurement(e1);
		m3.setPatientGroup(g1);
		assertEquals(m1, m3);
	}
}
