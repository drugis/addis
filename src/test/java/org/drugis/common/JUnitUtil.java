/*
	This file is part of JSMAA.
	(c) Tommi Tervonen, 2009	

    JSMAA is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    JSMAA is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with JSMAA.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.drugis.common;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.not;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.jgoodies.binding.beans.Model;
import com.jgoodies.binding.beans.Observable;
import com.jgoodies.binding.value.ValueModel;

public class JUnitUtil {
	public static void testSetter(Observable source, String propertyName, Object oldValue, Object newValue) {
		PropertyChangeListener mock = mockListener(source, propertyName, oldValue, newValue);
		
		source.addPropertyChangeListener(mock);
		Object desc = null;
		try {
			getSetterMethod(source, propertyName, newValue).invoke(source, newValue);
			desc = getGetterMethod(source, propertyName).invoke(source);
		} catch (Exception e) {
			fail(e.toString());
		}
			
		assertEquals(newValue, desc);
		verify(mock);
	}

	public static PropertyChangeListener mockListener(Object source,
			String propertyName, Object oldValue, Object newValue) {
		PropertyChangeListener mock = createMock(PropertyChangeListener.class);
		PropertyChangeEvent event = new PropertyChangeEvent(
				source, propertyName, oldValue, newValue);
		mock.propertyChange(eqPropertyChangeEvent(event));
		mock.propertyChange(not(eqPropertyChangeEvent(event)));
		expectLastCall().anyTimes();
		replay(mock);
		return mock;
	}
	
	public static PropertyChangeListener mockStrictListener(Observable source,
			String propertyName, Object oldValue, Object newValue) {
		PropertyChangeListener mock = createMock(PropertyChangeListener.class);
		PropertyChangeEvent event = new PropertyChangeEvent(
				source, propertyName, oldValue, newValue);
		mock.propertyChange(eqPropertyChangeEvent(event));
		replay(mock);
		return mock;
	}
	
	public static TableModelListener mockTableModelListener(TableModelEvent expected) {
		TableModelListener mock = createMock(TableModelListener.class);
		mock.tableChanged((TableModelEvent)eqEventObject(expected));
		replay(mock);
		return mock;
	}
	
	private static Method getGetterMethod(Observable source, String propertyName)
			throws NoSuchMethodException {
		return source.getClass().getMethod(deriveGetter(propertyName));
	}

	private static Method getSetterMethod(Observable source, String propertyName,
			Object newValue) throws NoSuchMethodException {
		Method[] methods = source.getClass().getMethods();
		for (Method m : methods) {
			if (m.getName().equals(deriveSetter(propertyName))) {
				return m;
			}
		}
		throw new NoSuchMethodException("Cannot find method " + deriveSetter(propertyName) + 
				" of class " + source.getClass().getCanonicalName());
	}
	
	private static Method get1ParamMethod(Observable source, String methodName, Object methodParam) 
	throws NoSuchMethodException {
		// TODO: we should check that the method has 1 param and that the param is correct type
		Method[] methods = source.getClass().getMethods();
		for (Method m : methods) {
			if (m.getName().equals(methodName)) {
				return m;
			}
		}
		throw new NoSuchMethodException("no method " + methodName);
	}
	
	private static String deriveGetter(String propertyName) {
		return "get" + capitalize(propertyName);
	}

	private static String deriveSetter(String propertyName) {
		return "set" + capitalize(propertyName);
	}

	private static String capitalize(String propertyName) {
		return propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
	}

	public static PropertyChangeEvent eqPropertyChangeEvent(PropertyChangeEvent in) {
	    reportMatcher(new PropertyChangeEventMatcher(in));
	    return null;
	}
	
	public static EventObject eqEventObject(EventObject in) {
	    reportMatcher(new EventObjectMatcher(in));
	    return null;
	}

	@SuppressWarnings("unchecked")
	public static void testAdder(Observable source, String propertyName, String methodName, Object toAdd) {
		List list1 = new ArrayList();
		List list2 = new ArrayList();
		list2.add(toAdd);
		
		PropertyChangeListener mock = mockListener(source, propertyName, list1, list2);
		source.addPropertyChangeListener(mock);
		Object actual = null;
		try {
			get1ParamMethod(source, methodName, toAdd).invoke(source, toAdd);
			actual = getGetterMethod(source, propertyName).invoke(source);
		} catch (Exception e) {
			fail(e.toString());
		}
		
		assertTrue(((List) actual).contains(toAdd));
		assertTrue(1 == ((List) actual).size());
		verify(mock);
	}

	@SuppressWarnings("unchecked")
	public static void testAdderSet(Observable source, String propertyName, String methodName, Object toAdd) {
		Set list1 = new HashSet();
		Set list2 = new HashSet();
		list2.add(toAdd);
		
		PropertyChangeListener mock = mockListener(source, propertyName, list1, list2);
		source.addPropertyChangeListener(mock);
		Object actual = null;
		try {
			get1ParamMethod(source, methodName, toAdd).invoke(source, toAdd);
			actual = getGetterMethod(source, propertyName).invoke(source);
		} catch (Exception e) {
			fail(e.toString());
		}
		
		assertTrue(((Collection) actual).contains(toAdd));
		assertTrue(1 == ((Collection) actual).size());
		verify(mock);
	}
	

	@SuppressWarnings("unchecked")
	public static void testDeleter(Model source, String propertyName, String deleteMethodName, Object toDelete) throws Exception {
		List list1 = new ArrayList();
		List list2 = new ArrayList();
		list1.add(toDelete);

		// set the parameter
		getSetterMethod(source, propertyName, list1).invoke(source, list1);

		PropertyChangeListener mock = mockListener(source, propertyName, list1, list2);
		source.addPropertyChangeListener(mock);		

		get1ParamMethod(source, deleteMethodName, toDelete).invoke(source, toDelete);		

		Object actual = getGetterMethod(source, propertyName).invoke(source);
		assertTrue(0 ==  ((List) actual).size());
		verify(mock);
	}

	@SuppressWarnings("unchecked")
	public static void testDeleterSet(Observable source, String propertyName, String deleteMethodName, Object toDelete) throws Exception {
		Set list1 = new HashSet();
		Set list2 = new HashSet();
		list1.add(toDelete);

		// set the parameter
		getSetterMethod(source, propertyName, list1).invoke(source, list1);

		PropertyChangeListener mock = mockListener(source, propertyName, list1, list2);
		source.addPropertyChangeListener(mock);		

		get1ParamMethod(source, deleteMethodName, toDelete).invoke(source, toDelete);		

		Object actual = getGetterMethod(source, propertyName).invoke(source);
		assertTrue(0 ==  ((Set) actual).size());
		verify(mock);
	}	

	public static void assertNotEquals(Object expected, Object actual) {
		if (expected == null) {
			assertTrue(actual != null);
		} else {
			assertFalse(expected.equals(actual));
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <B> B serializeObject(B b) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		
		oos.writeObject(b);
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		
		return (B) ois.readObject();
	}

	public static void testSetter(ValueModel vm,
			Object oldValue, Object newValue) {
		PropertyChangeListener mock = mockListener(vm, "value", oldValue, newValue);
		vm.addValueChangeListener(mock);
		
		vm.setValue(newValue);
		assertEquals(newValue, vm.getValue());
		verify(mock);
	}	
}
