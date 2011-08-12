package org.drugis.addis.presentation;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.OtherActivity;
import static org.easymock.EasyMock.*;
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
	
	@Test
	public void testPropertyChange() {
		PropertyChangeListener mock = createMock(PropertyChangeListener.class);
		mock.propertyChange(new PropertyChangeEvent(d_oa, OtherActivity.PROPERTY_DESCRIPTION, "Foo", "Bar"));
		replay();
		d_oa.addPropertyChangeListener(mock);
		d_oa.setDescription("Bar");
		verify();
	}
}
