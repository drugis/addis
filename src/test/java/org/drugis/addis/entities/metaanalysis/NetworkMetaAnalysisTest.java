package org.drugis.addis.entities.metaanalysis;

import static org.junit.Assert.*;

import org.drugis.addis.ExampleData;
import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class NetworkMetaAnalysisTest {
	@Test
	public void testSetName() {
		NetworkMetaAnalysis analysis = ExampleData.buildNetworkMetaAnalysis();
		JUnitUtil.testSetter(analysis, MetaAnalysis.PROPERTY_NAME, analysis.getName(), "TEST");
	}
	
	@Test
	public void testGetType() {
		NetworkMetaAnalysis analysis = ExampleData.buildNetworkMetaAnalysis();
		assertEquals("Markov Chain Monte Carlo Network Meta-Analysis", analysis.getType());
	}
}
