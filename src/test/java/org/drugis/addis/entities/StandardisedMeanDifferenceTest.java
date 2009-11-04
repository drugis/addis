package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.Interval;
import org.drugis.common.StudentTTable;
import org.junit.Before;
import org.junit.Test;

public class StandardisedMeanDifferenceTest {
	private static final double s_subjMean = 0.2342;
	private static final double s_baselMean = 4.7811;
	private static final double s_subjStdDev = 0.2;
	private static final double s_baslStdDev = 2.5;
	private static final int s_subjSize = 35;
	private static final int s_baslSize = 41;
	int d_sampleSize = s_subjSize + s_baslSize;
	
	private StandardisedMeanDifference d_smd;
	private BasicContinuousMeasurement d_subject;
	private BasicContinuousMeasurement d_baseline;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E", Type.CONTINUOUS);
		PatientGroup subjs = new BasicPatientGroup(null, null, null, s_subjSize);
		PatientGroup basels = new BasicPatientGroup(null, null, null, s_baslSize);
		d_subject = new BasicContinuousMeasurement(e, s_subjMean, s_subjStdDev, subjs);
		d_baseline = new BasicContinuousMeasurement(e, s_baselMean, s_baslStdDev, basels);
		d_smd = new StandardisedMeanDifference(d_baseline, d_subject);
	}
	
	@Test
	public void testGetMean() {
		double expected = getSMD();
		assertEquals(expected, d_smd.getRatio(),0.0001);
	}

	@Test
	public void testGetError() {
		double firstFactor = d_sampleSize / (s_subjSize * s_baslSize);
		double secondFactor = square(getSMD()) / (2 * (d_sampleSize - 3.94));
		double expected = Math.sqrt(firstFactor + secondFactor);
		assertEquals(expected, d_smd.getError(), 0.01);
	}
	
	@Test
	public void testGetCI() {
		double t = StudentTTable.getT(d_sampleSize - 2);
		double upper = d_smd.getRatio() + d_smd.getError() * t;
		double lower = d_smd.getRatio() - d_smd.getError() * t;
		Interval<Double> interval = d_smd.getConfidenceInterval();
		assertEquals(upper, interval.getUpperBound(),0.0001);
		assertEquals(lower, interval.getLowerBound(),0.0001);
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(d_subject.getEndpoint(),d_smd.getEndpoint());
		assertEquals(d_baseline.getEndpoint(), d_smd.getEndpoint());
	}
	
	@Test
	public void testGetCohend() {
		double expected = (s_subjMean - s_baselMean)/getPooledStdDev();
		assertEquals(expected, d_smd.getCohenD(), 0.0001);
	}
	
	@Test
	public void testGetCohenVariance() {
		double expected = d_sampleSize/(s_subjSize*s_baslSize) + square(d_smd.getCohenD())/(2*d_sampleSize);
		assertEquals(expected, d_smd.getCohenVariance(), 0.0001);
	}
	
	@Test
	public void testGetCorrectionJ() {
		double expected = 1 - (3 / (4*(d_sampleSize - 2) - 1));
		assertEquals(expected, d_smd.getCorrectionJ(), 0.0001);
	}
	
	private double square(double x) {
		return x*x;
	}
	
	private double getSMD() {
		double pooledStdDev = getPooledStdDev();
		
		double firstFactor = (s_subjMean - s_baselMean) / pooledStdDev;
		
		double secondFactor = 1 - (3 / (4 * d_sampleSize - 9));
		double expected = firstFactor * secondFactor;
		return expected;
	}

	private double getPooledStdDev() {
		double numerator = (s_subjSize - 1) * square(s_subjStdDev) + (s_baslSize - 1) * square(s_baslStdDev);
		double pooledStdDev = Math.sqrt(numerator / (s_subjSize + s_baslSize - 2));
		return pooledStdDev;
	}
}
