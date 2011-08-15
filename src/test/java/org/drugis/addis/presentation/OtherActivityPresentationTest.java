package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.entities.OtherActivity;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class OtherActivityPresentationTest {

	private OtherActivity d_oa;
	private OtherActivityPresentation d_oap;
	private ValueModel d_dm;

	@Before
	public void setUp() {
		d_oa = new OtherActivity("Foo");
		d_oap = new OtherActivityPresentation(d_oa);
		d_dm = d_oap.getDescriptionModel();
	}
	
	@Test
	public void testChangePropagates() {
		d_dm.setValue("foo");
		assertEquals(d_oa.getDescription(), d_dm.getValue());
	}

}
