package org.drugis.common.gui;

import static org.junit.Assert.*;

import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Source;
import org.junit.Before;
import org.junit.Test;

public class GUIHelperTest {

	@Before
	public void setUp() {

	}
	
	@Test
	public void testCreateToolTip() {
		assertEquals(
				"<html><b>From ClinicalTrials.gov</b><br>\ntest</html>",
				GUIHelper.createToolTip(new Note(Source.CLINICALTRIALS, "test")));
	}
}