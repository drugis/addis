package org.drugis.common.test;

import static org.junit.Assert.*;

import java.awt.event.ActionEvent;

import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;

import org.drugis.common.EventObjectMatcher;
import org.junit.Test;

public class EventObjectMatcherTest {
	@Test
	public void testMatches() {
		DefaultTableModel model = new DefaultTableModel();
		EventObjectMatcher matcher = new EventObjectMatcher(
				new TableModelEvent(model));
		assertTrue(matcher.matches(new TableModelEvent(model)));
		assertFalse(matcher.matches(new TableModelEvent(new DefaultTableModel())));
		assertFalse(matcher.matches(new ActionEvent(model, 0, "")));
	}
}
