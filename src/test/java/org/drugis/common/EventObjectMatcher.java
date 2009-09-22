package org.drugis.common;


import java.util.EventObject;

import org.easymock.IArgumentMatcher;

public class EventObjectMatcher  implements IArgumentMatcher {
	private EventObject d_expected;

	public EventObjectMatcher(EventObject expected) {
		d_expected = expected;
	}

	public void appendTo(StringBuffer buffer) {
		buffer.append("EventObjectMatcher(");
		buffer.append("class = " + d_expected.getClass() + ", ");
		buffer.append("source = " + d_expected.getSource() + ")");
	}
	
	public boolean eq(Object o1, Object o2) {
		if (o1 == null) {
			return o2 == null;
		}
		return o1.equals(o2);
	}

	public boolean matches(Object a) {
		if (!a.getClass().equals(d_expected.getClass())) {
			return false;
		}
		EventObject actual = (EventObject)a;
		
		return eq(actual.getSource(), d_expected.getSource());
	}
}
