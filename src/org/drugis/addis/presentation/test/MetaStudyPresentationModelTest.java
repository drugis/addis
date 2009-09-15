package org.drugis.addis.presentation.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.MetaAnalysis;
import org.drugis.addis.entities.MetaStudy;
import org.drugis.addis.entities.Study;
import org.drugis.addis.entities.StudyCharacteristic;
import org.drugis.addis.entities.test.TestData;
import org.drugis.addis.presentation.MetaStudyPresentationModel;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class MetaStudyPresentationModelTest {
	private MetaStudy d_study;
	private Domain d_domain;
	private MetaStudyPresentationModel d_presentationModel;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		TestData.initDefaultData(d_domain);
		MetaAnalysis ma = new MetaAnalysis(TestData.buildEndpointHamd(), new ArrayList<Study>(d_domain.getStudies()));
		d_study = new MetaStudy("Meta", ma);
		d_presentationModel = new MetaStudyPresentationModel(d_study);
	}
	
	@Test
	public void testGetIncludedStudies() {
		assertEquals(d_study.getAnalysis().getStudies(), d_presentationModel.getIncludedStudies());
	}
	
	@Test
	public void testGetCharacteristicVisibleModel() {
		for (StudyCharacteristic c : StudyCharacteristic.values()) {
			AbstractValueModel m = d_presentationModel.getCharacteristicVisibleModel(c);
			JUnitUtil.testSetter(m, "value", Boolean.TRUE, Boolean.FALSE);
			
			m = d_presentationModel.getCharacteristicVisibleModel(c);
			assertEquals(Boolean.FALSE, m.getValue());
		}
	}
}
