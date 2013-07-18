/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.mcmcmodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.common.threading.TaskUtil;
import org.drugis.mtc.MCMCModel.ExtendSimulation;
import org.junit.Test;

public class BaselineMeanDifferenceModelIT {

	@Test
	public void testModelResults() throws InterruptedException {
		// values calculated using equivalent JAGS model with 10k/20k iterations.
		double[] m = new double[] {-1.52, -2.1, -2.3, -0.69, -2.5};
		double[] s = new double[] {1.18, 0.1, 1.4, 0.16, 1.6};
		int[] n = new int[] {30, 86, 178, 102, 177};
		double expectedMu = -1.804124;
		double expectedSigma = 0.645427;
		double dev = expectedSigma * 0.075;

		BaselineMeanDifferenceModel model = new BaselineMeanDifferenceModel(buildMeasurementsList(m, s, n));
		model.setExtendSimulation(ExtendSimulation.FINISH);
		TaskUtil.run(model.getActivityTask());

		assertTrue(model.isReady());
		SummaryHelper.waitUntilDefined(model.getSummary());
		assertEquals(expectedMu, model.getSummary().getMean(), dev);
		assertEquals(expectedSigma, model.getSummary().getStandardDeviation(), dev * 2);
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
