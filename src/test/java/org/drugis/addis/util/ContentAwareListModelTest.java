package org.drugis.addis.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.drugis.addis.entities.Arm;
import org.easymock.IArgumentMatcher;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;


public class ContentAwareListModelTest {

	class ListDataEventMatcher implements IArgumentMatcher {
		private ListDataEvent d_expected;

		public ListDataEventMatcher(ListDataEvent expected) {
			d_expected = expected;
		}

		public void appendTo(StringBuffer buffer) {
			buffer.append("ListDataEventMatcher(");
			buffer.append("source = " + d_expected.getSource() + ", ");
			buffer.append("type = " + d_expected.getType() + ", ");
			buffer.append("index0 = " + d_expected.getIndex0() + ", ");
			buffer.append("index1 = " + d_expected.getIndex1() + ")");
		}

		public boolean matches(Object a) {
			if (!(a instanceof ListDataEvent)) {
				return false;
			}
			ListDataEvent actual = (ListDataEvent)a;
			return actual.getSource() == d_expected.getSource() &&
			actual.getType() == d_expected.getType() &&
			actual.getIndex0() == d_expected.getIndex0() &&
			actual.getIndex1() == d_expected.getIndex1();
		}
	};
	
	public ListDataEvent eqListDataEvent(ListDataEvent in) {
	    reportMatcher(new ListDataEventMatcher(in));
	    return null;
	    
	}
	
	private ObservableList<Arm> d_list;
	private ContentAwareListModel<Arm> d_contentAware;

	@Before
	public void setUp() {
		d_list = new ArrayListModel<Arm>();
		d_list.add(new Arm("My arm!", 0));
		d_contentAware = new ContentAwareListModel<Arm>(d_list);
	}
	
	@Test
	public void testAddElementsFiresChange() {
		ListDataListener mockListenerAdd = createMock(ListDataListener.class);
		mockListenerAdd.intervalAdded(eqListDataEvent(new ListDataEvent(d_contentAware, ListDataEvent.INTERVAL_ADDED, 1, 1)));
		replay(mockListenerAdd);
		
		d_contentAware.addListDataListener(mockListenerAdd);
		
		d_list.add(new Arm("His arm!", 0));
		verify(mockListenerAdd);
		d_contentAware.removeListDataListener(mockListenerAdd);
		
		ListDataListener mockListenerRemove = createMock(ListDataListener.class);
		mockListenerRemove.intervalRemoved(eqListDataEvent(new ListDataEvent(d_contentAware, ListDataEvent.INTERVAL_REMOVED, 0, 0)));
		replay(mockListenerRemove);
		
		d_contentAware.addListDataListener(mockListenerRemove);
		
		d_list.remove(0);
		verify(mockListenerRemove);
		d_contentAware.removeListDataListener(mockListenerRemove);
	}
	
	@Test
	public void testNameChangeFiresChange() {
		Arm arm = new Arm("My arm!", 0);
		d_list.add(arm);

		ListDataListener mockListener = createMock(ListDataListener.class);
		mockListener.contentsChanged(eqListDataEvent(new ListDataEvent(d_contentAware, ListDataEvent.CONTENTS_CHANGED, 0, 1)));
		replay(mockListener);
		
		d_contentAware.addListDataListener(mockListener);
		arm.setName("His Arm!");
		verify(mockListener);
		d_contentAware.removeListDataListener(mockListener);
		
		// also test listening to elements initially in the list.
		d_contentAware = new ContentAwareListModel<Arm>(d_list);
		
		ListDataListener mockListener1 = createMock(ListDataListener.class);
		mockListener1.contentsChanged(eqListDataEvent(new ListDataEvent(d_contentAware, ListDataEvent.CONTENTS_CHANGED, 0, 1)));
		replay(mockListener1);
		
		d_contentAware.addListDataListener(mockListener1);
		
		d_list.get(0).setName("His Arm!");
		verify(mockListener1);
		d_contentAware.removeListDataListener(mockListener1);
	}
	
}
