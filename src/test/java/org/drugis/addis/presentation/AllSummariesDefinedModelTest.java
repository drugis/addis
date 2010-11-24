package org.drugis.addis.presentation;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.analysis.MetaBenefitRiskAnalysis;
import org.drugis.addis.entities.analysis.StudyBenefitRiskAnalysis;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.mtc.summary.Summary;
import org.junit.Before;
import org.junit.Test;

public class AllSummariesDefinedModelTest {
	
	private DomainImpl d_domain;
	private PresentationModelFactory d_pmf;
	private MetaBenefitRiskPresentation d_mpm;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		d_pmf = new PresentationModelFactory(d_domain);
		MetaBenefitRiskAnalysis metaBRanalysis = ExampleData.buildMetaBenefitRiskAnalysis();
		StudyBenefitRiskAnalysis studyBRanalysis = ExampleData.buildStudyBenefitRiskAnalysis();
		d_mpm = new MetaBenefitRiskPresentation(metaBRanalysis, d_pmf);
		
		// test creation; no further tests required
		new StudyBenefitRiskPresentation(studyBRanalysis, d_pmf);
	}
	
	public class MySummary extends AbstractObservable implements Summary {
		private boolean d_defined;

		public MySummary(boolean defined) {
			d_defined = defined;
		}
		
		public boolean getDefined() {
			return d_defined;
		}
		
		public void setDefined(boolean defined) {
			boolean oldValue = d_defined;
			d_defined = defined;
			firePropertyChange(PROPERTY_DEFINED, oldValue, d_defined);
		}
	}

	@Test	
	public void testSingleSummary() {
		MySummary trueSummary = new MySummary(true);
		AllSummariesDefinedModel trueModel = new AllSummariesDefinedModel(Collections.singletonList(trueSummary));
		assertTrue(trueModel.getValue());
		
		MySummary falseSummary = new MySummary(false);
		AllSummariesDefinedModel falseModel = new AllSummariesDefinedModel(Collections.singletonList(falseSummary));
		assertFalse(falseModel.getValue());
	}
	
	@Test public void testMultipleSummaries() {
		MySummary summary1 = new MySummary(true);
		MySummary summary2 = new MySummary(false);
		List<Summary> summaries = Arrays.asList(new Summary[] { summary1, summary2 });
		
		AllSummariesDefinedModel allValuesModel = new AllSummariesDefinedModel(summaries);

		assertEquals(false, allValuesModel.getValue());
		
		summary2.setDefined(true);
		allValuesModel = new AllSummariesDefinedModel(summaries);
		assertEquals(true, allValuesModel.getValue());
	}
}
