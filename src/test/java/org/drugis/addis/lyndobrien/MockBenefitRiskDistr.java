/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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

package org.drugis.addis.lyndobrien;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.drugis.addis.entities.relativeeffect.AxisType;

public class MockBenefitRiskDistr implements BenefitRiskDistribution {
	private double[][] d_samples;
	private int d_currentSample = 0;

	public MockBenefitRiskDistr() throws IOException {
		d_samples = new double[2][3000];

		InputStream stream = MockBenefitRiskDistr.class.getResourceAsStream("samples.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	
		int i = 0;
		while (reader.ready()) {
			String line = reader.readLine();
			StringTokenizer tok = new StringTokenizer(line, ",");
			tok.nextToken();
			Double b = Double.parseDouble(tok.nextToken());
			Double r = Double.parseDouble(tok.nextToken());
			d_samples[0][i] = b;
			d_samples[1][i] = r;
			++i;
		}
	}

	public AxisType getBenefitAxisType() {
		return AxisType.LINEAR;
	}

	public AxisType getRiskAxisType() {
		return AxisType.LINEAR;
	}

	public Sample nextSample() {
		int idx = d_currentSample;
		++d_currentSample;
		return new Sample(d_samples[0][idx], d_samples[1][idx]); 
	}
	
	double[][] getSamples() {
		return d_samples;
	}

	public String getBenefitAxisName() {
		return "\"Benefit\"";
	}

	public String getRiskAxisName() {
		return "\"Risk\"";
	}
}
