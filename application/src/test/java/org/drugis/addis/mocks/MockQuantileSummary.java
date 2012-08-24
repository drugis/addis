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

package org.drugis.addis.mocks;

import java.util.Random;

import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.QuantileSummary;

public class MockQuantileSummary extends QuantileSummary {
	private double[] d_quantiles;
	private MCMCResults d_results;
	private boolean d_defined = false;
	private static final Random RANDOM = new Random();

	public MockQuantileSummary(MCMCResults results, Parameter parameter) {
		super(results, parameter);
		d_results = results;
		createResults();
	}

	@Override
	public void resultsEvent(MCMCResultsEvent event) {
		createResults();
	}
	
	@Override
	public boolean getDefined() {
		 return d_defined;
	}
	
	private synchronized void createResults() {
		if (d_results.getNumberOfSamples() < 2) return;
		d_quantiles = new double[3];
		d_quantiles[1] = RANDOM.nextDouble() * 4 - 2;
		d_quantiles[2] = d_quantiles[1] + RANDOM.nextDouble() * 1.5;
		d_quantiles[0] = d_quantiles[1] - (d_quantiles[2] - d_quantiles[1]);
		d_defined = true;
		firePropertyChange(PROPERTY_DEFINED, null, d_defined);
	}
	
	public void fireChange() {
		firePropertyChange(PROPERTY_DEFINED, null, d_defined);
	}
	
	@Override
	public double getQuantile(int idx) {
		return d_quantiles[idx];
	}
	
}
