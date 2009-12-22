package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.CategoricalVariable;
import org.drugis.addis.entities.ContinuousMeasurement;
import org.drugis.addis.entities.ContinuousVariable;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.SIUnit;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class BasicArmPresentationTest {
	private Arm d_pg;
	private PresentationModelFactory d_pmf;
	private BasicArmPresentation d_pres;

	@Before
	public void setUp() {
		d_pmf = new PresentationModelFactory(new DomainImpl());
		d_pg = new Arm(null, null, 0);
		d_pres = new BasicArmPresentation(d_pg, d_pmf);
	}
	
	
	@Test
	public void testGetLabel() {
		Arm group = d_pg;
		assertEquals("INCOMPLETE", d_pres.getLabelModel().getValue());
		
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		group.setDose(dose);
		Drug drug = new Drug("Fluoxetine", "atc");
		group.setDrug(drug);
		assertEquals("Fluoxetine", d_pres.getLabelModel().getValue());
	}
		
	@Test
	public void testFireLabelChanged() {
		Drug drug = new Drug("Fluoxetine", "atc");
		Drug drug2 = new Drug("Paroxetine", "atc");
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		d_pg.setDrug(drug);
		d_pg.setDose(dose);
		
		AbstractValueModel lm = d_pres.getLabelModel();
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
		d_pg.setDose(dose);
		assertEquals(dose.getQuantity(), d_pres.getDoseModel().getMinModel().getValue());
		assertEquals(dose.getQuantity(), d_pres.getDoseModel().getMaxModel().getValue());
	}
		
	@Test
	public void testFlexibleDoseModelInitialValues() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(25.5, 30.2), SIUnit.MILLIGRAMS_A_DAY);
		d_pg.setDose(dose);
		assertEquals(dose.getFlexibleDose().getLowerBound(), d_pres.getDoseModel().getMinModel().getValue());
		assertEquals(dose.getFlexibleDose().getUpperBound(), d_pres.getDoseModel().getMaxModel().getValue());
	}
	
	@Test
	public void testFixedToFlexible() {
		FixedDose dose = new FixedDose(25.5, SIUnit.MILLIGRAMS_A_DAY);
		d_pg.setDose(dose);
		d_pres.getDoseModel().getMaxModel().setValue(dose.getQuantity() + 2);
		assertTrue(d_pres.getBean().getDose() instanceof FlexibleDose);
	}
	
	@Test
	public void testFlexibleToFixed() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(25.5, 30.2), SIUnit.MILLIGRAMS_A_DAY);
		d_pg.setDose(dose);
		d_pres.getDoseModel().getMaxModel().setValue(dose.getFlexibleDose().getLowerBound());
		assertTrue(d_pres.getBean().getDose() instanceof FixedDose);
	}
	
	@Test
	public void testSetMaxLowerThanMinDose() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(10.0,20.0), SIUnit.MILLIGRAMS_A_DAY);
		d_pg.setDose(dose);
		d_pres.getDoseModel().getMaxModel().setValue(8d);
		assertEquals(8d, d_pres.getDoseModel().getMaxModel().doubleValue(), 0.001);
		assertEquals(8d, d_pres.getDoseModel().getMinModel().doubleValue(), 0.001);
		assertTrue(d_pres.getBean().getDose() instanceof FixedDose);
	}
	
	@Test
	public void testSetMinHigherThanMaxDose() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(10.0,20.0), SIUnit.MILLIGRAMS_A_DAY);
		d_pg.setDose(dose);
		d_pres.getDoseModel().getMinModel().setValue(25d);
		assertEquals(25d, d_pres.getDoseModel().getMaxModel().doubleValue(), 0.001);
		assertEquals(25d, d_pres.getDoseModel().getMinModel().doubleValue(), 0.001);
		assertTrue(d_pres.getBean().getDose() instanceof FixedDose);
	}
	
	@Test
	public void testSetUnit() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(10.0,20.0), null);
		d_pg.setDose(dose);
		d_pres.getDoseModel().getUnitModel().setValue(SIUnit.MILLIGRAMS_A_DAY);
		assertEquals(SIUnit.MILLIGRAMS_A_DAY, d_pres.getBean().getDose().getUnit());
	}
	
	@Test
	public void testGetTooltip() {
		ContinuousVariable age = new ContinuousVariable("Age");
		d_pg.setPopulationCharacteristic(age, age.buildMeasurement());
		CategoricalVariable gender = new CategoricalVariable("Gender", new String[]{"Male", "Female"});
		d_pg.setPopulationCharacteristic(gender, gender.buildMeasurement());
		assertEquals("<html>Age: " + age.buildMeasurement().toString() + "<br>" +
				"Gender: Male = 0 / Female = 0<br></html>", d_pres.getCharacteristicTooltip());
	}
	
	@Test
	public void testGetCharacteristicModel() {
		ContinuousVariable age = new ContinuousVariable("Age");
		ContinuousMeasurement m = age.buildMeasurement();
		d_pg.setPopulationCharacteristic(age, m);
		assertEquals(d_pmf.getLabeledModel(m), d_pres.getCharacteristicModel(age));
		assertEquals(null, d_pres.getCharacteristicModel(new ContinuousVariable("X")));
	}
}
