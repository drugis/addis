package org.drugis.addis.presentation;

import static org.drugis.common.JUnitUtil.assertAllAndOnly;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.addis.entities.Study;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class DomainStudyListHolderTest {
	private DomainImpl d_domain;
	private DomainStudyListHolder d_pm;
	private TypedHolder<Indication> d_indication;
	private TypedHolder<OutcomeMeasure> d_outcome;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		
		d_indication = new TypedHolder<Indication>(ExampleData.buildIndicationDepression());
		d_outcome = new TypedHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd());
		d_pm = new DomainStudyListHolder(d_domain,
				d_indication,
				d_outcome);
	}
	
	@Test
	public void testValue() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyBennie());
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(ExampleData.buildStudyMultipleArmsperDrug());
		
		assertAllAndOnly(studies, d_pm.getValue());
	}
	
	@Test
	public void testEndpointChangeTriggers() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyBennie());
		studies.add(ExampleData.buildStudyChouinard());
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm, "value", null, 
				studies);
		d_pm.addValueChangeListener(mock);
		
		d_outcome.setValue(ExampleData.buildEndpointCgi());
		assertAllAndOnly(studies, d_pm.getValue());
		verify(mock);
	}

	@Test
	public void testIndicationChangeTriggers() {
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm, "value", null, 
				Collections.<Study>emptyList());
		d_pm.addValueChangeListener(mock);
		
		d_indication.setValue(ExampleData.buildIndicationChronicHeartFailure());
		assertAllAndOnly(Collections.<Study>emptyList(), d_pm.getValue());
		verify(mock);
	}

	@Test
	public void testStudiesChangeTriggers() {
		List<Study> studies = new ArrayList<Study>();
		studies.add(ExampleData.buildStudyBennie());
		studies.add(ExampleData.buildStudyChouinard());
		studies.add(ExampleData.buildStudyDeWilde());
		studies.add(ExampleData.buildStudyMultipleArmsperDrug());
		studies.add(ExampleData.buildStudyAdditionalThreeArm());
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm, "value", null, studies);
		d_pm.addValueChangeListener(mock);
		
		d_domain.addStudy(ExampleData.buildStudyAdditionalThreeArm());
		assertAllAndOnly(studies, d_pm.getValue());
		verify(mock);
	}
}