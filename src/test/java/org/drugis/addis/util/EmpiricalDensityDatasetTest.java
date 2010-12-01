package org.drugis.addis.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.commons.math.stat.descriptive.summary.Sum;
import org.drugis.mtc.BasicParameter;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.util.FileResults;
import org.drugis.mtc.yadas.RandomEffectsVariance;
import org.junit.Before;
import org.junit.Test;

public class EmpiricalDensityDatasetTest {
	private static final double EPSILON = 0.0000001;
	private Parameter[] d_parameters;
	private FileResults d_results;
	
	private double[] s_densities1 = {54, 73, 82, 87,  93, 110, 140, 155, 177, 183, 238, 231, 250,
			254, 280, 300, 359, 371, 426, 401, 435, 440, 477, 504, 474, 450, 453, 478, 506, 475, 459,
			462, 411, 412, 382, 336, 328, 318, 359, 272, 270, 224, 200, 180, 126, 149, 161,  93,  83,  69};

	@Before
	public void setUp() throws IOException {
		InputStream is = EmpiricalDensityDatasetTest.class.getResourceAsStream("conv-samples.txt");
		Treatment t1 = new Treatment("iPCI");
		Treatment t2 = new Treatment("mPCI");
		Treatment t3 = new Treatment("sPCI");
		d_parameters = new Parameter[] {
				new BasicParameter(t1, t2), new BasicParameter(t2, t3), new RandomEffectsVariance()	
		};
		d_results = new FileResults(is, d_parameters, 3, 10000);
		d_results.makeSamplesAvailable();
	}
	
	@Test
	public void testDensityLength() {
		EmpiricalDensityDataset edd = new EmpiricalDensityDataset(d_results, d_parameters[0], 50);
		assertEquals(50, edd.getDensities().length);
	}
	
	@Test
	public void testDensitySum() {
		Sum sum = new Sum();
		EmpiricalDensityDataset edd = new EmpiricalDensityDataset(d_results, d_parameters[0], 50);
		assertEquals(sum.evaluate(s_densities1), sum.evaluate(edd.getDensities()), EPSILON);
	}
	
	@Test
	public void testDensities() {
		EmpiricalDensityDataset edd = new EmpiricalDensityDataset(d_results, d_parameters[0], 50);
		assertArrayEquals(s_densities1, edd.getDensities(), EPSILON);
	}

	@Test
	public void testNormalisedDensities() throws IOException {
		EmpiricalDensityDataset edd = new EmpiricalDensityDataset(d_results, d_parameters[0], 50);
		double[] normDensities = readDensity("test.txt");
		assertArrayEquals(normDensities, edd.getNormDensities(), EPSILON);
	}
	
	
	@Test
	public void testReadFiles() throws IOException {
		assertNotNull(readDensity("test.txt"));
	}
	
	private double[] readDensity(String file) throws IOException {
		InputStream is = EmpiricalDensityDatasetTest.class.getResourceAsStream(file);
		double[] data = new double[50];		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		for (int i = 0; reader.ready(); ++i) {
			String line = reader.readLine();
			StringTokenizer tok = new StringTokenizer(line, " ");
			tok.nextToken(); // skip the first column (IDs)
			data[i] = Double.parseDouble(tok.nextToken());
		}
		return data;
	}
}
