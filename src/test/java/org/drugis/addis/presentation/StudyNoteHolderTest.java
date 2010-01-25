package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Study;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyNoteHolderTest {
	private Study d_study;
	private StudyNoteHolder d_noteHolder;

	@Before
	public void setUp() {
		d_study = new Study("", new Indication(0l, ""));
		d_study.putNote("key", new Note("testNote"));
		d_noteHolder = new StudyNoteHolder(d_study,"key");
	}
	
	@Test
	public void testGetValue() {
		assertEquals("testNote",d_noteHolder.getValue());
	}
	
	@Test(expected = RuntimeException.class)
	public void testSetValue() {
		d_noteHolder.setValue(new Note());
	}
	
	@Test
	public void testValueChanged() {
		Note n = new Note("newNote");
		PropertyChangeListener l = JUnitUtil.mockListener(d_noteHolder, "value", null, n.getText());
		d_noteHolder.addPropertyChangeListener(l);
		d_study.putNote("key",n);
		verify(l);
	}
}
