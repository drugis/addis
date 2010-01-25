package org.drugis.addis.presentation;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.junit.Before;
import org.junit.Test;

public class AddStudyWizardPresentationTest {
	
	private Domain d_domain;
	private AddStudyWizardPresentation d_wizard;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_wizard = new AddStudyWizardPresentation(d_domain, new PresentationModelFactory(d_domain));
	}
	
	@Test
	public void testGetSourceModel() {
		// Test whether source = ct when imported.
		//d_wizard.getIdNoteModel().setValue("NCT00644527");
	}
	
	@Test
	public void testGetIDModel() {
	}
	
	@Test
	public void testGetTitleModel() {
	}

}
