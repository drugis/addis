package nl.rug.escher.entities.test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import nl.rug.escher.entities.PatientGroup;
import nl.rug.escher.entities.Study;



import com.jgoodies.binding.beans.Model;

public class Helper {
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
	
	private static Method getAdderMethod(Model source, String methodName, Object toAdd) 
	throws NoSuchMethodException {
		return source.getClass().getMethod(methodName, toAdd.getClass());
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

	static void testAdder(Study source, String propertyName, PatientGroup g2) {
		testAdder(source, propertyName, null, g2);
	}

	static void testAdder(Study source, String propertyName, PatientGroup g2, String propertySingular) {
		testAdder(source, propertyName, propertySingular, g2);
	}

	@SuppressWarnings("unchecked")
	static void testAdder(Model source, String propertyName, String methodName, Object g2) {
		List list1 = new ArrayList();
		List list2 = new ArrayList();
		list2.add(g2);
		
		PropertyChangeListener mock = createMock(PropertyChangeListener.class);
		mock.propertyChange(eqEvent(new PropertyChangeEvent(
				source, propertyName, list1, list2)));
		replay(mock);
		
		source.addPropertyChangeListener(mock);
		Object actual = null;
		try {
			getAdderMethod(source, methodName, g2).invoke(source, g2);
			actual = getGetterMethod(source, propertyName).invoke(source);
		} catch (Exception e) {
			fail(e.toString());
		}
		
		assertTrue(((List)actual).contains(g2));
		assertEquals(1, ((List)actual).size());
		verify(mock);
	}

}
