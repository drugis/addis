package org.drugis.addis.presentation.wizard;

import static org.easymock.EasyMock.verify;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.presentation.DefaultListHolder;
import org.drugis.addis.presentation.ListHolder;
import org.drugis.addis.presentation.StudyGraphPresentation.Vertex;
import org.drugis.common.JUnitUtil;
import org.jgraph.event.GraphSelectionEvent;
import org.junit.Before;
import org.junit.Test;

public class SelectedDrugsGraphListenerTest {

	private ListHolder<Drug> d_drugs;
	private SelectedDrugsGraphListener d_list;

	@Before
	public void setUp() {
		d_drugs = new DefaultListHolder<Drug>(new ArrayList<Drug>());
		d_list = new SelectedDrugsGraphListener(d_drugs);
	}
	
	@Test
	public void testSelectionChangeAddDrug() {
		Drug drug = new Drug("drug", "2452");
		Vertex vert = new Vertex(drug, 8);
		Vertex[] cells = new Vertex[]{vert}; 
		boolean[] bools = new boolean[]{true};
		GraphSelectionEvent ev = new GraphSelectionEvent("haa", cells, bools);
		
		List<Drug> expVal = Collections.singletonList(drug);
		PropertyChangeListener mock = JUnitUtil.mockListener(d_drugs, "value", new ArrayList<Drug>(), expVal);
		d_drugs.addValueChangeListener(mock);
		
		d_list.valueChanged(ev);
		
		verify(mock);
	}
	
	@Test
	public void testSelectionChangeRemoveDrug() {
		Drug drug = new Drug("drug", "2452");
		List<Drug> oldVal = Collections.singletonList(drug);		
		d_drugs.setValue(oldVal);
		Vertex vert = new Vertex(drug, 8);		
		Vertex[] cells = new Vertex[]{vert}; 
		boolean[] bools = new boolean[]{true};
		GraphSelectionEvent ev = new GraphSelectionEvent("haa", cells, bools);
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_drugs, "value", d_drugs.getValue(), new ArrayList<Drug>());
		d_drugs.addValueChangeListener(mock);
		
		d_list.valueChanged(ev);
		
		verify(mock);
	}
	
}
