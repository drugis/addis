package org.drugis.addis.presentation;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.entities.OutcomeMeasure;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;
import static org.easymock.EasyMock.*;

public class SelectableStudyGraphModelTest {

	private DomainImpl d_domain;
	private ArrayList<Drug> d_drugs;
	private SelectableStudyGraphModel d_pm;
	private ListHolder<Drug> d_drugListHolder;

	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_drugs = new ArrayList<Drug>();
		d_drugs.add(ExampleData.buildDrugFluoxetine());
		d_drugs.add(ExampleData.buildDrugParoxetine());
		d_drugs.add(ExampleData.buildDrugSertraline());
		d_drugListHolder = new DefaultListHolder<Drug>(d_drugs);
		d_pm = new SelectableStudyGraphModel(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<OutcomeMeasure>(ExampleData.buildEndpointHamd()),
				d_drugListHolder, d_domain);
	}
	
	@Test
	public void testGetSelectedDrugsModel() {
		ListHolder<Drug> selDrugs = d_pm.getSelectedDrugsModel();
		List<Drug> list = Collections.singletonList(ExampleData.buildDrugFluoxetine());
		
		PropertyChangeListener mock = JUnitUtil.mockListener(selDrugs, "value", selDrugs.getValue(), list);
		selDrugs.addValueChangeListener(mock);
		
		d_drugListHolder.setValue(list);
		verify(mock);
		
	}
}
