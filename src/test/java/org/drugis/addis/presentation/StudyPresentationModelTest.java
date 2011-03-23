/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright (C) 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright (C) 2010 Gert van Valkenhoef, Tommi Tervonen, 
 * Tijs Zwinkels, Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, 
 * Ahmad Kamal, Daniel Reid.
 * Copyright (C) 2011 Gert van Valkenhoef, Ahmad Kamal, 
 * Daniel Reid, Florin Schimbinschi.
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


import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;
import java.util.Collections;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.addis.entities.ContinuousPopulationCharacteristic;
import org.drugis.addis.entities.DerivedStudyCharacteristic;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.PopulationCharacteristic;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.Variable;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyPresentationModelTest {
	
	private StudyPresentation d_model;
	private Study d_study;
	private PresentationModelFactory d_pmf;

	@Before
	public void setUp() {
		d_study = new Study("study", new Indication(0L, "ind"));
		d_pmf = new PresentationModelFactory(new DomainImpl());
		d_model = new StudyPresentation(d_study, d_pmf);
	}
	
	@Test
	public void testIsStudyCompleted() {
		d_study.setCharacteristic(BasicStudyCharacteristic.STATUS,
				BasicStudyCharacteristic.Status.COMPLETED);		
		assertEquals(true, d_model.isStudyFinished());
		
		d_study.setCharacteristic(BasicStudyCharacteristic.STATUS,
				BasicStudyCharacteristic.Status.ACTIVE);
		assertEquals(false, d_model.isStudyFinished());
	}
	
	@Test
	public void testStudyArmsUpdatesIfChanged() {
		StudyCharacteristicHolder model = d_model.getCharacteristicModel(DerivedStudyCharacteristic.ARMS);
		assertEquals(new Integer(0), model.getValue());
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", null, new Integer(1));
		model.addPropertyChangeListener(mock);
		d_study.addArm(new Arm(null, null, 1));

		verify(mock);
		assertEquals(new Integer(1), model.getValue());
	}
	
	@Test
	public void testStudySizeUpdatesIfChanged() {
		StudyCharacteristicHolder model = d_model.getCharacteristicModel(DerivedStudyCharacteristic.STUDYSIZE);
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", null, new Integer(100));
		model.addPropertyChangeListener(mock);
		d_study.addArm(new Arm(null, null, 100));

		verify(mock);
		assertEquals(new Integer(100), model.getValue());		
	}
	
	@Test
	public void testDrugsUpdatesIfChanged() {
		Drug d = new Drug("testDrug","0A");
		StudyCharacteristicHolder model = d_model.getCharacteristicModel(DerivedStudyCharacteristic.DRUGS);
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", null, Collections.singleton(d));
		model.addPropertyChangeListener(mock);
		
		d_study.addArm(new Arm(d, null, 0));

		verify(mock);
		assertEquals(Collections.singleton(d), model.getValue());	
	}
	
	@Test
	public void testDoseUpdatesIfChanged() {
		StudyCharacteristicHolder model = d_model.getCharacteristicModel(DerivedStudyCharacteristic.DOSING);
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", null, DerivedStudyCharacteristic.Dosing.FLEXIBLE);
		model.addPropertyChangeListener(mock);
		d_study.addArm(new Arm(null, new FlexibleDose(new Interval<Double>(1d,10d), SIUnit.MILLIGRAMS_A_DAY), 0));
		
		verify(mock);
		assertEquals(DerivedStudyCharacteristic.Dosing.FLEXIBLE, model.getValue());
	}
	
	@Test
	public void testGetArmCount() {
		assertEquals(d_study.getArms().size(), d_model.getArmCount());
		d_study.addArm(new Arm(new Drug("X", "Y"), null, 0));
		assertEquals(d_study.getArms().size(), d_model.getArmCount());
	}
	
	@Test
	public void testGetArms() {
		Arm arm = new Arm(new Drug("X", "Y"), null, 0);
		d_study.addArm(arm);
		assertEquals(Collections.singletonList(d_pmf.getModel(arm)), d_model.getArms());
	}
	
	@Test
	public void testGetPopulationCharacteristicCount() {
		Arm arm1 = new Arm(new Drug("X", "Y"), null, 0);
		d_study.addArm(arm1);
		Arm arm2 = new Arm(new Drug("X", "Y"), null, 0);
		d_study.addArm(arm2);
		ContinuousPopulationCharacteristic age = new ContinuousPopulationCharacteristic("Age");
		assertEquals(0, d_model.getPopulationCharacteristicCount());
		d_study.setPopulationCharacteristics(Collections.<PopulationCharacteristic>singletonList(age));
		assertEquals(1, d_model.getPopulationCharacteristicCount());
	}
	
	@Test
	public void testGetPopulationCharacteristicsOverall() {
		ContinuousPopulationCharacteristic age = new ContinuousPopulationCharacteristic("Age");
		d_study.setPopulationCharacteristics(Collections.<PopulationCharacteristic>singletonList(age));
		assertEquals(Collections.singletonList(age), d_model.getPopulationCharacteristics());
	}
	
	@Test
	public void testGetEndpoints() {
		Endpoint ep = new Endpoint("ep", Variable.Type.RATE);
		d_study.addEndpoint(ep);
		AdverseEvent ade = new AdverseEvent("ade1", Variable.Type.RATE);
		d_study.addAdverseEvent(ade);
		
		assertEquals(Collections.singletonList(ep), d_model.getEndpoints());
	}
	
	@Test
	public void testGetAdes() {
		Endpoint ep = new Endpoint("ep", Variable.Type.RATE);
		d_study.addEndpoint(ep);
		AdverseEvent ade = new AdverseEvent("ade1", Variable.Type.RATE);
		d_study.addAdverseEvent(ade);
		
		assertEquals(Collections.singletonList(ade), d_model.getAdverseEvents());
	}
}
