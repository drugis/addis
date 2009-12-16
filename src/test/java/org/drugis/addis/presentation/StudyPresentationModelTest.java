package org.drugis.addis.presentation;


import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicArm;
import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.DerivedStudyCharacteristic;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.FlexibleDose;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.SIUnit;
import org.drugis.addis.entities.BasicStudyCharacteristic;
import org.drugis.common.Interval;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyPresentationModelTest {
	
	private StudyPresentationModel d_model;
	private BasicStudy d_study;

	@Before
	public void setUp() {
		d_study = new BasicStudy("study", new Indication(0L, "ind"));
		d_model = new StudyPresentationModel(d_study);
	}
	
	@Test
	public void testIsStudyCompleted() {
		d_study.getCharacteristics().put(BasicStudyCharacteristic.STATUS,
				BasicStudyCharacteristic.Status.FINISHED);		
		assertEquals(true, d_model.isStudyFinished());
		
		d_study.getCharacteristics().put(BasicStudyCharacteristic.STATUS,
				BasicStudyCharacteristic.Status.ONGOING);
		assertEquals(false, d_model.isStudyFinished());
	}
	
	@Test
	public void testStudyArmsUpdatesIfChanged() {
		StudyCharacteristicHolder model = d_model.getCharacteristicModel(DerivedStudyCharacteristic.ARMS);
		assertEquals(new Integer(0), model.getValue());
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", null, new Integer(1));
		model.addPropertyChangeListener(mock);
		d_study.addArm(new BasicArm(null, null, 1));

		verify(mock);
		assertEquals(new Integer(1), model.getValue());
	}
	
	@Test
	public void testStudySizeUpdatesIfChanged() {
		StudyCharacteristicHolder model = d_model.getCharacteristicModel(DerivedStudyCharacteristic.STUDYSIZE);
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", null, new Integer(100));
		model.addPropertyChangeListener(mock);
		d_study.addArm(new BasicArm(null, null, 100));

		verify(mock);
		assertEquals(new Integer(100), model.getValue());		
	}
	
	@Test
	public void testDrugsUpdatesIfChanged() {
		StudyCharacteristicHolder model = d_model.getCharacteristicModel(DerivedStudyCharacteristic.DRUGS);
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", null, "[testDrug]");
		model.addPropertyChangeListener(mock);
		d_study.addArm(new BasicArm(new Drug("testDrug","0A"), null, 0));

		verify(mock);
		assertEquals("[testDrug]", model.getValue());	
	}
	
	@Test
	public void testDoseUpdatesIfChanged() {
		StudyCharacteristicHolder model = d_model.getCharacteristicModel(DerivedStudyCharacteristic.DOSING);
		PropertyChangeListener mock = JUnitUtil.mockListener(model, "value", null, DerivedStudyCharacteristic.Dosing.FLEXIBLE);
		model.addPropertyChangeListener(mock);
		d_study.addArm(new BasicArm(null, new FlexibleDose(new Interval<Double>(1d,10d), SIUnit.MILLIGRAMS_A_DAY), 0));
		
		verify(mock);
		assertEquals(DerivedStudyCharacteristic.Dosing.FLEXIBLE, model.getValue());
	}	
}
