package org.drugis.addis.mcmcmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.junit.Ignore;
import org.junit.Test;

public class BaselineMeanDifferenceModelIT {
	@Ignore
	@Test
	public void testModelResults() {
		// values calculated using equivalent JAGS model with 10k/20k iterations.
		double[] m = new double[] {-1.52, -2.1, -2.3, -0.69, -2.5};
		double[] s = new double[] {1.18, 0.1, 1.4, 0.16, 1.6};
		int[] n = new int[] {30, 86, 178, 102, 177};
		double expectedMu = -1.853143;
		double expectedSigma = 1.428731;
		double dev = expectedSigma * 0.05;
		
		BaselineMeanDifferenceModel model = new BaselineMeanDifferenceModel(buildMeasurementsList(m, s, n));
		model.run();
		
		assertTrue(model.isReady());
		assertEquals(expectedMu, model.getResult().getMu(), dev);
		assertEquals(expectedSigma, model.getResult().getSigma(), dev);
	}

	private static List<ContinuousMeasurement> buildMeasurementsList(double[] m, double[] s, int[] n) {
		assert(n.length == m.length);
		assert(n.length == s.length);
		List<ContinuousMeasurement> result = new ArrayList<ContinuousMeasurement>(n.length);
		for (int i = 0; i < n.length; ++i) {
			result.add(new BasicContinuousMeasurement(m[i], s[i], n[i]));
		}
		return result;
	}
}
