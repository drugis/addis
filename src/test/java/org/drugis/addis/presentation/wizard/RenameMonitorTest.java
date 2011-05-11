package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.Arm;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.ObservableList;

public class RenameMonitorTest {

	static class RenameMonitorImpl extends RenameMonitor<Arm> {
		public int d_cnt = 0;

		public RenameMonitorImpl(AddListItemsPresentation<Arm> listPresentation) {
			super(listPresentation);
		}

		@Override
		protected void renameDetected() {
			++d_cnt;
		}
	}

	private Arm d_a;
	private ObservableList<Arm> d_l;
	private AddArmsPresentation d_listPresentation;

	@Before
	public void setUp() {
		d_a = new Arm("test", 0);
		d_l = new ArrayListModel<Arm>();
		d_listPresentation = new AddArmsPresentation(d_l, null, 0);
	}
	
	@Test
	public void testFireChangeOnRename() {
		d_l.add(d_a);
		RenameMonitorImpl monitor = new RenameMonitorImpl(d_listPresentation); 
		d_a.setName("omg");
		assertEquals(1, monitor.d_cnt);
	}
	
	@Test
	public void testAddingWorks() {
		RenameMonitorImpl monitor = new RenameMonitorImpl(d_listPresentation);
		d_a.setName("Yo momma");
		assertEquals(0, monitor.d_cnt);
		d_l.add(d_a);
		assertEquals(0, monitor.d_cnt);
		d_a.setName("omg");
		assertEquals(1, monitor.d_cnt);
	}
	
	@Test
	public void testRemovingWorks() {
		d_l.add(d_a);
		RenameMonitorImpl monitor = new RenameMonitorImpl(d_listPresentation);

		d_a.setName("omg");
		assertEquals(1, monitor.d_cnt);
		d_l.remove(d_a);
		d_a.setName("back to the future");
		assertEquals(1, monitor.d_cnt);
	}
	
	@Test
	public void testReplacingListWorks() {
		d_l.add(d_a);
		RenameMonitorImpl monitor = new RenameMonitorImpl(d_listPresentation);
		Arm a2 = new Arm("Foo", 1);
		ArrayListModel<Arm> l2 = new ArrayListModel<Arm>();
		l2.add(a2);
		d_listPresentation.setList(l2);
		assertEquals(0, monitor.d_cnt);
		d_a.setName("Bar");
		assertEquals(0, monitor.d_cnt);
		a2.setName("Quz");
		assertEquals(1, monitor.d_cnt);
		l2.add(d_a);
		assertEquals(1, monitor.d_cnt);
		d_a.setName("Fqip-");
		assertEquals(2, monitor.d_cnt);
	}
	
}
