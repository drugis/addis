package org.drugis.addis.util;

import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.util.FilteredObservableList.Filter;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;

public class FilteredObservableListTest {
	private ArrayListModel<String> d_inner;
	private Filter<String> d_filter;
	private FilteredObservableList<String> d_outer;

	@Before
	public void setUp() {
		d_inner = new ArrayListModel<String>(Arrays.asList("Gert", "Daan", "Jan", "Klaas"));
		d_filter = new FilteredObservableList.Filter<String>() {
			public boolean accept(String str) {
				return !str.contains("aa");
			}
		};
		d_outer = new FilteredObservableList<String>(d_inner, d_filter);
	}
	
	@Test
	public void testContents() {
		assertEquals("Gert", d_outer.get(0));
		assertEquals("Jan", d_outer.get(1));
		assertEquals(2, d_outer.size());
		
		assertEquals("Gert", d_outer.getElementAt(0));
		assertEquals("Jan", d_outer.getElementAt(1));
		assertEquals(2, d_outer.getSize());
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testAddNotSupported() {
		d_outer.add("Test");
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSetNotSupported() {
		d_outer.set(2, "Test");
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testRemoveNotSupported() {
		d_outer.remove("Gert");
	}
	
	@Test
	public void testContentsUpdateAddEnd() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_ADDED, 2, 2)));
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.add("Bart");
		assertEquals("Bart", d_outer.get(2));
		verify(mock);
	}

	@Test
	public void testContentsUpdateAddIndex() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_ADDED, 1, 1)));
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.add(2, "Henk");
		assertEquals(Arrays.asList("Gert", "Henk", "Jan"), d_outer);
		verify(mock);
	}
	
	@Test
	public void testContentsUpdateAddNone() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.add(2, "Haank");
		assertEquals(Arrays.asList("Gert", "Jan"), d_outer);
		verify(mock);
	}
	
	@Test
	public void testContentsUpdateAddAllIndex() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_ADDED, 1, 2)));
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.addAll(2, Arrays.asList("Henk", "Bart"));
		assertEquals(Arrays.asList("Gert", "Henk", "Bart", "Jan"), d_outer);
		verify(mock);
	}

	@Test
	public void testContentsUpdateAddAllEnd() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_ADDED, 2, 3)));
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.addAll(Arrays.asList("Henk", "Bart"));
		assertEquals(Arrays.asList("Gert", "Jan", "Henk", "Bart"), d_outer);
		verify(mock);
	}
	
	@Test
	public void testContentsUpdateRemoveEnd() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_REMOVED, 1, 1)));
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.remove("Jan");
		assertEquals(Arrays.asList("Gert"), d_outer);
		verify(mock);
	}
	
	@Test
	public void testContentsUpdateRemoveStart() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_REMOVED, 0, 0)));
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.remove("Gert");
		assertEquals(Arrays.asList("Jan"), d_outer);
		verify(mock);
	}
	
	@Test
	public void testContentsUpdateRemoveNone() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.remove("Daan");
		assertEquals(Arrays.asList("Gert", "Jan"), d_outer);
		verify(mock);
	}
	
	@Test
	public void testContentsUpdateRemoveAll() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_REMOVED, 0, 1)));
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.clear();
		assertEquals(Collections.emptyList(), d_outer);
		verify(mock);
	}
	
	@Test
	public void testContentsUpdateSetNoChangeIncl() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.contentsChanged(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.CONTENTS_CHANGED, 1, 1)));
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.set(2, "Kees");
		assertEquals(Arrays.asList("Gert", "Kees"), d_outer);
		verify(mock);
	}
	
	@Test
	public void testContentsUpdateSetNoChangeExcl() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.set(3, "Paard");
		assertEquals(Arrays.asList("Gert", "Jan"), d_outer);
		verify(mock);
	}
	
	@Test
	public void testContentsUpdateSetChangeAdd() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_ADDED, 1, 1)));
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.set(1, "Kees");
		assertEquals(Arrays.asList("Gert", "Kees", "Jan"), d_outer);
		verify(mock);
	}
	
	@Test
	public void testContentsUpdateSetChangeRemove() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_REMOVED, 1, 1)));
		replay(mock);
		d_outer.addListDataListener(mock);
		d_inner.set(2, "Paard");
		assertEquals(Arrays.asList("Gert"), d_outer);
		verify(mock);
	}
	
	@Test
	public void testSetFilter() {
		ListDataListener mock = createStrictMock(ListDataListener.class);
		mock.intervalRemoved(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_REMOVED, 0, 1)));
		mock.intervalAdded(ListDataEventMatcher.eqListDataEvent(new ListDataEvent(d_outer, ListDataEvent.INTERVAL_ADDED, 0, 2)));
		replay(mock);
		d_outer.addListDataListener(mock);
		
		d_outer.setFilter(new FilteredObservableList.Filter<String>() {
			public boolean accept(String str) {
				return !str.equals("Gert");
			}
		});
		assertEquals(Arrays.asList("Daan", "Jan", "Klaas"), d_outer);
		verify(mock);
	}
}
