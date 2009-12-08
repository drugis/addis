package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.MeanDifference;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class RandomEffectsMetaAnalysisPresentationTest {
	@Test
	public void testGetAnalysisTypeRate() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointHamd(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugParoxetine());
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		RandomEffectsMetaAnalysisPresentation pres = (RandomEffectsMetaAnalysisPresentation) fact.getModel(meta);
		assertEquals(Endpoint.Type.RATE, pres.getAnalysisType());
	}
	
	@Test
	public void testGetAnalysisTypeContinuous() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointCgi(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugParoxetine());
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		RandomEffectsMetaAnalysisPresentation pres = (RandomEffectsMetaAnalysisPresentation) fact.getModel(meta);
		assertEquals(Endpoint.Type.CONTINUOUS, pres.getAnalysisType());
	}
	
	@Test
	public void testGetIncludedStudies() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointCgi(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugParoxetine());
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		RandomEffectsMetaAnalysisPresentation pres = (RandomEffectsMetaAnalysisPresentation) fact.getModel(meta);
		List<Study> expected = new ArrayList<Study>();
		expected.add(ExampleData.buildStudyChouinard());

		JUnitUtil.assertAllAndOnly(expected, pres.getIncludedStudies().getValue());
	}
	
	@Test
	public void testGetForestPlotPresentation() {
		RandomEffectsMetaAnalysis meta = new RandomEffectsMetaAnalysis("meta",
				ExampleData.buildEndpointCgi(),
				Collections.singletonList(ExampleData.buildStudyChouinard()),
				ExampleData.buildDrugFluoxetine(),
				ExampleData.buildDrugParoxetine());
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain );
		PresentationModelFactory fact = new PresentationModelFactory(domain);
		RandomEffectsMetaAnalysisPresentation pres = (RandomEffectsMetaAnalysisPresentation) fact.getModel(meta);
		
		ForestPlotPresentation expected = new ForestPlotPresentation(meta, MeanDifference.class, 
				new PresentationModelFactory(new DomainImpl()));
		ForestPlotPresentation actual = pres.getForestPlotPresentation(MeanDifference.class);
		assertEquals(expected.getRelativeEffectAt(0).getRelativeEffect(), actual.getRelativeEffectAt(0).getRelativeEffect(), 0.001);
		assertEquals(expected.getHeterogeneity(), actual.getHeterogeneity());
		assertEquals(expected.getHeterogeneityI2(), actual.getHeterogeneityI2());
	}
	
}
