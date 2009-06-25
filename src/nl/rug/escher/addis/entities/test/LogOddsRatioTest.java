package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import nl.rug.escher.addis.entities.BasicPatientGroup;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.BasicStudy;
import nl.rug.escher.addis.entities.Dose;
import nl.rug.escher.addis.entities.Drug;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.LogOddsRatio;
import nl.rug.escher.addis.entities.OddsRatio;
import nl.rug.escher.addis.entities.SIUnit;

public class LogOddsRatioTest {
	private static final int s_sizeNum = 142;
	private static final int s_sizeDen = 144;
	private static final int s_effectNum = 73;
	private static final int s_effectDen = 63;
	private static final int s_nonNum = s_sizeNum - s_effectNum;
	private static final int s_nonDen = s_sizeDen - s_effectDen; 
	
	BasicRateMeasurement d_numerator;
	BasicRateMeasurement d_denominator;
	LogOddsRatio d_ratio;
	
	@Before
	public void setUp() {
		Endpoint e = new Endpoint("E");
		
		BasicPatientGroup g1 = new BasicPatientGroup(new BasicStudy("X"), new Drug("D"), new Dose(8.8, SIUnit.MILLIGRAMS_A_DAY), s_sizeNum);
		d_numerator = new BasicRateMeasurement(e);
		d_numerator.setRate(s_effectNum);
		g1.addMeasurement(d_numerator);
		
		BasicPatientGroup g2 = new BasicPatientGroup(new BasicStudy("X"), new Drug("F"), new Dose(8.8, SIUnit.MILLIGRAMS_A_DAY), s_sizeDen);
		d_denominator = new BasicRateMeasurement(e);
		d_denominator.setRate(s_effectDen);
		g2.addMeasurement(d_denominator);
		
		d_ratio = new LogOddsRatio(d_denominator, d_numerator);
	}
	
	@Test
	public void testMean() {
		OddsRatio ratio = new OddsRatio(d_denominator, d_numerator);
		assertEquals(Math.log(ratio.getRatio()), d_ratio.getMean(), 0.00001);
	}
	
	@Test
	public void testStdDev() {
		assertEquals(Math.sqrt(1.0 / s_effectNum + 1.0 / s_effectDen + 1.0 / s_nonNum + 1.0 / s_nonDen),
				d_ratio.getStdDev(), 0.00001);
	}
	
	@Test
	public void testGetLabel() {
		assertEquals(0.31 + "\u00B1" + 0.24, d_ratio.getLabel());
	}
}