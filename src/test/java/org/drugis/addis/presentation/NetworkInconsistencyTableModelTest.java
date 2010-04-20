package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;


import mocks.MockNetworkMetaAnalysis;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.InconsistencyParameter;
import org.junit.Before;
import org.junit.Test;

public class NetworkInconsistencyTableModelTest {

	private PresentationModelFactory d_pmf;
	private NetworkInconsistencyFactorsTableModel d_tableModel;
	private NetworkMetaAnalysis d_analysis;
	
	@Before
	public void setUp() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_analysis = NetworkTableModelTest.buildMockNetworkMetaAnalysis();
		d_pmf = new PresentationModelFactory(domain);
		
		d_tableModel = new NetworkInconsistencyFactorsTableModel((NetworkMetaAnalysisPresentation) d_pmf.getModel(d_analysis), d_pmf);
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
					assertEquals("Fluoxetine, Sertraline, Paroxetine", d_tableModel.getValueAt(y, x));
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
	
	@Test
	public void testContinuousValueAt() {
		NetworkMetaAnalysis d_contAnalysis = buildMockContinuousNetworkMetaAnalysis();
		NetworkInconsistencyFactorsTableModel d_contTableModel = new NetworkInconsistencyFactorsTableModel((NetworkMetaAnalysisPresentation) d_pmf.getModel(d_contAnalysis), d_pmf);
		d_contAnalysis.getInconsistencyModel().run();
		for(int x = 0; x < d_contTableModel.getColumnCount(); ++x) {
			for(int y = 0; y < d_contTableModel.getRowCount(); ++y) {
				InconsistencyParameter ip = d_contAnalysis.getInconsistencyModel().getInconsistencyFactors().get(y);
				if(x == 0){
					System.out.println("inconsistency cycle: "+d_contTableModel.getValueAt(y, x));
					assertEquals("Fluoxetine, Sertraline, Paroxetine", d_contTableModel.getValueAt(y, x));
				} else if (x == 1){
					Estimate icModel = d_contAnalysis.getInconsistencyModel().getInconsistency(ip);
					
					BasicContinuousMeasurement contMeas = new BasicContinuousMeasurement(icModel.getMean(), icModel.getStandardDeviation(), 0);
					ContinuousMeasurementPresentation<BasicContinuousMeasurement> pm = 
										(ContinuousMeasurementPresentation<BasicContinuousMeasurement>) d_pmf.getModel(contMeas);
					assertEquals(pm.normConfIntervalString(), d_contTableModel.getValueAt(y, x));
				}
			}
		}	
	}
	
	private NetworkMetaAnalysis buildMockContinuousNetworkMetaAnalysis() {
		List<Study> studies = Arrays.asList(new Study[] {
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard()});
		List<Drug> drugs = Arrays.asList(new Drug[] {ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine(), 
				ExampleData.buildDrugSertraline()});
		
		NetworkMetaAnalysis analysis = new MockNetworkMetaAnalysis("Test Network", 
				ExampleData.buildIndicationDepression(), ExampleData.buildEndpointCgi(),
				studies, drugs, ExampleData.buildMap(studies, drugs));
		
		return analysis;
	}
}