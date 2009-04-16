package nl.rug.escher.entities.test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;



import com.jgoodies.binding.beans.Model;

public class Helper {
	public static void testSetter(Model source, String propertyName, Object oldValue, Object newValue) {
		String setter = deriveSetter(propertyName);
		String getter = deriveGetter(propertyName);
		
		PropertyChangeListener mock = createMock(PropertyChangeListener.class);
		mock.propertyChange(eqEvent(new PropertyChangeEvent(
				source, propertyName, oldValue, newValue)));
		replay(mock);
		
		source.addPropertyChangeListener(mock);
		Object desc = null;
		try {
			source.getClass().getMethod(setter, newValue.getClass()).invoke(source, newValue);
			desc = source.getClass().getMethod(getter).invoke(source);
		} catch (Exception e) {
			fail(e.toString());
		}
			
		assertEquals(newValue, desc);
		verify(mock);
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
