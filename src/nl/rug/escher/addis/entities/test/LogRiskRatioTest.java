package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.LogRiskRatio;
import nl.rug.escher.addis.entities.Endpoint.Type;
import nl.rug.escher.common.Interval;

public class LogRiskRatioTest {

	private LogRiskRatio d_ratio;

	@Before
	public void setUp() {
		Endpoint e = new Endpoint("e", Type.RATE);
		BasicRateMeasurement r1 = new BasicRateMeasurement(e, 341, 595);
		BasicRateMeasurement r2 = new BasicRateMeasurement(e, 377, 595);
		d_ratio = new LogRiskRatio(r1, r2);
	}
	
	@Test
	public void testGetMean() {
		assertEquals(Math.log(1.105), d_ratio.getMean(), 0.001);
	}
	
	@Test
	public void testGetStdDev() {
		assertEquals(0.04715, d_ratio.getStdDev(), 0.00001);
	}
	
	@Test
	public void testGetConfidenceInterval() {
		Interval<Double> ival = d_ratio.getConfidenceInterval();
		assertEquals(1.01, ival.getLowerBound(), 0.01);
		assertEquals(1.20, ival.getUpperBound(), 0.01);
	}
}
