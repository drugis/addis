package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import mocks.MockNetworkMetaAnalysis;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.ContinuousMeasurementEstimate;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.LogContinuousMeasurementEstimate;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.NetworkMetaAnalysis;
import org.drugis.mtc.Estimate;
import org.drugis.mtc.Treatment;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class NetworkTableModelTest {

	
	private PresentationModelFactory d_pmf;
	private NetworkTableModel d_tableModel;
	private NetworkMetaAnalysis d_analysis;
	private NetworkMetaAnalysis d_contAnalysis;
	private NetworkTableModel d_contTableModel;

	@Before
	public void setUp() {
		DomainImpl domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		d_analysis = buildMockNetworkMetaAnalysis();
		d_pmf = new PresentationModelFactory(domain);
		d_tableModel = new NetworkTableModel((NetworkMetaAnalysisPresentation)d_pmf.getModel(d_analysis), d_pmf, d_analysis.getInconsistencyModel());
	}
	
	@Test
	public void testGetColumnCount() {
		assertEquals(d_analysis.getIncludedDrugs().size(), d_tableModel.getColumnCount());
	}

	@Test
	public void testGetRowCount() {
		assertEquals(d_analysis.getIncludedDrugs().size(), d_tableModel.getRowCount());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testValueAt() {
		for(int x = 0; x < d_tableModel.getColumnCount(); ++x) {
			for(int y = 0; y < d_tableModel.getRowCount(); ++y) {
				if(x == y){
					assertEquals(d_analysis.getIncludedDrugs().get(x), ((PresentationModel<Drug>) d_tableModel.getValueAt(x, y)).getBean());
					assertEquals(null, d_tableModel.getDescriptionAt(x, y));
				} else {
					assertEquals("n/a", ((PresentationModel<LogContinuousMeasurementEstimate>) d_tableModel.getValueAt(x, y)).getBean().toString());
					String expected = "\""+d_analysis.getIncludedDrugs().get(y)+"\" relative to \""+d_analysis.getIncludedDrugs().get(x)+"\"";
					assertEquals(expected, d_tableModel.getDescriptionAt(x, y));
				}
			}
		}	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueAtAfterModelRun() {
		d_analysis.getInconsistencyModel().run();

		for(int x = 0; x < d_analysis.getIncludedDrugs().size(); ++x){
			for(int y = 0; y < d_analysis.getIncludedDrugs().size(); ++y){
				if(x == y){
					assertEquals(d_analysis.getIncludedDrugs().get(x), ((PresentationModel<Drug>) d_tableModel.getValueAt(x, y)).getBean());
				} else {
					Treatment t1 = d_analysis.getBuilder().getTreatment(d_analysis.getIncludedDrugs().get(x).getName());
					Treatment t2 = d_analysis.getBuilder().getTreatment(d_analysis.getIncludedDrugs().get(y).getName());
					Estimate relEffect = d_analysis.getInconsistencyModel().getRelativeEffect(t1, t2);
					assertEquals(new LogContinuousMeasurementEstimate(relEffect.getMean(), relEffect.getStandardDeviation()).toString(), ((PresentationModel<LogContinuousMeasurementEstimate>) d_tableModel.getValueAt(x, y)).getBean().toString());
				}
			}
		}	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetValueContinuousModelRun() {
		d_contAnalysis = buildMockContinuousNetworkMetaAnalysis();
		d_contTableModel = new NetworkTableModel((NetworkMetaAnalysisPresentation)d_pmf.getModel(d_contAnalysis), d_pmf, d_contAnalysis.getInconsistencyModel());
		
		d_contAnalysis.getInconsistencyModel().run();

		for(int x = 0; x < d_contAnalysis.getIncludedDrugs().size(); ++x){
			for(int y = 0; y < d_contAnalysis.getIncludedDrugs().size(); ++y){
				if(x == y){
					assertEquals(d_contAnalysis.getIncludedDrugs().get(x), ((PresentationModel<Drug>) d_contTableModel.getValueAt(x, y)).getBean());
				} else {
					Treatment t1 = d_contAnalysis.getBuilder().getTreatment(d_contAnalysis.getIncludedDrugs().get(x).getName());
					Treatment t2 = d_contAnalysis.getBuilder().getTreatment(d_contAnalysis.getIncludedDrugs().get(y).getName());
					Estimate relEffect = d_contAnalysis.getInconsistencyModel().getRelativeEffect(t1, t2);
					assertEquals(new ContinuousMeasurementEstimate(relEffect.getMean(), relEffect.getStandardDeviation()).toString(), ((PresentationModel<ContinuousMeasurementEstimate>) d_contTableModel.getValueAt(x, y)).getBean().toString());
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

	public static NetworkMetaAnalysis buildMockNetworkMetaAnalysis() {
		List<Study> studies = Arrays.asList(new Study[] {
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard(), ExampleData.buildStudyDeWilde(), ExampleData.buildStudyFava2002()});
		List<Drug> drugs = Arrays.asList(new Drug[] {ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine(), 
				ExampleData.buildDrugSertraline()});
		
		NetworkMetaAnalysis analysis = new MockNetworkMetaAnalysis("Test Network", 
				ExampleData.buildIndicationDepression(), ExampleData.buildEndpointHamd(),
				studies, drugs, ExampleData.buildMap(studies, drugs));
		
		return analysis;
	}
}