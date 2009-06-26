package nl.rug.escher.addis.entities.test;

import static org.junit.Assert.assertEquals;
import nl.rug.escher.addis.entities.BasicRateMeasurement;
import nl.rug.escher.addis.entities.Endpoint;
import nl.rug.escher.addis.entities.LogOddsRatio;
import nl.rug.escher.addis.entities.OddsRatio;
import nl.rug.escher.addis.entities.Endpoint.Type;

import org.junit.Before;
import org.junit.Test;

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
		Endpoint e = new Endpoint("E", Type.RATE);
		
		d_numerator = new BasicRateMeasurement(e, s_effectNum, s_sizeNum);
		
		d_denominator = new BasicRateMeasurement(e, s_effectDen, s_sizeDen);
		
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