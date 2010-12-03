package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;

import org.drugis.addis.util.EmpiricalDensityDatasetTest;
import org.drugis.common.JUnitUtil;
import org.drugis.mtc.BasicParameter;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MCMCResultsListener;
import org.drugis.mtc.Parameter;
import org.drugis.mtc.Treatment;
import org.drugis.mtc.util.FileResults;
import org.drugis.mtc.yadas.RandomEffectsVariance;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class MCMCResultsMemoryUsageModelTest {
	private static final int AVAILABLE_SAMPLES = 10000;
	private Parameter[] d_parameters;
	private FileResults d_results;
	
	
	public class FakeResults implements MCMCResults {
		private int d_nChains;
		private int d_nSamples;
		private int d_nParameters;

		public FakeResults(int nChains, int nSamples, int nParameters) {
			d_nChains = nChains;
			d_nSamples = nSamples;
			d_nParameters = nParameters;
		}
		public void addResultsListener(MCMCResultsListener l) {}
		public void removeResultsListener(MCMCResultsListener l) {}
		public int findParameter(Parameter p) { return 0; }
		public int getNumberOfChains() { return d_nChains; }
		public int getNumberOfSamples() { return d_nSamples; }
		public Parameter[] getParameters() { return new Parameter[d_nParameters]; }
		public double getSample(int p, int c, int i) { return 0; }
		public double[] getSamples(int p, int c) { return null; }
	}

	@Before
	public void setUp() throws IOException {
		Treatment t1 = new Treatment("iPCI");
		Treatment t2 = new Treatment("mPCI");
		Treatment t3 = new Treatment("sPCI");
		d_parameters = new Parameter[] {
				new BasicParameter(t1, t2), new BasicParameter(t2, t3), new RandomEffectsVariance()	
		};
		d_results = readSamples();
	}
	
	@Test
	public void sanityCheck() {
		assertEquals(64, Double.SIZE);
	}
	
	@Test
	public void testInitialValueEmpty() {
		MCMCResultsMemoryUsageModel model = new MCMCResultsMemoryUsageModel(d_results);
		assertEquals("0.0 KB", model.getValue());
	}
	
	@Test
	public void testInitialValueFull() {
		d_results.makeSamplesAvailable();
		MCMCResultsMemoryUsageModel model = new MCMCResultsMemoryUsageModel(d_results);

		assertEquals("0.7 MB", model.getValue());
	}
	
	@Test
	public void testInitialValueOther() {
		FakeResults results = new FakeResults(3, 100, 3);
		MCMCResultsMemoryUsageModel model = new MCMCResultsMemoryUsageModel(results);
		assertEquals("7.2 KB", model.getValue());
	}
	
	@Test
	public void testInitialValueLarge() {
		MCMCResults results = new FakeResults(3, 100 * 1000, 3);
		MCMCResultsMemoryUsageModel model = new MCMCResultsMemoryUsageModel(results);
		assertEquals("7.2 MB", model.getValue());
	}
	
	@Test
	public void testInitialValueVeryLarge() {
		MCMCResults results = new FakeResults(3, 1000 * 1000 * 10, 3);
		MCMCResultsMemoryUsageModel model = new MCMCResultsMemoryUsageModel(results);
		assertEquals("720.0 MB", model.getValue());
	}
	
	@Test
	public void testInitialValueTiny() {
		MCMCResults results = new FakeResults(3, 1, 3);
		MCMCResultsMemoryUsageModel model = new MCMCResultsMemoryUsageModel(results);
		assertEquals("0.1 KB", model.getValue());
	}
	
	@Test
	public void testValueUpdate() {
		MCMCResultsMemoryUsageModel model = new MCMCResultsMemoryUsageModel(d_results);
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(model, "value", null, "0.7 MB");
		model.addValueChangeListener(listener);
		
		d_results.makeSamplesAvailable();
		assertEquals("0.7 MB", model.getValue());
		EasyMock.verify(listener);
	}
	
	private FileResults readSamples() throws IOException {
		InputStream is = EmpiricalDensityDatasetTest.class.getResourceAsStream("conv-samples.txt");
		FileResults results = new FileResults(is, d_parameters, 3, AVAILABLE_SAMPLES);
		is.close();
		return results;
	}
}
