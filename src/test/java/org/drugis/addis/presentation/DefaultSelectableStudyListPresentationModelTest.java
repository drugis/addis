/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
 * Copyright (C) 2010  Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs and Hanno Koeslag.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Test;

public class DefaultSelectableStudyListPresentationModelTest {

	private DefaultSelectableStudyListPresentationModel d_model;
	private DefaultListHolder<Study> d_holder;
	private Study d_s1;
	private Study d_s2;
	private Indication d_ind;
	private List<Study> d_studies;

	@Before
	public void setUp() {
		d_studies = new ArrayList<Study>();
		d_ind = new Indication(0L, "ind");
		d_s1 = new Study("s1", d_ind);
		d_s2 = new Study("s2", d_ind);		
		d_studies.add(d_s1);
		d_studies.add(d_s2);
		d_holder = new DefaultListHolder<Study>(d_studies);
		
		d_model = new DefaultSelectableStudyListPresentationModel(d_holder); 
	}
	
	@Test
	public void testGetSelectedBooleanModel() {
		assertTrue(d_model.getSelectedStudyBooleanModel(d_s1).getValue());
		assertTrue(d_model.getSelectedStudyBooleanModel(d_s2).getValue());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUnknownStudy() {
		d_model.getSelectedStudyBooleanModel(new Study("xxxx", d_ind));
	}
	
	@Test
	public void testGetSelectedModelOnChange() {
		d_model.getSelectedStudyBooleanModel(d_s1).setValue(false);
		
		List<Study> newList = new ArrayList<Study>(d_studies);
		Study newStudy = new Study("new study", d_ind);
		newList.add(newStudy);
		d_holder.setValue(newList);
		
		assertFalse(d_model.getSelectedStudyBooleanModel(d_s1).getValue());
		assertTrue(d_model.getSelectedStudyBooleanModel(d_s2).getValue());
		assertTrue(d_model.getSelectedStudyBooleanModel(newStudy).getValue());
	}
	
	@Test
	public void testGetSelectedStudiesModel() {
		assertEquals(d_studies, d_model.getSelectedStudiesModel().getValue());
		d_model.getSelectedStudyBooleanModel(d_s1).setValue(false);
		assertEquals(Collections.singletonList(d_s2), d_model.getSelectedStudiesModel().getValue());	
	}
}
