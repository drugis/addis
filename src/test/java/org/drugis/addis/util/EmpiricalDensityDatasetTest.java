package org.drugis.addis.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.drugis.mtc.BasicParameter;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.util.FileResults;
import org.drugis.mtc.yadas.RandomEffectsVariance;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EmpiricalDensityDatasetTest {
	private static final double EPSILON = 0.0000001;
	private Parameter[] d_parameters;
	private FileResults d_results;
	
	private static final int[] s_densities1 = {54, 73, 82, 87,  93, 110, 140, 155, 177, 183, 238, 231, 250,
			254, 280, 300, 359, 371, 426, 401, 435, 440, 477, 504, 474, 450, 453, 478, 506, 475, 459,
			462, 411, 412, 382, 336, 328, 318, 359, 272, 270, 224, 200, 180, 126, 149, 161,  93,  83,  69};
	private static final double[] s_quantiles1 = { 0.1472530, 0.7713364 }; 

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
	}
	
	@Test
	public void testReadFiles() throws IOException {
		assertNotNull(readDensity("test.txt"));
	}
	
	@Test
	public void testCountLength() {
		d_results.makeSamplesAvailable();
		EmpiricalDensityDataset edd = new EmpiricalDensityDataset(d_results, d_parameters[0], 50);
		assertEquals(50, edd.getCounts().length);
	}
	
	@Test
	public void testCounts() {
		d_results.makeSamplesAvailable();
		EmpiricalDensityDataset edd = new EmpiricalDensityDataset(d_results, d_parameters[0], 50);
		assertArrayEquals(s_densities1, edd.getCounts());
	}

	@Test
	public void testDensities() throws IOException {
		d_results.makeSamplesAvailable();
		EmpiricalDensityDataset edd = new EmpiricalDensityDataset(d_results, d_parameters[0], 50);
		double[] normDensities = readDensity("test.txt");
		assertArrayEquals(normDensities, edd.getDensities(), EPSILON);
	}

	@Test
	public void testDensitiesDynamic() throws IOException {
		EmpiricalDensityDataset edd = new EmpiricalDensityDataset(d_results, d_parameters[0], 50);
		d_results.makeSamplesAvailable();
		double[] normDensities = readDensity("test.txt");
		assertArrayEquals(normDensities, edd.getDensities(), EPSILON);
	}
	
	@Test @Ignore
	public void testResultsEventShouldTriggerDatasetChanged() {
		// FIXME: test for dataset change events.
		fail();
	}
	
	@Test
	public void testGetX() throws IOException {
		d_results.makeSamplesAvailable();
		EmpiricalDensityDataset edd = new EmpiricalDensityDataset(d_results, d_parameters[0], 50);
		double bottom = s_quantiles1[0];
		double top = s_quantiles1[1];
		double interval = (top - bottom) / 50;
		
		assertEquals((0.5 + 1) * interval + bottom, edd.getX(0, 1), EPSILON);
		assertEquals((0.5 + 25) * interval + bottom, edd.getX(0, 25), EPSILON);
		assertEquals((0.5 + 49) * interval + bottom, edd.getX(0, 49), EPSILON);
	}
	
	private double[] readDensity(String file) throws IOException {
		/*
		 * Generated in R using:
			x <- read.table("conv-samples.txt", sep=",")
			chains <- sapply(0:2, function(i) { x[(5001 + i * 10000):((i+1)*10000),1] })
			y <- c(chains)
			q <- quantile(y, c(0.025, 0.975), type=6)
			nBins <- 50
			binSize <- (q[2] - q[1])/nBins
			breaks <- c(min(y), seq(from=q[1], to=q[2], by=binSize), max(y))
			dens <- hist(y, plot=F, breaks=breaks)$density[2:51]
			write.table(dens, "test.txt")
		 */
		
		InputStream is = EmpiricalDensityDatasetTest.class.getResourceAsStream(file);
		double[] data = new double[50];		
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		reader.readLine(); // skip the first line
		for (int i = 0; reader.ready(); ++i) {
			String line = reader.readLine();
			StringTokenizer tok = new StringTokenizer(line, " ");
			tok.nextToken(); // skip the first column (IDs)
			data[i] = Double.parseDouble(tok.nextToken());
		}
		return data;
	}
}
