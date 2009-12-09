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

import com.jgoodies.binding.value.AbstractValueModel;

public class BasicPatientGroupPresentationTest {
	private BasicPatientGroup d_pg;

	@Before
	public void setUp() {
		d_pg = new BasicPatientGroup(null, null, 0);
	}
	
	@Test
	public void testGetLabel() {
		BasicPatientGroup group = d_pg;
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(group);
		assertEquals("INCOMPLETE", pres.getLabelModel().getValue());
		
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		group.setDose(dose);
		Drug drug = new Drug("Fluoxetine", "atc");
		group.setDrug(drug);
		assertEquals("Fluoxetine", pres.getLabelModel().getValue());
	}
		
	@Test
	public void testFireLabelChanged() {
		Drug drug = new Drug("Fluoxetine", "atc");
		Drug drug2 = new Drug("Paroxetine", "atc");
		Dose dose = new Dose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		d_pg.setDrug(drug);
		d_pg.setDose(dose);
		
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(d_pg);
		AbstractValueModel lm = pres.getLabelModel();
		String expect = (String) lm.getValue();
		
		d_pg.setDrug(drug2);
		PropertyChangeListener l = JUnitUtil.mockListener(lm, "value", lm.getValue(), expect);
		lm.addPropertyChangeListener(l);
		d_pg.setDrug(drug);
		verify(l);
	}
}
