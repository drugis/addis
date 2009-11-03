package org.drugis.addis.entities;

import static org.junit.Assert.*;

import org.drugis.addis.entities.Endpoint.Type;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MeanDifferenceTest {
	private static final double s_mean1 = 0.23424;
	private static final double s_mean2 = 4.78111;
	private static final double s_stdDev1 = 0.2;
	private static final double s_stdDev2 = 2.5;
//	private MeanDifference d_md;
	private BasicContinuousMeasurement d_numerator;
	private BasicContinuousMeasurement d_denominator;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E", Type.CONTINUOUS);
		
//		d_numerator = new BasicContinuousMeasurement(e, p);
//		d_md = new MeanDifference( );
	}
	
	@Ignore
	@Test
	public void testGetMean() {
		fail();
	}
	
	@Ignore
	@Test
	public void testGetError() {
		fail();
	}

}
