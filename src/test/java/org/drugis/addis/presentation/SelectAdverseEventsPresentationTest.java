package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Variable;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("serial")
public class SelectAdverseEventsPresentationTest {
	private AdverseEvent d_ade1 = new AdverseEvent("ADE 1", Variable.Type.RATE);
	private AdverseEvent d_ade2 = new AdverseEvent("ADE 2", Variable.Type.RATE);
	private AdverseEvent d_ade3 = new AdverseEvent("ADE 3", Variable.Type.RATE);
	private ListHolder<AdverseEvent> d_list;
	private SelectAdverseEventsPresentation d_pm;
	
	@Before
	public void setUp() {
		d_list = new AbstractListHolder<AdverseEvent>() {
			@Override
			public List<AdverseEvent> getValue() {
				List<AdverseEvent> l = new ArrayList<AdverseEvent>();
				l.add(d_ade1);
				l.add(d_ade2);
				return l;
			}
		};
		
		d_pm = new SelectAdverseEventsPresentation(d_list, null);
	}
	
	@Test
	public void testGetTypeName() {
		assertEquals("Adverse Event", d_pm.getTypeName());
	}
	
	@Test
	public void testHasAddOptionDialog() {
		assertTrue(d_pm.hasAddOptionDialog());
	}
	
	@Test
	public void testGetTitle() {
		assertEquals("Select Adverse Events", d_pm.getTitle());
		assertEquals("Please select the appropriate adverse events.", d_pm.getDescription());
	}
	
	@Test
	public void testGetOptions() {
		assertEquals(d_list.getValue(), d_pm.getOptions().getValue());
		d_list.getValue().add(d_ade3);
		assertEquals(d_list.getValue(), d_pm.getOptions().getValue());
	}
	
	@Test
	public void testAddSlot() {
		assertEquals(0, d_pm.countSlots());
		d_pm.addSlot();
		assertEquals(1, d_pm.countSlots());
	}
	
	@Test
	public void testGetSlot() {
		d_pm.addSlot();
		d_pm.getSlot(0).setValue(d_ade2);
		assertEquals(d_ade2, d_pm.getSlot(0).getValue());
	}
	
	@Test
	public void testRemoveSlot() {
		d_pm.addSlot();
		assertEquals(1, d_pm.countSlots());
		d_pm.removeSlot(0);
		assertEquals(0, d_pm.countSlots());
		
		d_pm.addSlot();
		d_pm.getSlot(0).setValue(d_ade1);
		d_pm.addSlot();
		d_pm.getSlot(1).setValue(d_ade2);
		d_pm.removeSlot(0);
		assertEquals(d_pm.getSlot(0).getValue(), d_ade2);
	}
	
	@Test
	public void testAddSlotsEnabledModel() {
		assertEquals(d_pm.getAddSlotsEnabledModel().getValue(), Boolean.TRUE);
		d_pm.addSlot();
		d_pm.addSlot();
		assertEquals(d_pm.getAddSlotsEnabledModel().getValue(), Boolean.TRUE);
		d_pm.addSlot();
		assertEquals(d_pm.getAddSlotsEnabledModel().getValue(), Boolean.TRUE);
	}
	
	@Test
	public void testInputCompleteModel() {
		assertEquals(Boolean.TRUE, d_pm.getInputCompleteModel().getValue());
		
		PropertyChangeListener mock = JUnitUtil.mockListener(d_pm.getInputCompleteModel(), "value",
				Boolean.TRUE, Boolean.FALSE);
		d_pm.getInputCompleteModel().addValueChangeListener(mock);
		d_pm.addSlot();
		assertEquals(Boolean.FALSE, d_pm.getInputCompleteModel().getValue());
		verify(mock);
		
		mock = JUnitUtil.mockListener(d_pm.getInputCompleteModel(), "value",
				Boolean.FALSE, Boolean.TRUE);
		d_pm.getInputCompleteModel().addValueChangeListener(mock);
		d_pm.getSlot(0).setValue(d_ade2);
		assertEquals(Boolean.TRUE, d_pm.getInputCompleteModel().getValue());
		verify(mock);
	}
	
	@Test
	public void testSelectSameValueTwiceRemovesFromFirst() {
		d_pm.addSlot();
		d_pm.addSlot();
		d_pm.getSlot(1).setValue(d_ade1);
		assertEquals(d_ade1, d_pm.getSlot(1).getValue());
		d_pm.getSlot(0).setValue(d_ade1);
		assertEquals(d_ade1, d_pm.getSlot(0).getValue());
		assertEquals(null, d_pm.getSlot(1).getValue());
	}
}
