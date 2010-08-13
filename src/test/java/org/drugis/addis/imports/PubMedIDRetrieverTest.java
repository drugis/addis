package org.drugis.addis.imports;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class PubMedIDRetrieverTest {
	
	private List<String> testPubMedIDs = new ArrayList<String>(0);

	@Before
	public void setUp() {
		testPubMedIDs.add("TESTING");
		testPubMedIDs.clear();
	}
	
	@Test
	public void testImportPubMedID() {
		
		try{
			testPubMedIDs = new PubMedIDRetriever().importPubMedID("NCT0000400");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}	
}
