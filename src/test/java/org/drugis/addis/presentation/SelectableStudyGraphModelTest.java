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

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class SelectableStudyGraphModelTest {

	private DomainImpl d_domain;
	private ArrayList<Drug> d_drugs;
	private SelectableStudyGraphModel d_pm;
	private ListHolder<Drug> d_drugListHolder;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_drugs = new ArrayList<Drug>();
		d_drugs.add(ExampleData.buildDrugFluoxetine());
		d_drugs.add(ExampleData.buildDrugParoxetine());
		d_drugs.add(ExampleData.buildDrugSertraline());
		d_drugListHolder = new DefaultListHolder<Drug>(d_drugs);
		d_pm = new SelectableStudyGraphModel(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd()),
				d_drugListHolder, d_domain);
	}
	
	@Test
	public void testGetSelectedDrugsModel() {
		
		ListHolder<Drug> selDrugs = d_pm.getSelectedDrugsModel();
		List<Drug> list = Collections.singletonList(ExampleData.buildDrugFluoxetine());
		
		PropertyChangeListener mock = JUnitUtil.mockListener(selDrugs, "value", selDrugs.getValue(), list);
		selDrugs.addValueChangeListener(mock);
		
		d_drugListHolder.setValue(list);
		verify(mock);
		
		
	}
	
	@Test
	public void testIsSelectionCollected() {
		assertTrue(d_pm.isSelectionConnected());
		
		d_drugs.remove(ExampleData.buildDrugFluoxetine());
		d_drugListHolder = new DefaultListHolder<Drug>(d_drugs);
		d_pm = new SelectableStudyGraphModel(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd()),
				d_drugListHolder, d_domain);
		
		assertFalse(d_pm.isSelectionConnected());
	}
}
