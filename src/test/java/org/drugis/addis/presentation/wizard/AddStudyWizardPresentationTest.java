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
import org.drugis.common.JUnitUtil;
import org.junit.Before;
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
		assertEquals(1,d_wizardImported.getNumberEndpoints());
		assertEquals(2,d_wizardImported.getNumberArms());
	}
	
	@Test
	public void testGetCharacteristicNoteModel() throws MalformedURLException, IOException {
		importStudy();
		assertEquals("March 2008",d_wizardImported.getCharacteristicNoteModel(BasicStudyCharacteristic.STUDY_START).getValue());
	}
	
	@Test
	public void testGetEndpointListModel() {
		JUnitUtil.assertAllAndOnly(d_domain.getEndpoints(), d_wizard.getEndpointListModel().getValue());
	}
	
	@Test 
	public void testGetEndpointNoteModel() throws MalformedURLException, IOException {
		importStudy();
		String note = (String) d_wizardImported.getEndpointNoteModel(1).getValue();
		assertTrue(note.contains("Quality of life"));
		d_wizardImported.removeEndpoint(0);
		note = (String) d_wizardImported.getEndpointNoteModel(0).getValue();
		assertTrue(note.contains("Quality of life"));
	}
	
	@Test
	public void testgetNumberEndpoints() {
		int numEndpoints = d_wizard.getNumberEndpoints();
		d_wizard.addEndpointModels(2);
		assertEquals(numEndpoints + 2, d_wizard.getNumberEndpoints());
	}
	
	@Test
	public void testgetNumberArms() {
		int numArms = d_wizard.getNumberArms();
		d_wizard.addArmModels(2);
		assertEquals(numArms + 2,d_wizard.getNumberArms());
		d_wizard.removeArm(0);
		assertEquals(numArms + 1,d_wizard.getNumberArms());
	}
	
	@Test
	public void testCheckID() {
		assertEquals(true,d_wizard.checkID());
		d_wizard.getIdModel().setValue(d_domain.getStudies().first().getStudyId());
		assertEquals(false,d_wizard.checkID());
	}
	
	@Test
	public void testSaveStudy() {
		d_wizard.getEndpointModel(0).setValue(d_domain.getEndpoints().first());
		d_wizard.getIndicationModel().setValue(d_domain.getIndications().first());
		d_wizard.saveStudy();
		assertTrue(d_domain.getStudies().contains(d_wizard.getStudy()));
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSaveNoEndpoint() {
		d_wizard.getIndicationModel().setValue(d_domain.getIndications().first());
		assertEquals(1, d_wizard.getNumberEndpoints());
		d_wizard.removeEndpoint(0);
		d_wizard.saveStudy();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSaveIllegalID() throws InvalidStateException {
		d_wizard.getIdModel().setValue(d_domain.getStudies().first().getStudyId());
		d_wizard.getEndpointModel(0).setValue(d_domain.getEndpoints().first());
		d_wizard.getIndicationModel().setValue(d_domain.getIndications().first());
		d_wizard.saveStudy();
	}
	
	@Test
	public void testTransferNotes() throws MalformedURLException, IOException {
		importStudy();
		d_wizardImported.getIndicationModel().setValue(d_domain.getIndications().first());
		d_wizardImported.getEndpointModel(0).setValue(d_domain.getEndpoints().first());
		d_wizardImported.getEndpointModel(1).setValue(d_domain.getEndpoints().last());
		d_wizardImported.commitOutcomesArmsToNew();
		d_wizardImported.saveStudy();
		assertEquals("NCT00644527",d_wizardImported.getStudy().getNote(Study.PROPERTY_ID).getText());
		assertTrue(d_wizardImported.getStudy().getNote(BasicStudyCharacteristic.TITLE).getText().contains("Rezeptive Musiktherapie Bei Depression"));
		assertTrue(d_wizardImported.getStudy().getNote(d_wizardImported.getEndpointModel(0).getValue()).getText().contains("the Beck Depression Inventory (single weighted)"));
		assertTrue(d_wizardImported.getStudy().getNote(d_wizardImported.getArmModel(3).getBean()).getText().contains("Each 50% of the subjects will be assigned randomly"));
	}
}
