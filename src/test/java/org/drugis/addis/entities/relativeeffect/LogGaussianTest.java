package org.drugis.addis.entities.relativeeffect;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class LogGaussianTest {

		private LogGaussian d_gauss1;
		private LogGaussian d_gauss2;

		@Before public void setUp() {
			d_gauss1 = new LogGaussian(0.0, 1.0);
			d_gauss2 = new LogGaussian(-5.0, 2.0);
		}
		
		@Test public void testGetAxisType() {
			assertEquals(AxisType.LOGARITHMIC, d_gauss1.getAxisType());
		}
		
		@Test public void testGetParameters() {
			assertEquals(0.0, d_gauss1.getMu(), 0.00000001);
			assertEquals(1.0, d_gauss1.getSigma(), 0.00000001);
			assertEquals(-5.0, d_gauss2.getMu(), 0.00000001);
			assertEquals(2.0, d_gauss2.getSigma(), 0.00000001);
		}
		
		@Test public void testGetQuantile() {
			double z90 = 1.644853626951;
			double z95 = 1.959963984540;
			assertEquals(Math.exp(z95 * 1.0 + 0.0), d_gauss1.getQuantile(0.975), 0.00001);
			assertEquals(Math.exp(z95 * 2.0 + -5.0), d_gauss2.getQuantile(0.975), 0.00001);
			assertEquals(Math.exp(-z95 * 1.0 + 0.0), d_gauss1.getQuantile(0.025), 0.00001);
			assertEquals(Math.exp(-z95 * 2.0 + -5.0), d_gauss2.getQuantile(0.025), 0.00001);
			assertEquals(Math.exp(z90 * 1.0 + 0.0), d_gauss1.getQuantile(0.95), 0.00001);
			assertEquals(Math.exp(z90 * 2.0 + -5.0), d_gauss2.getQuantile(0.95), 0.00001);
			assertEquals(Math.exp(0.0), d_gauss1.getQuantile(0.5), 0.00001);
			assertEquals(Math.exp(-5.0), d_gauss2.getQuantile(0.5), 0.00001);
		}
	
		@Test(expected=IllegalArgumentException.class) public void testPreconditionSigmaNonNegative() {
			new LogGaussian(0.0, -.01);
		}

		@Test(expected=IllegalArgumentException.class) public void testPreconditionSigmaNotNaN() {
			new LogGaussian(0.0, Double.NaN);
		}

		@Test(expected=IllegalArgumentException.class) public void testPreconditionMuNotNaN() {
			new LogGaussian(Double.NaN, 1.0);
		}
}
