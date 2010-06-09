package org.drugis.addis.mcmcmodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.RateMeasurement;
import org.junit.Test;

public class BaselineOddsModelIT {
	@Test
	public void testModelResults() {
		// values calculated using equivalent JAGS model with 10k/20k iterations.
		int[] n = {47, 144, 120, 101, 73, 161, 54, 92, 45, 119, 103, 52, 120, 121, 170};
		int[] r = {30, 63, 61, 67, 27, 95, 31, 57, 27, 84, 51, 9, 76, 77, 58};
		double expectedMu = 0.1320994;
		double expectedSigma = 0.1779923;
		double dev = expectedSigma * 0.05;
		
		BaselineOddsModel model = new BaselineOddsModel(buildMeasurementsList(n, r));
		model.run();
		
		assertTrue(model.isReady());
		assertEquals(expectedMu, model.getResult().getMu(), dev);
		assertEquals(expectedSigma, model.getResult().getSigma(), dev);
	}

	private static List<RateMeasurement> buildMeasurementsList(int[] n, int[] r) {
		assert(n.length == r.length);
		List<RateMeasurement> result = new ArrayList<RateMeasurement>(n.length);
		for (int i = 0; i < n.length; ++i) {
			result.add(new BasicRateMeasurement(r[i], n[i]));
		}
		return result;
	}
}
