/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
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

import static org.drugis.addis.entities.AssertEntityEquals.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.drugis.addis.entities.DoseUnit;
import org.drugis.addis.entities.FixedDose;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.DrugTreatment;
import org.drugis.common.Interval;
import org.junit.Before;
import org.junit.Test;

public class DrugTreatmentPresentationTest {
	private DrugTreatment d_drugTreatment;
	private DrugTreatmentPresentation d_pm;

	@Before
	public void setUp() {
		d_drugTreatment = new DrugTreatment(null, null);
		d_pm = new DrugTreatmentPresentation(d_drugTreatment);
	}
	
	@Test
	public void testFixedDoseModelInitialValues() {
		FixedDose dose = new FixedDose(25.5, DoseUnit.createMilliGramsPerDay());
		d_drugTreatment.setDose(dose);
		assertEquals(dose.getQuantity(), d_pm.getDoseModel().getMinModel().getValue());
		assertEquals(dose.getQuantity(), d_pm.getDoseModel().getMaxModel().getValue());
	}
		
	@Test
	public void testFlexibleDoseModelInitialValues() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(25.5, 30.2), DoseUnit.createMilliGramsPerDay());
		d_drugTreatment.setDose(dose);
		assertEquals(dose.getFlexibleDose().getLowerBound(), d_pm.getDoseModel().getMinModel().getValue());
		assertEquals(dose.getFlexibleDose().getUpperBound(), d_pm.getDoseModel().getMaxModel().getValue());
	}
	
	@Test
	public void testFixedToFlexible() {
		FixedDose dose = new FixedDose(25.5, DoseUnit.createMilliGramsPerDay());
		d_drugTreatment.setDose(dose);
		d_pm.getDoseModel().getMaxModel().setValue(dose.getQuantity() + 2);
		assertTrue(d_pm.getBean().getDose() instanceof FlexibleDose);
	}
	
	@Test
	public void testFlexibleToFixed() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(25.5, 30.2), DoseUnit.createMilliGramsPerDay());
		d_drugTreatment.setDose(dose);
		d_pm.getDoseModel().getMaxModel().setValue(dose.getFlexibleDose().getLowerBound());
		assertTrue(d_pm.getBean().getDose() instanceof FixedDose);
	}
	
	@Test
	public void testSetMaxLowerThanMinDose() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(10.0, 20.0), DoseUnit.createMilliGramsPerDay());
		d_drugTreatment.setDose(dose);
		d_pm.getDoseModel().getMaxModel().setValue(8d);
		assertEquals(8d, d_pm.getDoseModel().getMaxModel().doubleValue(), 0.001);
		assertEquals(8d, d_pm.getDoseModel().getMinModel().doubleValue(), 0.001);
		assertTrue(d_pm.getBean().getDose() instanceof FixedDose);
	}
	
	@Test
	public void testSetMinHigherThanMaxDose() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(10.0, 20.0), DoseUnit.createMilliGramsPerDay());
		d_drugTreatment.setDose(dose);
		d_pm.getDoseModel().getMinModel().setValue(25d);
		assertEquals(25d, d_pm.getDoseModel().getMaxModel().doubleValue(), 0.001);
		assertEquals(25d, d_pm.getDoseModel().getMinModel().doubleValue(), 0.001);
		assertTrue(d_pm.getBean().getDose() instanceof FixedDose);
	}

	@Test
	public void testSetDoseUnit() {
		FlexibleDose dose = new FlexibleDose(new Interval<Double>(10.0,20.0), DoseUnit.createMilliGramsPerDay());
		d_drugTreatment.setDose(dose);
		assertEntityEquals(d_pm.getDoseModel().getDoseUnitPresentation().getBean(), d_drugTreatment.getDose().getDoseUnit());
	}
}
