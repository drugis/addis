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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import com.jgoodies.binding.beans.Model;

public class JUnitUtil {
	public static void testSetter(Model source, String propertyName, Object oldValue, Object newValue) {
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


	public static PropertyChangeListener mockListener(Model source,
			String propertyName, Object oldValue, Object newValue) {
		PropertyChangeListener mock = createMock(PropertyChangeListener.class);
		PropertyChangeEvent event = new PropertyChangeEvent(
				source, propertyName, oldValue, newValue);
		mock.propertyChange(eqEvent(event));
		mock.propertyChange(not(eqEvent(event)));
		expectLastCall().anyTimes();
		replay(mock);
		return mock;
	}
	
	
	private static Method getGetterMethod(Model source, String propertyName)
			throws NoSuchMethodException {
		return source.getClass().getMethod(deriveGetter(propertyName));
	}

	private static Method getSetterMethod(Model source, String propertyName,
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
	
	private static Method get1ParamMethod(Model source, String methodName, Object methodParam) 
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

	public static PropertyChangeEvent eqEvent(PropertyChangeEvent in) {
	    reportMatcher(new PropertyChangeEventMatcher(in));
	    return null;
	}

	@SuppressWarnings("unchecked")
	public static void testAdder(Model source, String propertyName, String methodName, Object toAdd) {
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
	public static void testAdderSet(Model source, String propertyName, String methodName, Object toAdd) {
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
	public static void testDeleterSet(Model source, String propertyName, String deleteMethodName, Object toDelete) throws Exception {
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

}
