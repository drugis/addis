package org.drugis.addis.util;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.ListIterator;
import java.util.TreeSet;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.junit.Before;
import org.junit.Test;

public class SortedSetModelTest {
	private SortedSetModel<String> d_filledList;

	@Before
	public void setUp() {
		resetFilledList();
	}

	private void resetFilledList() {
		d_filledList = new SortedSetModel<String>(Arrays.asList("Gert", "Margreth", "Daan"));
	}
	
	@Test
	public void testConstruct() {
		SortedSetModel<String> ssm = new SortedSetModel<String>();
		assertEquals(0, ssm.getSize());
		assertEquals(0, ssm.size());
		
		SortedSetModel<String> ssm2 = new SortedSetModel<String>(Collections.singleton("Foo"));
		assertEquals(1, ssm2.getSize());
		assertEquals(1, ssm2.size());
		assertEquals("Foo", ssm2.get(0));

		SortedSetModel<String> ssm3 = new SortedSetModel<String>(Arrays.asList("Foo", "Foo", "Bar"));
		assertEquals(2, ssm3.getSize());
		assertEquals(2, ssm3.size());
		assertEquals("Bar", ssm3.get(0));
		assertEquals("Foo", ssm3.get(1));
		assertEquals("Foo", ssm3.getElementAt(1));
	}

	@Test
	public void testRemoving() {
		assertTrue(d_filledList.remove("Daan"));
		assertEquals(Arrays.asList("Gert", "Margreth"), d_filledList);
		assertEquals("Gert", d_filledList.remove(0));
		assertEquals(Arrays.asList("Margreth"), d_filledList);
		
		resetFilledList();
		d_filledList.clear();
		assertEquals(Collections.emptyList(), d_filledList);

		resetFilledList();
		ListIterator<String> it = d_filledList.listIterator();
		int i = 0;
		while (it.hasNext()) {
			it.next();
			if (i % 2 == 0) {
				it.remove();
			}
			++i;
		}
		assertEquals(Collections.singletonList("Gert"), d_filledList);
	}

	@Test
	public void testAdding() {
		SortedSetModel<String> ssm = new SortedSetModel<String>();
		ssm.add(0, "Gert");
		assertEquals(Arrays.asList("Gert"), ssm);
		
		ssm.add(0, "Margreth");
		assertEquals(Arrays.asList("Gert", "Margreth"), ssm);
	}
	
	@Test
	public void testAddingEvent() {
		ListDataListener mockListener = createStrictMock(ListDataListener.class);
		mockListener.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_filledList, ListDataEvent.INTERVAL_ADDED, 1, 1)));
		replay(mockListener);
		d_filledList.addListDataListener(mockListener);
		d_filledList.add("Douwe");
		verify(mockListener);
	}
	
	@Test
	public void testDeletingEvent() {
		d_filledList.add("Douwe");
		ListDataListener mockListener = createStrictMock(ListDataListener.class);
		mockListener.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_filledList, ListDataEvent.INTERVAL_REMOVED, 1, 1)));
		replay(mockListener);
		d_filledList.addListDataListener(mockListener);
		d_filledList.remove("Douwe");
		verify(mockListener);
	}

	@Test
	public void testGetSet() {
		assertEquals(new TreeSet<String>(d_filledList), d_filledList.getSet());
	}

}
