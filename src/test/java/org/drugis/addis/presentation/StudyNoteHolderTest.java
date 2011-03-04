/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Note;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyNoteHolderTest {
	private Study d_study;
	private StudyNoteHolder d_noteHolder;

	@Before
	public void setUp() {
		Indication indication = new Indication(0l, "");
		d_study = new Study("", indication);
		d_study.putNote(Study.PROPERTY_INDICATION, new Note(Source.CLINICALTRIALS, "testNote"));
		d_noteHolder = new StudyNoteHolder(d_study, Study.PROPERTY_INDICATION);
	}
	
	@Test
	public void testGetValue() {
		assertEquals("testNote",d_noteHolder.getValue());
	}
	
	@Test(expected = RuntimeException.class)
	public void testSetValue() {
		d_noteHolder.setValue(new Note(Source.CLINICALTRIALS));
	}
	
	@Test
	public void testValueChanged() {
		Note n = new Note(Source.CLINICALTRIALS, "newNote");
		PropertyChangeListener l = JUnitUtil.mockListener(d_noteHolder, "value", null, n.getText());
		d_noteHolder.addPropertyChangeListener(l);
		d_study.putNote(Study.PROPERTY_INDICATION, n);
		verify(l);
	}
}
