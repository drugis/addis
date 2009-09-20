/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009  Gert van Valkenhoef and Tommi Tervonen.
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

package org.drugis.addis.presentation.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.test.TestData;
import org.drugis.addis.presentation.PresentationModelManager;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.PresentationModel;

public class PresentationModelManagerTest {
	
	private PresentationModelManager d_model;

	@Before
	public void setUp() {
		this.d_model = new PresentationModelManager();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMetaStudyGetModel() {
		Domain d = new DomainImpl();
		TestData.initDefaultData(d);
		List<Study> studies = new ArrayList<Study>();
		studies.addAll(d.getStudies());
		MetaAnalysis anal = new MetaAnalysis(TestData.buildEndpointHamd(),
				studies);
		MetaStudy s = new MetaStudy("ms", anal);
		
		PresentationModel m = d_model.getModel(s);
		assertEquals(s, m.getBean());
		PresentationModel m2 = d_model.getModel(s);
		assertTrue(m == m2);
	}

	@Test
	public void testGetOtherModel() {
		Domain d = new DomainImpl();
		TestData.initDefaultData(d);
		assertNotNull(d_model.getModel((BasicStudy) d.getStudies().first()));
	}
}
