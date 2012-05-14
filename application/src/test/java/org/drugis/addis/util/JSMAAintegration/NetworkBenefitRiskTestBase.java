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

package org.drugis.addis.util.JSMAAintegration;

import static org.junit.Assert.assertEquals;
import junit.framework.AssertionFailedError;
import fi.smaa.jsmaa.model.Alternative;
import fi.smaa.jsmaa.model.Criterion;
import fi.smaa.jsmaa.model.SMAAModel;
import fi.smaa.jsmaa.simulator.SMAA2Simulation;

public class NetworkBenefitRiskTestBase {

	private static final double[][] EXPECTED_RA = {
			{ 0.0215, 0.2648, 0.3217, 0.3143, 0.0777 },
			{ 0.1496, 0.3248, 0.2519, 0.2249, 0.0488 },
			{ 0.6114, 0.1201, 0.0868, 0.0545, 0.1272 },
			{ 0.0758, 0.1342, 0.1931, 0.2389, 0.3580 },
			{ 0.1417, 0.1561, 0.1465, 0.1674, 0.3883 }};
	private static final double[][] EXPECTED_CW = {
			{ 0.08646901, 0.24112709, 0.22045918, 0.1362404, 0.10229834, 0.21340600 },
			{ 0.17593187, 0.12181948, 0.29181468, 0.1293235, 0.13949573, 0.14161473 },
			{ 0.17986517, 0.17659262, 0.09069475, 0.1618931, 0.19437357, 0.19658082 },
			{ 0.07495453, 0.31162260, 0.27471687, 0.1266365, 0.09423491, 0.11783455 },
			{ 0.18030730, 0.07638429, 0.27879293, 0.2584503, 0.11542176, 0.09064338 }};
	private static final double[] EXPECTED_CF = {
			0.1144, 0.5532, 0.9870, 0.5538, 0.6522
		};
	private static final double EPSILON_CW = 0.03;
	private static final double EPSILON_RA = 0.02;

	public NetworkBenefitRiskTestBase() {
		super();
	}

	protected void checkResults(SMAAModel model, SMAA2Simulation simulation, double slack) {
		assertEquals("Fluoxetine", model.getAlternatives().get(0).getName());
		assertEquals("Placebo", model.getAlternatives().get(2).getName());
		assertEquals("Diarrhea", model.getCriteria().get(0).getName());
		assertEquals("HAM-D Responders", model.getCriteria().get(2).getName());
		
		// verify CWs & CFs
		for (int i = 0; i < model.getAlternatives().size(); ++i) {
			Alternative alt = model.getAlternatives().get(i);
			for (int j = 0; j < model.getCriteria().size(); ++j) {
				Criterion crit = model.getCriteria().get(j);
				final double actual = simulation.getResults().getCentralWeightVectors().get(alt).get(crit);
				final double expected = EXPECTED_CW[i][j];
				if (Math.abs(actual - expected) > EPSILON_CW * slack) {
					throw new AssertionFailedError(
							"Central weight for " + alt + " differs on " + crit +
							": expected <" + expected + ">, actual <" + actual + ">");
				}
			}
			final double actual = simulation.getResults().getConfidenceFactors().get(alt);
			final double expected = EXPECTED_CF[i];
			if (Math.abs(actual - expected) > EPSILON_CW * slack) {
				throw new AssertionFailedError(
						"Confidence factor for " + alt + " differs: expected <" +
						expected + ">, actual <" + actual + ">");
			}
		}
		
		// verify RAs
		for (int i = 0; i < model.getAlternatives().size(); ++i) {
			for (int r = 0; r < model.getAlternatives().size(); ++r) {
				Alternative alt = model.getAlternatives().get(i);
				final double actual = simulation.getResults().getRankAcceptabilities().get(alt).get(r);
				final double expected = EXPECTED_RA[i][r];
				if (Math.abs(actual - expected) > EPSILON_RA * slack) {
					throw new AssertionFailedError(
							"Rank probability for " + alt + " differs on " + (r + 1) +
							": expected <" + expected + ">, actual <" + actual + ">");
				}
			}
		}
	}

}