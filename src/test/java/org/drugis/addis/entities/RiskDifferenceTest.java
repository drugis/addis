package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class RiskDifferenceTest {
	private static final int s_sizeNum = 142;
	private static final int s_sizeDen = 144;
	private static final int s_effectNum = 73;
	private static final int s_effectDen = 63;
	private static final double s_meanNum = (double)s_effectNum / (double)(s_sizeNum - s_effectNum); 
	private static final double s_meanDen = (double)s_effectDen / (double)(s_sizeDen - s_effectDen);
	private static final double s_stdDevNum = s_meanNum / Math.sqrt(s_sizeNum);
	private static final double s_stdDevDen = s_meanDen / Math.sqrt(s_sizeDen);
	
	BasicRateMeasurement d_numerator;
	BasicRateMeasurement d_denominator;
	RiskDifference d_riskDif;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E", Type.RATE);
		PatientGroup pnum = new BasicPatientGroup(null,null,null,s_sizeNum);
		PatientGroup pden = new BasicPatientGroup(null,null,null,s_sizeDen);
		d_numerator = new BasicRateMeasurement(e, s_effectNum, pnum);		
		d_denominator = new BasicRateMeasurement(e, s_effectDen, pden);
		d_riskDif = new RiskDifference(d_denominator, d_numerator);
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(new Endpoint("E", Type.RATE), d_riskDif.getEndpoint());
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(s_sizeNum + s_sizeDen, (int)d_riskDif.getSampleSize());
	}
	
	@Test
	public void testGetRatio() {
		assertEquals(RiskDifference(),	(double)d_riskDif.getRatio(), 0.00001);
	}
	
	@Test
	public void testGetCI() {
		double helper1 = (double)s_effectNum * (double)(s_sizeNum - s_effectNum) / Math.pow(s_sizeNum, 3);
		double helper2 = (double)s_effectDen * (double)(s_sizeDen - s_effectDen) / Math.pow(s_sizeDen, 3);
		double expectedSE = Math.sqrt(helper1 + helper2);		
		double t = StudentTTable.getT(s_sizeNum + s_sizeDen - 2);
		
		double lower = RiskDifference() - t * expectedSE;
		double upper = RiskDifference() + t * expectedSE;
		
		Interval<Double> ci = d_riskDif.getConfidenceInterval();
		assertEquals(lower, ci.getLowerBound(), 0.00001);
		assertEquals(upper, ci.getUpperBound(), 0.00001);
	}

	private double RiskDifference() {
		return (double)s_effectNum/(double)s_sizeNum 
				- (double)s_effectDen/(double)s_sizeDen;
	}
	
	private double square(double d) {
		return d * d;
	}

	@Ignore
	@Test
	//We do not need to specifically test for the SE as it does so already in testGetCI() because of symmetrical confidence interval
	public void testGetError() {
		double t = StudentTTable.getT(d_riskDif.getSampleSize() - 2);
		double g = square(t * s_stdDevDen / s_meanDen);
		double q = d_riskDif.getRatio();
		double sd = q / (1 - g) * Math.sqrt((1 - g) * square(s_stdDevNum) / square(s_meanNum) + 
				square(s_stdDevDen) / square(s_meanDen));
		
		assertEquals(sd, d_riskDif.getError(), 0.00001);
	}
}
