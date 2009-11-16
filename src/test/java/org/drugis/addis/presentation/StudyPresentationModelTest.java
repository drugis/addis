package org.drugis.addis.presentation;

import static org.junit.Assert.*;

import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.junit.Before;
import org.junit.Test;

public class StudyPresentationModelTest {
	
	private StudyPresentationModel d_model;
	private Study d_study;

	@Before
	public void setUp() {
		d_study = new BasicStudy("study", new Indication(0L, "ind"));
		d_model = new StudyPresentationModel(d_study);
	}
	
	@Test
	public void testIsStudyCompleted() {
		d_study.getCharacteristics().put(StudyCharacteristic.STATUS,
				StudyCharacteristic.Status.FINISHED);		
		assertEquals(true, d_model.isStudyFinished());
		
		d_study.getCharacteristics().put(StudyCharacteristic.STATUS,
				StudyCharacteristic.Status.ONGOING);
		assertEquals(false, d_model.isStudyFinished());
	}
	
}
