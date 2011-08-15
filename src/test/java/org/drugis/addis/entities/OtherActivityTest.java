package org.drugis.addis.entities;

import org.drugis.common.JUnitUtil;
import org.junit.Test;

public class OtherActivityTest {
	
	@Test
	public void testPropertyChange() {
		OtherActivity oa = new OtherActivity("Foo");
		JUnitUtil.testSetter(oa, OtherActivity.PROPERTY_DESCRIPTION, "Foo", "Bar");
	}
}
