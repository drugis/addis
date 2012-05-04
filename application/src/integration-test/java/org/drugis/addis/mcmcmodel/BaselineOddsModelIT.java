/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.common.threading.TaskUtil;
import org.junit.Test;

public class BaselineOddsModelIT {
	@Test
	public void testModelResults() throws InterruptedException {
		// values calculated using equivalent JAGS model with 10k/20k iterations.
		int[] n = {47, 144, 120, 101, 73, 161, 54, 92, 45, 119, 103, 52, 120, 121, 170};
		int[] r = {30, 63, 61, 67, 27, 95, 31, 57, 27, 84, 51, 9, 76, 77, 58};
		double expectedMu = 0.1320994;
		double expectedSigma = 0.1779923;
		double dev = expectedSigma * 0.05;
		
		BaselineOddsModel model = new BaselineOddsModel(buildMeasurementsList(n, r));
		TaskUtil.run(model.getActivityTask());
		
		assertTrue(model.isReady());
		SummaryHelper.waitUntilDefined(model.getSummary());
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
