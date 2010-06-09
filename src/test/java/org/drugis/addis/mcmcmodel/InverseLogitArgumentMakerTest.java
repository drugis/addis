package org.drugis.addis.mcmcmodel;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class InverseLogitArgumentMakerTest {
	
	@Before
	public void setUp() {
		
	}
	
	@Test
	public void testGetArgumentFirstArray() {
		InverseLogitArgumentMaker am = new InverseLogitArgumentMaker(0);
		double[] inputs = new double[] { -2.3, 3.5, -0.1, 0.0, 8.4, 3.5 };
		double[] expected = new double[inputs.length];
		for (int i = 0; i < inputs.length; ++i) {
			expected[i] = MathUtil.ilogit(inputs[i]);
		}
		double[] actual = am.getArgument(new double[][] { inputs });
		for (int i = 0; i < inputs.length; ++i) {
			assertEquals(expected[i], actual[i], 0.0000001);
		}
	}
	
	@Test
	public void testGetArgumentOtherArray() {
		double expected = MathUtil.ilogit(2.0);
		
		assertEquals(expected, new InverseLogitArgumentMaker(2).getArgument(new double[][] { {0.0}, {4.0}, {2.0}, {3.0} })[0], 0.0000001);
	}
}
