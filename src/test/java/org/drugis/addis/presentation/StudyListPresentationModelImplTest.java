package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.BasicStudy;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class StudyListPresentationModelImplTest {
	
	private List<Study> d_studies;
	private StudyListPresentationModel d_model;

	@Before
	public void setUp() {
		d_studies = new ArrayList<Study>();
		d_studies.add(new BasicStudy("s1", new Indication(0L, "")));
		d_studies.add(new BasicStudy("s2", new Indication(1L, "")));
		d_model = new StudyListPresentationModelImpl(d_studies); 
	}
	
	@Test
	public void testGetIncludedStudies(){
		assertEquals(d_studies, d_model.getIncludedStudies());
	}
	
	@Test
	public void testGetCharacteristicVisibleModel() {
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			AbstractValueModel v = d_model.getCharacteristicVisibleModel(c);
			assertTrue(v.booleanValue());
		}
	}
}
