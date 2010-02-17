package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.*;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Study;
import org.drugis.addis.presentation.PresentationModelFactory;
import org.drugis.addis.presentation.SelectableStudyListPresentationModel;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

import static org.easymock.EasyMock.*;

public class NetworkMetaAnalysisWizardPMTest {

	private Domain d_domain;
	private NetworkMetaAnalysisWizardPM d_pm;
	
	@Before
	public void setUp() {
		d_domain = new DomainImpl();
		ExampleData.initDefaultData(d_domain);
		d_pm = new NetworkMetaAnalysisWizardPM(d_domain, new PresentationModelFactory(d_domain));
	}
	
	@Test
	public void testDrugsSelectedCompleteListener() {
		ValueModel completeModel = d_pm.getConnectedDrugsSelectedModel();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		assertTrue((Boolean)completeModel.getValue());
		
		ArrayList<Drug> newList = new ArrayList<Drug>();
		newList.add(ExampleData.buildDrugSertraline());
		d_pm.getSelectedDrugsModel().setValue(newList);
		assertFalse((Boolean)completeModel.getValue());
		
		newList = new ArrayList<Drug>(newList);
		newList.add(ExampleData.buildDrugParoxetine());
		d_pm.getSelectedDrugsModel().setValue(newList);
		assertFalse((Boolean)completeModel.getValue());
		
		newList = new ArrayList<Drug>(newList);		
		newList.add(ExampleData.buildDrugFluoxetine());
		d_pm.getSelectedDrugsModel().setValue(newList);
		assertTrue((Boolean)completeModel.getValue());		
	}
	
	@Test
	public void testStudyListModel() {
		SelectableStudyListPresentationModel listModel = d_pm.getStudyListModel();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		
		ArrayList<Study> newList = new ArrayList<Study>();
		newList.addAll(d_pm.getStudiesEndpointAndIndication());
		assertEquals(newList, listModel.getIncludedStudies().getValue());

		ArrayList<Drug> selectionList = new ArrayList<Drug>();
		selectionList.add(ExampleData.buildDrugSertraline());
		selectionList.add(ExampleData.buildDrugParoxetine());
		
		ArrayList<Study> expected = new ArrayList<Study>();
		PropertyChangeListener mock = JUnitUtil.mockListener(listModel.getIncludedStudies(), "value", newList, expected);
		listModel.getIncludedStudies().addValueChangeListener(mock);
		d_pm.getSelectedDrugsModel().setValue(selectionList);
		
		verify(mock);
	}
	
	@Test
	public void testStudyListModelAdds() {
		SelectableStudyListPresentationModel listModel = d_pm.getStudyListModel();
		
		d_pm.getIndicationModel().setValue(ExampleData.buildIndicationDepression());
		d_pm.getEndpointModel().setValue(ExampleData.buildEndpointHamd());
		ArrayList<Study> allStudiesList = new ArrayList<Study>(d_pm.getStudiesEndpointAndIndication());
		
		ArrayList<Drug> selectionList = new ArrayList<Drug>();
		selectionList.add(ExampleData.buildDrugSertraline());
		selectionList.add(ExampleData.buildDrugParoxetine());
		
		d_pm.getSelectedDrugsModel().setValue(new ArrayList<Drug>(selectionList));
		
		PropertyChangeListener mock = JUnitUtil.mockListener(listModel.getIncludedStudies(), 
				"value", new ArrayList<Study>(), allStudiesList);
		listModel.getIncludedStudies().addValueChangeListener(mock);

		selectionList.add(ExampleData.buildDrugFluoxetine());	
		
		d_pm.getSelectedDrugsModel().setValue(selectionList);		
		
		verify(mock);
	}
	
	
}
