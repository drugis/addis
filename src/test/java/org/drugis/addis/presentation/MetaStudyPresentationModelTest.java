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

package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.entities.ExampleData;
import org.drugis.addis.presentation.MetaStudyPresentationModel;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class MetaStudyPresentationModelTest {
	private MetaStudy d_study;
	private Domain d_domain;
	private MetaStudyPresentationModel d_presentationModel;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		MetaAnalysis ma = new MetaAnalysis(ExampleData.buildEndpointHamd(), new ArrayList<Study>(d_domain.getStudies()));
		d_study = new MetaStudy("Meta", ma);
		d_presentationModel = new MetaStudyPresentationModel(d_study);
	}
	
	@Test
	public void testGetIncludedStudies() {
		assertEquals(d_study.getAnalysis().getStudies(), d_presentationModel.getIncludedStudies());
	}
	
	@Test
	public void testGetCharacteristicVisibleModel() {
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			AbstractValueModel m = d_presentationModel.getCharacteristicVisibleModel(c);
			JUnitUtil.testSetter(m, "value", Boolean.TRUE, Boolean.FALSE);
			
			m = d_presentationModel.getCharacteristicVisibleModel(c);
			assertEquals(Boolean.FALSE, m.getValue());
		}
	}
}
