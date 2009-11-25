package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import java.util.Collections;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
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
}
