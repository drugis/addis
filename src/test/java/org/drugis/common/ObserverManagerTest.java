package org.drugis.common;

import static org.easymock.EasyMock.*;

import java.beans.PropertyChangeListener;

import org.junit.Test;

import com.jgoodies.binding.beans.Observable;

public class ObserverManagerTest {
	@Test
	public void testSingleListener() {
		Observable source = createMock(Observable.class);
		ObserverManager d_om = new ObserverManager(source);
		
		PropertyChangeListener listener = JUnitUtil.mockStrictListener(source, "test", "X", "Y");
		d_om.addPropertyChangeListener(listener);
		d_om.firePropertyChange("test", "X", "Y");
		verify(listener);
	}
	
	@Test
	public void testMultipleListener() {
		Observable source = createMock(Observable.class);
		ObserverManager d_om = new ObserverManager(source);
		
		PropertyChangeListener listener0 = JUnitUtil.mockStrictListener(source, "test", "X", "Y");
		d_om.addPropertyChangeListener(listener0);
		PropertyChangeListener listener1 = JUnitUtil.mockStrictListener(source, "test", "X", "Y");
		d_om.addPropertyChangeListener(listener1);
		d_om.firePropertyChange("test", "X", "Y");
		verify(listener0);
		verify(listener1);
	}
	
	@Test
	public void testRemoveListener() {
		Observable source = createMock(Observable.class);
		ObserverManager d_om = new ObserverManager(source);
		
		PropertyChangeListener listener = createMock(PropertyChangeListener.class);
		replay(listener);
		
		d_om.addPropertyChangeListener(listener);
		d_om.removePropertyChangeListener(listener);
		d_om.firePropertyChange("test", "X", "Y");
		verify(listener);
	}
}
