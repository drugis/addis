/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
 * Copyright (C) 2012 Gert van Valkenhoef, Daniel Reid, 
 * Joël Kuiper, Wouter Reckman.
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

package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertEquals;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Study;
import org.drugis.common.event.ListDataEventMatcher;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class AddEpochsPresentationTest {
	private Study d_study;
	
	@Before
	public void setUp() {
		d_study = ExampleData.buildStudyBennie().clone();
	}
	
	@Test
	public void testRename() {
		AddEpochsPresentation presentation = new AddEpochsPresentation(d_study, "Epoch", 1);
		
		ListDataListener mockListener = EasyMock.createStrictMock(ListDataListener.class);
		mockListener.contentsChanged(ListDataEventMatcher.eqListDataEvent(
				new ListDataEvent(d_study.getEpochs(), ListDataEvent.CONTENTS_CHANGED, 0, 0)));
		EasyMock.replay(mockListener);
		
		d_study.getEpochs().addListDataListener(mockListener);
		
		final String newName = "Other epoch";
		presentation.rename(0, newName);
		
		EasyMock.verify(mockListener);
		assertEquals(newName, d_study.getEpochs().get(0).getName());
		assertEquals(newName, presentation.getList().get(0).getName());
	}
	
	@Test
	public void testRenameAfterSetStudy() {
		AddEpochsPresentation presentation = new AddEpochsPresentation(new Study(), "Epoch", 1);
		presentation.setStudy(d_study);
		
		ListDataListener mockListener = EasyMock.createStrictMock(ListDataListener.class);
		mockListener.contentsChanged(ListDataEventMatcher.eqListDataEvent(
				new ListDataEvent(d_study.getEpochs(), ListDataEvent.CONTENTS_CHANGED, 0, 0)));
		EasyMock.replay(mockListener);
		
		d_study.getEpochs().addListDataListener(mockListener);
		
		final String newName = "Other epoch";
		presentation.rename(0, newName);
		
		EasyMock.verify(mockListener);
		assertEquals(newName, d_study.getEpochs().get(0).getName());
		assertEquals(newName, presentation.getList().get(0).getName());
	}
}
