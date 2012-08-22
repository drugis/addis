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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.treatment.Category;
import org.drugis.addis.entities.treatment.TreatmentCategorization;
import org.drugis.addis.entities.treatment.LeafNode;
import org.drugis.addis.entities.treatment.RangeEdge;
import org.drugis.addis.presentation.wizard.TreatmentCategorizationWizardPresentation;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ObservableList;

import edu.uci.ics.jung.graph.util.Pair;

public class TreatmentCategorizationPresentationTest {

	private TreatmentCategorization d_tc;
	private TreatmentCategorizationPresentation d_pm;
	private Domain d_domain;
	private TreatmentCategorizationWizardPresentation d_wpm;

	@Before
	public void setUp() {
		d_tc = TreatmentCategorization.createDefault("HD/LD", ExampleData.buildDrugFluoxetine(), DoseUnit.createMilliGramsPerDay());
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_wpm = new TreatmentCategorizationWizardPresentation(d_tc, d_domain);
	}

	@Test
	public void testIncludedStudies() {
		final Study studyBennie = ExampleData.buildStudyBennie();		//dose 20
		final Study studyChouinard = ExampleData.buildStudyChouinard();	//dose 27.5
		final Study studyFava2002 = ExampleData.buildStudyFava2002();	//dose 30
		final Category foo = new Category(d_tc, "foo");
		final Category bar = new Category(d_tc, "bar");
		final Category baz = new Category(d_tc, "baz");

		// NOTE: studies Bennie and Chouinard have already been added by default, so just add Fava2002 and its dependencies here
		d_domain.getAdverseEvents().add(ExampleData.buildAdverseEventSexualDysfunction());
		d_domain.getStudies().add(studyFava2002);

		d_wpm.getCategories().add(foo);
		d_wpm.getCategories().add(bar);
		d_wpm.getCategories().add(baz);
		d_wpm.getModelForFixedDose().setValue(d_wpm.getFixedRangeNode());
		d_wpm.addDefaultRangeEdge(d_wpm.getFixedRangeNode());
		Pair<RangeEdge> splits = d_wpm.splitRange((RangeEdge) d_wpm.getOutEdges(d_wpm.getFixedRangeNode()).get(0), 21.0, false);
		d_wpm.getModelForEdge(splits.getFirst()).setValue(new LeafNode(foo));
		d_wpm.getModelForEdge(splits.getSecond()).setValue(new LeafNode(bar));

		d_pm = new TreatmentCategorizationPresentation(d_tc, d_domain);
		final ObservableList<Study> fooStudies = d_pm.getCategorizedStudyList(foo).getIncludedStudies();
		final ObservableList<Study> barStudies = d_pm.getCategorizedStudyList(bar).getIncludedStudies();
		final ObservableList<Study> bazStudies = d_pm.getCategorizedStudyList(baz).getIncludedStudies();

		assertTrue(fooStudies.contains(studyBennie));
		assertFalse(fooStudies.contains(studyChouinard));
		assertFalse(fooStudies.contains(studyFava2002));
		
		assertTrue(barStudies.contains(studyChouinard));
		assertTrue(barStudies.contains(studyFava2002));
		assertTrue(bazStudies.isEmpty());
	}
}
