package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.SIUnit;
import org.drugis.common.Interval;
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
		
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		group.setDose(dose);
		Drug drug = new Drug("Fluoxetine", "atc");
		group.setDrug(drug);
		assertEquals("Fluoxetine", pres.getLabelModel().getValue());
	}
		
	@Test
	public void testFireLabelChanged() {
		Drug drug = new Drug("Fluoxetine", "atc");
		Drug drug2 = new Drug("Paroxetine", "atc");
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
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
	
	@Test
	public void testFixedDoseModelInitialValues() {
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(
				new BasicPatientGroup(new Drug("", ""), dose, 100));
		assertEquals(dose.getQuantity(), pres.getDoseModel().getMinModel().getValue());
		assertEquals(dose.getQuantity(), pres.getDoseModel().getMaxModel().getValue());
	}
		
	@Test
	public void testFlexibleDoseModelInitialValues() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(25.5, 30.2), SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(
				new BasicPatientGroup(new Drug("", ""), dose, 100));
		assertEquals(dose.getFlexibleDose().getLowerBound(), pres.getDoseModel().getMinModel().getValue());
		assertEquals(dose.getFlexibleDose().getUpperBound(), pres.getDoseModel().getMaxModel().getValue());
	}
	
	@Test
	public void testFixedToFlexible() {
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(
				new BasicPatientGroup(new Drug("", ""), dose, 100));
		pres.getDoseModel().getMaxModel().setValue(dose.getQuantity() + 2);
		assertTrue(pres.getBean().getDose() instanceof FlexibleDose);
	}
	
	@Test
	public void testFlexibleToFixed() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(25.5, 30.2), SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(
				new BasicPatientGroup(new Drug("", ""), dose, 100));
		pres.getDoseModel().getMaxModel().setValue(dose.getFlexibleDose().getLowerBound());
		assertTrue(pres.getBean().getDose() instanceof FixedDose);
	}
	
	@Test
	public void testSetMaxLowerThanMinDose() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(10.0,20.0), SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(
				new BasicPatientGroup(new Drug("", ""), dose, 100));
		pres.getDoseModel().getMaxModel().setValue(8d);
		assertEquals(8d, pres.getDoseModel().getMaxModel().doubleValue(), 0.001);
		assertEquals(8d, pres.getDoseModel().getMinModel().doubleValue(), 0.001);
		assertTrue(pres.getBean().getDose() instanceof FixedDose);
	}
	
	@Test
	public void testSetMinHigherThanMaxDose() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(10.0,20.0), SIUnit.MILLIGRAMS_A_DAY);
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(
				new BasicPatientGroup(new Drug("", ""), dose, 100));
		pres.getDoseModel().getMinModel().setValue(25d);
		assertEquals(25d, pres.getDoseModel().getMaxModel().doubleValue(), 0.001);
		assertEquals(25d, pres.getDoseModel().getMinModel().doubleValue(), 0.001);
		assertTrue(pres.getBean().getDose() instanceof FixedDose);
	}
	
	@Test
	public void testSetUnit() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(10.0,20.0), null);
		BasicPatientGroupPresentation pres = new BasicPatientGroupPresentation(
				new BasicPatientGroup(new Drug("", ""), dose, 100));
		pres.getDoseModel().getUnitModel().setValue(SIUnit.MILLIGRAMS_A_DAY);
		assertEquals(SIUnit.MILLIGRAMS_A_DAY, pres.getBean().getDose().getUnit());
	}
}
