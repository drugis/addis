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

import static org.drugis.common.JUnitUtil.assertAllAndOnly;
import static org.easymock.EasyMock.verify;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DomainStudyListHolderTest {
	private DomainImpl d_domain;
	private DomainStudyListHolder d_pm;
	private ModifiableHolder<Indication> d_indication;
	private ModifiableHolder<OutcomeMeasure> d_outcome;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		
		d_indication = new ModifiableHolder<Indication>(ExampleData.buildIndicationDepression());
		d_outcome = new ModifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd());
		d_pm = new DomainStudyListHolder(d_domain,
				d_indication,
				d_outcome);
	}
	
	@Test
	public void testValue() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyBennie());
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(ExampleData.buildStudyMultipleArmsperDrug());
		
		assertAllAndOnly(studies, d_pm.getValue());
	}
	
	@Test
	public void testEndpointChangeTriggers() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyBennie());
		studies.add(ExampleData.buildStudyChouinard());
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm, "value", null, 
				studies);
		d_pm.addValueChangeListener(mock);
		
		d_outcome.setValue(ExampleData.buildEndpointCgi());
		assertAllAndOnly(studies, d_pm.getValue());
		verify(mock);
	}

	@Test
	public void testIndicationChangeTriggers() {
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm, "value", null, 
				Collections.<Study>emptyList());
		d_pm.addValueChangeListener(mock);
		
		d_indication.setValue(ExampleData.buildIndicationChronicHeartFailure());
		assertAllAndOnly(Collections.<Study>emptyList(), d_pm.getValue());
		verify(mock);
	}

	@Test
	public void testStudiesChangeTriggers() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyBennie());
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(ExampleData.buildStudyMultipleArmsperDrug());
		studies.add(ExampleData.buildStudyAdditionalThreeArm());
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm, "value", null, studies);
		d_pm.addValueChangeListener(mock);
		
		d_domain.addStudy(ExampleData.buildStudyAdditionalThreeArm());
		assertAllAndOnly(studies, d_pm.getValue());
		verify(mock);
	}
}