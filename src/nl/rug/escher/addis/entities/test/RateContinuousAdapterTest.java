package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.ContinuousMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.RateContinuousAdapter;

import org.junit.Before;
import org.junit.Test;

public class RateContinuousAdapterTest {
	BasicRateMeasurement d_rate;
	ContinuousMeasurement d_continuous;
	
	@Before
	public void setUp() {
		d_rate = new BasicRateMeasurement(new Endpoint("e"));
		d_rate.setRate(50);
		BasicPatientGroup g = new BasicPatientGroup(null, null, null, 100);
		g.addMeasurement(d_rate);
		
		d_continuous = new RateContinuousAdapter(d_rate);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(0.5, d_continuous.getMean(), 0.000001);
	}
	
	@Test
	public void testGetStdDev() {
		assertEquals(0.5/Math.sqrt(100), d_continuous.getStdDev(), 0.0000001);
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(100, (int)d_continuous.getSampleSize());
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("0.5 \u00B1 0.05", d_continuous.getLabel());
	}
	
	@Test
	public void testChangeEvents() {
		fail();
	}
}
