package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.Dose;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.SIUnit;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class BasicPatientGroupPresentationTest {
	private BasicPatientGroup d_pg;

	@Before
	public void setUp() {
		d_pg = new BasicPatientGroup(null, null, null, 0);
	}
	
	@Test
	public void testGetLabel() {
		BasicPatientGroup group = d_pg;
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(group);
		assertEquals("INCOMPLETE", pres.getLabel());
		
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		group.setDose(dose);
		Drug drug = new Drug("Fluoxetine");
		group.setDrug(drug);
		assertEquals("Fluoxetine " + dose.toString(), pres.getLabel());
	}
	
	@Test
	public void testFireLabelChanged() {
		BasicPatientGroup group;
		PropertyChangeListener l;
		Drug drug = new Drug("Fluoxetine");
		
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(d_pg);
		
		group = d_pg;
		group.setDrug(drug);
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		group.setDose(dose);
		String expect = pres.getLabel();
		group.setDose(null);
		assertEquals("INCOMPLETE", pres.getLabel());
		l = JUnitUtil.mockListener(pres, BasicPatientGroupPresentation.PROPERTY_LABEL, "INCOMPLETE", expect);
		pres.addPropertyChangeListener(l);
		group.setDose(dose);
		assertEquals(expect, pres.getLabel());
		verify(l);
		
		group = d_pg;
		group.setDose(dose);
		Drug drug2 = new Drug("Paroxetine");
		group.setDrug(drug2);
		l = JUnitUtil.mockListener(pres, BasicPatientGroupPresentation.PROPERTY_LABEL, pres.getLabel(), expect);
		pres.addPropertyChangeListener(l);
		group.setDrug(drug);
		verify(l);
	}
}
