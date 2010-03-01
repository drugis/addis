package org.drugis.addis.entities.metaanalysis;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class NetworkMetaAnalysisTest {
	@Test
	public void testSetName() {
		NetworkMetaAnalysis analysis = new NetworkMetaAnalysis("NAME");
		JUnitUtil.testSetter(analysis, MetaAnalysis.PROPERTY_NAME, "NAME", "TEST");
	}
}
