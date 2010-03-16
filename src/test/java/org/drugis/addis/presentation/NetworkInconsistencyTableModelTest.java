package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyParameter;
import org.junit.Before;
import org.junit.Test;

public class NetworkInconsistencyTableModelTest {

	private PresentationModelFactory d_pmf;
	private NetworkInconsistencyTableModel d_tableModel;
	private NetworkMetaAnalysis d_analysis;

	@Before
	public void setUp() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_analysis = ExampleData.buildNetworkMetaAnalysis();
		d_pmf = new PresentationModelFactory(domain);
		
		d_tableModel = new NetworkInconsistencyTableModel((NetworkMetaAnalysisPresentation) d_pmf.getModel(d_analysis), d_pmf);
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(2, d_tableModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_analysis.getInconsistencyFactors().size(), d_tableModel.getRowCount());
	}

	@Test
	public void testValueAt() {
		d_analysis.getInconsistencyModel().run();
		for(int x = 0; x < d_tableModel.getColumnCount(); ++x) {
			for(int y = 0; y < d_tableModel.getRowCount(); ++y) {
				InconsistencyParameter ip = d_analysis.getInconsistencyModel().getInconsistencyFactors().get(y);
				if(x == 0){
					// TODO: the order is not stable :s
					assertEquals("(Fluoxetine, Sertraline, Paroxetine)", d_tableModel.getValueAt(y, x));
				} else if (x == 1){
					Estimate icModel = d_analysis.getInconsistencyModel().getInconsistency(ip);
					
					BasicContinuousMeasurement contMeas = new BasicContinuousMeasurement(icModel.getMean(), icModel.getStandardDeviation(), 0);
					ContinuousMeasurementPresentation<BasicContinuousMeasurement> pm = 
										(ContinuousMeasurementPresentation<BasicContinuousMeasurement>) d_pmf.getModel(contMeas);
					assertEquals(pm.normConfIntervalString(), d_tableModel.getValueAt(y, x));
				}
			}
		}	
	}
}