package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.beans.PropertyChangeListener;

import nl.rug.escher.addis.entities.BasicContinuousMeasurement;
import nl.rug.escher.addis.entities.ContinuousMeasurement;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.OddsRatio;
import nl.rug.escher.addis.entities.PatientGroup;
import nl.rug.escher.addis.entities.SIUnit;
import nl.rug.escher.addis.entities.Study;
import nl.rug.escher.common.Interval;
import nl.rug.escher.common.JUnitUtil;
import nl.rug.escher.common.StudentTTable;

import org.junit.Before;
import org.junit.Test;

public class OddsRatioTest {
	private static final int s_sizeNum = 142;
	private static final int s_sizeDen = 144;
	private static final double s_meanNum = 73.0 / s_sizeNum; 
	private static final double s_meanDen = 63.0 / s_sizeDen;
	private static final double s_stdDevNum = s_meanNum / Math.sqrt(s_sizeNum);
	private static final double s_stdDevDen = s_meanDen / Math.sqrt(s_sizeDen);
	
	BasicContinuousMeasurement d_numerator;
	BasicContinuousMeasurement d_denominator;
	OddsRatio d_ratio;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E");
		
		PatientGroup g1 = new PatientGroup(new Study("X"), new Drug("D"), new Dose(8.8, SIUnit.MILLIGRAMS_A_DAY), s_sizeNum);
		d_numerator = new BasicContinuousMeasurement(e, s_meanNum, s_stdDevNum);
		g1.addMeasurement(d_numerator);
		
		PatientGroup g2 = new PatientGroup(new Study("X"), new Drug("F"), new Dose(8.8, SIUnit.MILLIGRAMS_A_DAY), s_sizeDen);
		d_denominator = new BasicContinuousMeasurement(e, s_meanDen, s_stdDevDen);
		g2.addMeasurement(d_denominator);
		
		d_ratio = new OddsRatio(d_denominator, d_numerator);
	}
	
	@Test
	public void testGetEndpoint() {
		assertEquals(new Endpoint("E"), d_ratio.getEndpoint());
	}
	
	@Test
	public void testGetSampleSize() {
		assertEquals(s_sizeNum + s_sizeDen, (int)d_ratio.getSampleSize());
	}
	
	@Test
	public void testGetMean() {
		assertEquals(s_meanNum / s_meanDen, (double)d_ratio.getMean(), 0.00001);
	}
	
	@Test
	public void testGetCI() {
		double t = StudentTTable.getT(d_ratio.getSampleSize() - 2);
		double g = square(t * s_stdDevDen / s_meanDen);
		double q = d_ratio.getMean();
		double sd = q / (1 - g) * Math.sqrt((1 - g) * square(s_stdDevNum) / square(s_meanNum) + 
				square(s_stdDevDen) / square(s_meanDen));
		double lower = q / (1 - g) - t * sd;
		double upper = q / (1 - g) + t * sd;
		
		Interval<Double> ci = d_ratio.getConfidenceInterval();
		assertEquals(lower, ci.getLowerBound(), 0.0001);
		assertEquals(upper, ci.getUpperBound(), 0.0001);
	}
	
	private double square(double d) {
		return d * d;
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("1.18 (0.93-1.49)", d_ratio.getLabel());
	}
	
	@Test
	public void testPropertyChangeEvents() {
		d_denominator.setMean(0.1);
		PropertyChangeListener l = 
			JUnitUtil.mockListener(d_ratio, OddsRatio.PROPERTY_LABEL, null, "1.18 (0.93-1.49)");
		d_ratio.addPropertyChangeListener(l);
		d_denominator.setMean(s_meanDen);
		verify(l);
		d_ratio.removePropertyChangeListener(l);
		
		d_numerator.setMean(0.1);
		l = JUnitUtil.mockListener(d_ratio, OddsRatio.PROPERTY_LABEL, null, "1.18 (0.93-1.49)");
		d_ratio.addPropertyChangeListener(l);
		d_numerator.setMean(s_meanNum);
		verify(l);
	}
}