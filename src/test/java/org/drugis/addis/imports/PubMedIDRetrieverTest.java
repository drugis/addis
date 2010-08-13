package org.drugis.addis.imports;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class PubMedIDRetrieverTest {
	@Before
	public void setUp() {
	}
	
	@Test
	public void testImportPubMedID() throws MalformedURLException, IOException {
		List<String> testPubMedIDs = new PubMedIDRetriever().importPubMedID("NCT00000400");
		JUnitUtil.assertAllAndOnly(Collections.singletonList("19401368"), testPubMedIDs);
	}
	
	@Test
	public void testImportPubMedNoID() throws MalformedURLException, IOException {
		List<String> testPubMedIDs = new PubMedIDRetriever().importPubMedID("NCT");
		JUnitUtil.assertAllAndOnly(Collections.emptyList(), testPubMedIDs);
	}
}
