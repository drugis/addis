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
}
