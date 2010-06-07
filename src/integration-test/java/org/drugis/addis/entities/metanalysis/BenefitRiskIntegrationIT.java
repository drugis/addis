package org.drugis.addis.entities.metanalysis;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainManager;
import org.drugis.addis.entities.Measurement;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.NetworkMetaAnalysis;
import org.drugis.addis.entities.relativeeffect.ConfidenceInterval;
import org.drugis.addis.entities.relativeeffect.RelativeEffect;
import org.drugis.addis.presentation.BenefitRiskMeasurementTableModel;
import org.drugis.addis.presentation.BenefitRiskPM;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class BenefitRiskIntegrationIT {

	@Test
	public void testBRAnalysisContinuous() throws InterruptedException, FileNotFoundException, IOException, ClassNotFoundException {
		DomainManager domainmgr = new DomainManager();
		domainmgr.loadXMLDomain(new FileInputStream("src/main/xml/hansen_cgi_brAnalysis.xml"));
		Domain domain = domainmgr.getDomain();
		PresentationModelFactory pmf = new PresentationModelFactory(domain);
		
		// Run the br analysis.
		BenefitRiskAnalysis analysis = domain.getBenefitRiskAnalyses().first();
		analysis.runAllConsistencyModels();
		
		boolean modelsDone = false;
		while(!modelsDone) {
			boolean allDone = true;
			for (MetaAnalysis ma : analysis.getMetaAnalyses())
				if (ma instanceof NetworkMetaAnalysis)
					if (!(((NetworkMetaAnalysis) ma).getConsistencyModel()).isReady())
						allDone = false;
			modelsDone = allDone;
			Thread.sleep(100);
		}
		
		// check measurementsTable values
/*
 * The 'expected' values below are not guaranteed to be correct. This part of the test is intended to flag changes of the results.
 * However, since the results are non-deterministic the test might fail occasionally even if the algorithm hasn't changed.
 */
		BenefitRiskPM pm = (BenefitRiskPM) pmf.getModel(analysis);
		BenefitRiskMeasurementTableModel mtm = pm.getMeasurementTableModel(true);
		
		assertMeanwithinTenPercent(0.875, -1.032, 2.782, mtm.getValueAt(1, 1));
		assertMeanwithinTenPercent(0.838, 0.465, 1.509, mtm.getValueAt(1, 2));
	}
	
	@Test
	public void testBRAnalysisResults() throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException {
		DomainManager domainmgr = new DomainManager();
		domainmgr.loadXMLDomain(new FileInputStream("src/main/xml/zhao_brAnalysis.xml"));
		Domain domain = domainmgr.getDomain();
		PresentationModelFactory pmf = new PresentationModelFactory(domain);
		
		// Run the br analysis.
		BenefitRiskAnalysis analysis = domain.getBenefitRiskAnalyses().first();
		analysis.runAllConsistencyModels();
		
		boolean modelsDone = false;
		while(!modelsDone) {
			boolean allDone = true;
			for (MetaAnalysis ma : analysis.getMetaAnalyses())
				if (ma instanceof NetworkMetaAnalysis)
					if (!(((NetworkMetaAnalysis) ma).getConsistencyModel()).isReady())
						allDone = false;
			modelsDone = allDone;
			Thread.sleep(100);
		}
		
		// check measurementsTable values
/*
 * The 'expected' values below are not guaranteed to be correct. This part of the test is intended to flag changes of the results.
 * However, since the results are non-deterministic the test might fail occasionally even if the algorithm hasn't changed.
 */
		BenefitRiskPM pm = (BenefitRiskPM) pmf.getModel(analysis);
		BenefitRiskMeasurementTableModel mtm = pm.getMeasurementTableModel(true);
		
		
		assertMeanwithinTenPercent(1.187, 0.904, 1.558,mtm.getValueAt(1, 1));
		assertMeanwithinTenPercent(1.269, 1.001, 1.610,mtm.getValueAt(2, 1));
		assertMeanwithinTenPercent(1.357, 1.103, 1.669,mtm.getValueAt(3, 1));
		
		assertMeanwithinTenPercent(0.574, 0.132, 2.496,mtm.getValueAt(1, 2));
		assertMeanwithinTenPercent(3.305, 0.772, 14.145,mtm.getValueAt(2, 2));
		assertMeanwithinTenPercent(0.494, 0.091, 2.683,mtm.getValueAt(3, 2));
		
		assertMeanwithinTenPercent(1.654, 0.696, 3.931,mtm.getValueAt(1, 3));
		assertMeanwithinTenPercent(0.689, 0.321, 1.479,mtm.getValueAt(2, 3));
		assertMeanwithinTenPercent(2.844, 1.625, 4.977,mtm.getValueAt(3, 3));
		
		assertMeanwithinTenPercent(0.831, 0.441, 1.565,mtm.getValueAt(1, 4));
		assertMeanwithinTenPercent(1.318, 0.702, 2.477,mtm.getValueAt(2, 4));
		assertMeanwithinTenPercent(0.647, 0.333, 1.258,mtm.getValueAt(3, 4));
		
		assertMeanwithinTenPercent(1.253, 0.342, 4.594,mtm.getValueAt(1, 5));
		assertMeanwithinTenPercent(1.856, 0.432, 7.976,mtm.getValueAt(2, 5));
		assertMeanwithinTenPercent(1.140, 0.294, 4.415,mtm.getValueAt(3, 5));
		
		assertMeanwithinTenPercent(1.401, 0.831, 2.364,mtm.getValueAt(1, 6));
		assertMeanwithinTenPercent(1.348, 0.770, 2.360,mtm.getValueAt(2, 6));
		assertMeanwithinTenPercent(1.877, 1.153, 3.055,mtm.getValueAt(3, 6));
	}
	
	@SuppressWarnings("unchecked")
	protected void assertMeanwithinTenPercent(double pointEstimate, double lowerBound, double upperBound, Object measurementTableObject) {
//		Note that this is the maximum allowed deviation (10%) for all cases in these tests, including the error in the expected (=recorded) value
		double allowedDeviation = 0.1;
		
		
		ConfidenceInterval ci = (ConfidenceInterval) ((PresentationModel<RelativeEffect<Measurement>>) measurementTableObject).getBean().getConfidenceInterval();
		assertEquals(pointEstimate, ci.getPointEstimate(), pointEstimate * allowedDeviation);
		assertEquals(lowerBound, ci.getLowerBound(), (Math.abs(lowerBound) + pointEstimate) * allowedDeviation);
		assertEquals(upperBound, ci.getUpperBound(), (Math.abs(upperBound) + pointEstimate) * allowedDeviation);
	}
}

	
