package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.junit.Before;
import org.junit.Test;

public class ForestPlotPresentationMetaTest {
	private ForestPlotPresentation d_pm;
	
	@Before
	public void setUp() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		RandomEffectsMetaAnalysis analysis = new RandomEffectsMetaAnalysis("TestMetaAnalysis",ExampleData.buildEndpointHamd(),
				studies, ExampleData.buildDrugFluoxetine(), ExampleData.buildDrugParoxetine());
		d_pm = new ForestPlotPresentation(analysis, OddsRatio.class);
	}
	
	@Test
	public void testNumEffects() {
		assertEquals(3, d_pm.getNumRelativeEffects());
	}
	
	@Test
	public void testStudyLabels() {
		assertEquals(ExampleData.buildStudyChouinard().toString(),
				d_pm.getStudyLabelAt(0));
		assertEquals(ExampleData.buildStudyDeWilde().toString(),
				d_pm.getStudyLabelAt(1));
	}
	
	@Test
	public void testCombinedLabel() {
		assertEquals("Combined", d_pm.getStudyLabelAt(2));
	}
	
	@Test
	public void testIsCombined() {
		assertEquals(false, d_pm.isCombined(0));
		assertEquals(false, d_pm.isCombined(1));
		assertEquals(true, d_pm.isCombined(2));
	}
	
	@Test
	public void testGetDiamondSize() {
		assertEquals(21, d_pm.getDiamondSize(0));
		assertEquals(0, d_pm.getDiamondSize(2));
	}
}
