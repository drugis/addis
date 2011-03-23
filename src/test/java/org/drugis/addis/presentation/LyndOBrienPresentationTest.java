package org.drugis.addis.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.analysis.MetaAnalysis;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.BenefitRiskAnalysis.AnalysisType;
import org.junit.Test;

public class LyndOBrienPresentationTest {
	@Test
	public void testCreateBeforeMeasurementsReady() {
		MetaBenefitRiskAnalysis br = buildAnalysis();
		new LyndOBrienPresentation<Drug, MetaBenefitRiskAnalysis>(br);
	}
	
	public static MetaBenefitRiskAnalysis buildAnalysis() {
		Indication indication = ExampleData.buildIndicationDepression();
		
		List<OutcomeMeasure> outcomeMeasureList = new ArrayList<OutcomeMeasure>();
		outcomeMeasureList.add(ExampleData.buildEndpointHamd());
		outcomeMeasureList.add(ExampleData.buildAdverseEventConvulsion());
		
		List<MetaAnalysis> metaAnalysisList = new ArrayList<MetaAnalysis>();
		metaAnalysisList.add(ExampleData.buildMetaAnalysisHamd());
		metaAnalysisList.add(ExampleData.buildMetaAnalysisConv());
		
		Drug parox = ExampleData.buildDrugParoxetine();
		List<Drug> fluoxList = Collections.singletonList(ExampleData.buildDrugFluoxetine());
		
		return new MetaBenefitRiskAnalysis("testBenefitRiskAnalysis",
										indication, metaAnalysisList, parox, fluoxList, AnalysisType.LyndOBrien);										
	}
}
