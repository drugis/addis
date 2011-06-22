package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.CombinationTreatment;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.presentation.CombinationTreatmentPresentation;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.verify;

public class CombinationTreatmentPresentationTest {

	private CombinationTreatment d_combTreatment;
	private CombinationTreatmentPresentation d_pm;

	@Before
	public void setUp() {
		d_combTreatment = new CombinationTreatment();
		d_combTreatment.addTreatment(new Drug("Fluoxetine", "N06AB12"), new FixedDose(12.0, SIUnit.MILLIGRAMS_A_DAY));
		d_combTreatment.addTreatment(new Drug("Paroxetine", "N062"), new FlexibleDose(new Interval<Double>(3.0, 7.0), SIUnit.MILLIGRAMS_A_DAY));
		d_pm = new CombinationTreatmentPresentation(d_combTreatment);
	}
	
	@Test
	public void testName() {
		assertEquals("Fluoxetine + Paroxetine", d_pm.getName());
		d_pm.getBean().getTreatments().get(0).setDrug(null);
		assertEquals("MISSING + Paroxetine", d_pm.getName());
	}

	@Test
	public void testDrugChangeFiresNameChange() {
		PropertyChangeListener mocklistener = 
			JUnitUtil.mockListener(d_pm, CombinationTreatmentPresentation.PROPERTY_NAME, null, "MISSING + Paroxetine");
		d_pm.addPropertyChangeListener(mocklistener);
		d_pm.getBean().getTreatments().get(0).setDrug(null);
		verify(mocklistener);
	}
	
	@Test
	public void testListChangeFiresNameChange() {
		PropertyChangeListener mocklistener = 
			JUnitUtil.mockListener(d_pm, CombinationTreatmentPresentation.PROPERTY_NAME, null, "Fluoxetine");
		d_pm.addPropertyChangeListener(mocklistener);
		d_pm.getBean().getTreatments().remove(1);
		verify(mocklistener);
	}
	
}
