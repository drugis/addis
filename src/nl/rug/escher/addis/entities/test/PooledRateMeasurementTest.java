package nl.rug.escher.addis.entities.test;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;

import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.PooledRateMeasurement;
import nl.rug.escher.addis.entities.RateMeasurement;
import nl.rug.escher.common.JUnitUtil;

import org.contract4j5.errors.ContractError;
import org.junit.Before;
import org.junit.Test;

public class PooledRateMeasurementTest {
	@Test(expected=ContractError.class)
	public void testConstructWithNull() {
		new PooledRateMeasurement(null);
	}
	
	@Test(expected=ContractError.class)
	public void testConstructWithEmpty() {
		new PooledRateMeasurement(new ArrayList<RateMeasurement>());
	}
	
	@Test(expected=ContractError.class)
	public void testConstructWithDifferentEndpoints() {
		d_m1 = new BasicRateMeasurement(new Endpoint());
		d_m1.setRate(12);
		d_m1.setPatientGroup(d_g1);
		new PooledRateMeasurement(Arrays.asList(
				new RateMeasurement[] {d_m1, d_m2}));
	}
	
	Endpoint d_e;
	BasicRateMeasurement d_m1;
	BasicRateMeasurement d_m2;
	PooledRateMeasurement d_m;
	PatientGroup d_g1;
	PatientGroup d_g2;
	
	@Before
	public void setUp() {
		d_e = new Endpoint();
		d_g1 = new PatientGroup();
		d_g1.setSize(100);
		d_m1 = new BasicRateMeasurement(d_e);
		d_m1.setRate(12);
		d_m1.setPatientGroup(d_g1);
		d_g2 = new PatientGroup();
		d_g2.setSize(50);
		d_m2 = new BasicRateMeasurement(d_e);
		d_m2.setRate(18);
		d_m2.setPatientGroup(d_g2);
		d_m = new PooledRateMeasurement(Arrays.asList(new RateMeasurement[] {d_m1, d_m2}));
	}
	
	@Test
	public void testGetRate() {
		assertEquals(new Integer(d_m1.getRate() + d_m2.getRate()), d_m.getRate());
		d_m1.setRate(50);
		assertEquals(new Integer(d_m1.getRate() + d_m2.getRate()), d_m.getRate());
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(d_e, d_m.getEndpoint());
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(new Integer(d_m1.getSampleSize() + d_m2.getSampleSize()), d_m.getSampleSize());
		d_m2.getPatientGroup().setSize(1000);
		assertEquals(new Integer(d_m1.getSampleSize() + d_m2.getSampleSize()), d_m.getSampleSize());
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("30/150", d_m.getLabel());
	}
	
	@Test
	public void testFireRateChanged() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_m, RateMeasurement.PROPERTY_RATE, d_m.getRate(), d_m.getRate() + 10);
		d_m.addPropertyChangeListener(l);
		d_m1.setRate(d_m1.getRate() + 10);
		verify(l);
	}
	
	@Test
	public void testFireSampleSizeChanged() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_m, RateMeasurement.PROPERTY_SAMPLESIZE, d_m.getSampleSize(), d_m.getSampleSize() + 10);
		d_m.addPropertyChangeListener(l);
		d_m1.getPatientGroup().setSize(d_m1.getPatientGroup().getSize() + 10);
		verify(l);
	}
	
	@Test
	public void testFireLabelChangedOnRate() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_m, RateMeasurement.PROPERTY_LABEL, "30/150", "40/150");
		d_m.addPropertyChangeListener(l);
		d_m1.setRate(d_m1.getRate() + 10);
		verify(l);
	}
	
	@Test
	public void testFireLabelChangedOnSampleSize() {
		PropertyChangeListener l = JUnitUtil.mockListener(
				d_m, RateMeasurement.PROPERTY_LABEL, "30/150", "30/250");
		d_m.addPropertyChangeListener(l);
		d_m1.getPatientGroup().setSize(d_m1.getPatientGroup().getSize() + 100);
		verify(l);
	}
	
	@Test(expected=RuntimeException.class)
	public void testFailEndpointChanged() {
		d_m2.setEndpoint(new Endpoint());
	}
}
