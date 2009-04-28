package nl.rug.escher.entities.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;



import com.jgoodies.binding.beans.Model;

public class Helper {
	public static void testSetter(Model source, String propertyName, Object oldValue, Object newValue) {
		PropertyChangeListener mock = createMock(PropertyChangeListener.class);
		mock.propertyChange(eqEvent(new PropertyChangeEvent(
				source, propertyName, oldValue, newValue)));
		replay(mock);
		
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

}
