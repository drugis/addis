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

package org.drugis.addis.mocks;

import java.util.Random;

import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.summary.NormalSummary;

public class MockNormalSummary extends NormalSummary {

	private double d_mean;
	private double d_stdDev;
	private MCMCResults d_results;
	private boolean d_defined = false;
	private static final Random RANDOM = new Random();

	public MockNormalSummary(MCMCResults results, Parameter parameter) {
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

	private void createResults() {
		if (d_results.getNumberOfSamples() < 2) return;
		d_mean = RANDOM.nextDouble() * 4 - 2;
		d_stdDev = RANDOM.nextDouble() * 1.5;
		d_defined = true;
		firePropertyChange(PROPERTY_MEAN, null, d_mean);
		firePropertyChange(PROPERTY_STANDARD_DEVIATION, null, d_stdDev);
	}
	
	public void fireChange() {
		firePropertyChange(PROPERTY_MEAN, null, d_mean);
	}
	
	@Override
	public double getMean() {
		return d_mean;
	}
	
	@Override
	public double getStandardDeviation() {
		return d_stdDev;
	}
}
