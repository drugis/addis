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

package org.drugis.addis.presentation;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.TreatmentCategorySet;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.common.event.ListDataEventMatcher;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class SelectableStudyGraphModelTest {

	private Domain d_domain;
	private ArrayList<TreatmentCategorySet> d_drugs;
	private SelectableStudyGraphModel d_pm;
	private ObservableList<TreatmentCategorySet> d_drugListHolder;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_drugs = new ArrayList<TreatmentCategorySet>();
		d_drugs.add(TreatmentCategorySet.createTrivial(ExampleData.buildDrugFluoxetine()));
		d_drugs.add(TreatmentCategorySet.createTrivial(ExampleData.buildDrugParoxetine()));
		d_drugs.add(TreatmentCategorySet.createTrivial(ExampleData.buildDrugSertraline()));
		d_drugListHolder = new ArrayListModel<TreatmentCategorySet>(d_drugs);
		ValueHolder<OutcomeMeasure> outcome = new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd());
		ObservableList<Study> studies = new ArrayListModel<Study>(Arrays.asList(
				ExampleData.buildStudyBennie(), ExampleData.buildStudyChouinard(), 
				ExampleData.buildStudyDeWilde(), ExampleData.buildStudyMultipleArmsperDrug()));
		d_pm = new SelectableStudyGraphModel(studies, d_drugListHolder, outcome);
	}
	
	@Test
	public void testGetSelectedDrugsModel() {
		ObservableList<TreatmentCategorySet> selDrugs = d_pm.getSelectedDrugsModel();
		List<TreatmentCategorySet> list = Collections.singletonList(TreatmentCategorySet.createTrivial(ExampleData.buildDrugFluoxetine()));
		
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(selDrugs, ListDataEvent.INTERVAL_REMOVED, 0, 2)));
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(selDrugs, ListDataEvent.INTERVAL_ADDED, 0, 0)));
		replay(mock);
		
		selDrugs.addListDataListener(mock);
		d_drugListHolder.clear();
		d_drugListHolder.addAll(list);
		d_pm.rebuildGraph();
		verify(mock);
	}
	
	@Test
	public void testIsSelectionConnected() {
		assertTrue(d_pm.isSelectionConnected());

		d_drugs.remove(TreatmentCategorySet.createTrivial(ExampleData.buildDrugFluoxetine()));
		d_pm.getSelectedDrugsModel().clear();
		d_pm.getSelectedDrugsModel().addAll(d_drugs);
		
		assertFalse(d_pm.isSelectionConnected());
	}
	
	@Test
	public void testDontResetSelectedDrugsWhenNoChanges() {
		d_pm.getSelectedDrugsModel().remove(0);
		List<TreatmentCategorySet> expected = new ArrayList<TreatmentCategorySet>(d_pm.getSelectedDrugsModel());
		d_pm.rebuildGraph();
		assertEquals(expected, d_pm.getSelectedDrugsModel());
	}
	
}
