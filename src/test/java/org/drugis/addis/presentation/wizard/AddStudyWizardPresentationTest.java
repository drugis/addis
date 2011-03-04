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

package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MalformedURLException;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Source;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.pietschy.wizard.InvalidStateException;

public class AddStudyWizardPresentationTest {
	
	private Domain d_domain;
	private AddStudyWizardPresentation d_wizardImported;
	private AddStudyWizardPresentation d_wizard;
	
	@Before
	public void setUp(){
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_wizard = new AddStudyWizardPresentation(d_domain, new PresentationModelFactory(d_domain), null);
	}
	
	private void importStudy() throws MalformedURLException, IOException {
		d_wizardImported = new AddStudyWizardPresentation(d_domain, new PresentationModelFactory(d_domain), null);
		d_wizardImported.getIdModel().setValue("NCT00644527");
		d_wizardImported.importCT();
	}
	
	@Test
	public void testSetIdGetSourceModel() throws MalformedURLException, IOException{
		importStudy();
		assertEquals(Source.CLINICALTRIALS, d_wizardImported.getSourceModel().getValue());
	}
	
	@Test
	public void testGetTitleModel() throws MalformedURLException, IOException {
		importStudy();
		assertEquals("Receptive Music Therapy for the Treatment of Depression", d_wizardImported.getTitleModel().getValue());
	}
	
	@Test
	public void testGetTitleNoteModel() throws MalformedURLException, IOException {
		importStudy();
		String titleNote = (String) d_wizardImported.getCharacteristicNoteModel(BasicStudyCharacteristic.TITLE).getValue();
		assertTrue(titleNote.contains("Rezeptive Musiktherapie"));
	}
	
	@Test
	public void testGetIndicationListModel() {
		assertTrue(d_wizard.getIndicationListModel().getValue().containsAll(d_domain.getIndications()));
	}
	
	@Test
	public void testGetIndicationNoteModel() throws MalformedURLException, IOException {
		importStudy();
		assertEquals("Depression",d_wizardImported.getIndicationNoteModel().getValue());
	}
	
	@Test
	public void testClearStudies() throws MalformedURLException, IOException {
		importStudy();
		d_wizardImported.clearStudies();
		assertEquals(Source.MANUAL, d_wizardImported.getSourceModel().getValue());
		assertEquals(null, d_wizardImported.getTitleModel().getValue());
		assertEquals(null,d_wizardImported.getIndicationNoteModel().getValue());
		//assertEquals(1,d_wizardImported.getNumberEndpoints());
		assertEquals(1,d_wizardImported.getEndpointSelectModel().getSlots().size());
		assertEquals(2,d_wizardImported.getNumberArms());
	}
	
	@Test
	public void testGetCharacteristicNoteModel() throws MalformedURLException, IOException {
		importStudy();
		assertEquals("March 2008",d_wizardImported.getCharacteristicNoteModel(BasicStudyCharacteristic.STUDY_START).getValue());
	}
	
	@Test
	public void testgetNumberEndpoints() {
		int numEndpoints = d_wizard.getEndpointSelectModel().getSlots().size();
		d_wizard.getEndpointSelectModel().addSlot();
		d_wizard.getEndpointSelectModel().addSlot();
		assertEquals(numEndpoints + 2, d_wizard.getEndpointSelectModel().getSlots().size());
	}
	
	@Test
	public void testgetNumberArms() {
		int numArms = d_wizard.getNumberArms();
		d_wizard.addArms(2);
		assertEquals(numArms + 2,d_wizard.getNumberArms());
		d_wizard.removeArm(0);
		assertEquals(numArms + 1,d_wizard.getNumberArms());
	}
	
	@Test
	public void testCheckID() {
		d_wizard.getNewStudyPM().getBean().setStudyId("This is not in the domain");
		assertEquals(true, d_wizard.isIdAvailable());
		d_wizard.getIdModel().setValue(d_domain.getStudies().first().getStudyId());
		assertEquals(false,d_wizard.isIdAvailable());
	}
	
	@Test
	public void testSaveStudy() {
		d_wizard.getNewStudyPM().getBean().setStudyId("This is not in the domain");
		d_wizard.getEndpointSelectModel().getSlot(0).setValue(d_domain.getEndpoints().first());
		d_wizard.getIndicationModel().setValue(d_domain.getIndications().first());
		d_wizard.saveStudy();
		assertTrue(d_domain.getStudies().contains(d_wizard.getStudy()));
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSaveNoEndpoint() {
		d_wizard.getIndicationModel().setValue(d_domain.getIndications().first());
		assertEquals(1, d_wizard.getEndpointSelectModel().getSlots().size());
		//d_wizard.removeEndpoint(0);
		d_wizard.getEndpointSelectModel().removeSlot(0);
		d_wizard.saveStudy();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSaveIllegalID() throws InvalidStateException {
		d_wizard.getIdModel().setValue(d_domain.getStudies().first().getStudyId());
		d_wizard.getEndpointSelectModel().getSlot(0).setValue(d_domain.getEndpoints().first());
		d_wizard.getIndicationModel().setValue(d_domain.getIndications().first());
		d_wizard.saveStudy();
	}
	
	@Test
	@Ignore //FIXME: re-enable once notes are handled in a sane way.
	public void testTransferNotes() throws MalformedURLException, IOException {
		importStudy();
		d_wizardImported.getIndicationModel().setValue(d_domain.getIndications().first());
		d_wizardImported.getEndpointSelectModel().getSlot(0).setValue(d_domain.getEndpoints().first());
		d_wizardImported.getEndpointSelectModel().getSlot(1).setValue(d_domain.getEndpoints().last());
		d_wizardImported.saveStudy();
		assertEquals("NCT00644527", d_wizardImported.getStudy().getNote(Study.PROPERTY_ID).getText());
		assertTrue(d_wizardImported.getStudy().getNote(BasicStudyCharacteristic.TITLE).getText().contains("Rezeptive Musiktherapie Bei Depression"));
		assertTrue(d_wizardImported.getStudy().getNote(d_wizardImported.getEndpointSelectModel().getSlot(0).getValue()).getText().contains("the Beck Depression Inventory (single weighted)"));
		assertTrue(d_wizardImported.getStudy().getNote(d_wizardImported.getArmModel(3).getBean()).getText().contains("Each 50% of the subjects will be assigned randomly"));
	}
}
