package org.drugis.addis.lyndobrien;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.DefaultXYDataset;
import org.junit.Test;

public class MockBenefitRiskDistr {
	private DefaultXYDataset d_data;
	private JFreeChart d_chart;
	
	public MockBenefitRiskDistr() throws IOException {
	}
	
	@Test
	public void TestChartOutput() throws IOException {
		d_chart = ChartFactory.createScatterPlot("test", "testX", "testY",
				d_data, PlotOrientation.HORIZONTAL, false, false, false);
		d_data = new DefaultXYDataset();
	
		d_data.addChangeListener(new DatasetChangeListener() {
			
			public void datasetChanged(DatasetChangeEvent arg0) {
				d_chart = ChartFactory.createScatterPlot("test", "testX", "testY",
						d_data, PlotOrientation.HORIZONTAL, false, false, false);

			}
		});
			
		double[][] samples = new double[2][3000];

		InputStream stream = MockBenefitRiskDistr.class.getResourceAsStream("samples.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
	
		int i = 0;
		while (reader.ready()) {
			String line = reader.readLine();
			StringTokenizer tok = new StringTokenizer(line, ",");
			tok.nextToken();
			Double b = Double.parseDouble(tok.nextToken());
			Double r = Double.parseDouble(tok.nextToken());
			samples[0][i] = b;
			samples[1][i] = r;
			++i;
		}
		
		d_data.addSeries("fnord", samples);
		
		ChartUtilities.saveChartAsPNG(new File("./test.png"), d_chart, 500, 500);
	}
	
}
