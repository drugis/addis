package org.drugis.addis.gui;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Source;
import org.junit.Test;

public class GUIFactoryTest {

	@Test
	public void testCreateToolTip() {
		assertEquals(
				"<html><b>From ClinicalTrials.gov</b><br>\ntest</html>",
				GUIFactory.createToolTip(new Note(Source.CLINICALTRIALS, "test")));
	}
	
	@Test
	public void testCreateToolTipHTMLEntities() {
		assertEquals(
				"<html><b>From ClinicalTrials.gov</b><br>\ntest &gt; you</html>",
				GUIFactory.createToolTip(new Note(Source.CLINICALTRIALS, "test > you")));
	}
}
