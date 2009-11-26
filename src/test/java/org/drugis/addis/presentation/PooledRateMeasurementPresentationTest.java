package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;

import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.BasicRateMeasurement;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.PooledRateMeasurement;
import org.drugis.addis.entities.RateMeasurement;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class PooledRateMeasurementPresentationTest {
	Endpoint d_e;
	BasicRateMeasurement d_m1;
	BasicRateMeasurement d_m2;
	PooledRateMeasurement d_m;
	BasicPatientGroup d_g1;
	BasicPatientGroup d_g2;
	RateMeasurementPresentation d_presentation;
	
	@Before
	public void setUp() {
		d_e = new Endpoint("e0", Type.RATE);
		d_g1 = new BasicPatientGroup(null, null, 100);
		d_m1 = new BasicRateMeasurement(d_e, 12, d_g1);
		d_g2 = new BasicPatientGroup(null, null, 50);
		d_m2 = new BasicRateMeasurement(d_e, 18, d_g2);
		d_m = new PooledRateMeasurement(Arrays.asList(new RateMeasurement[] {d_m1, d_m2}));
		d_presentation = new RateMeasurementPresentation(d_m);
	}
	
	@Test
	public void testFireLabelChangedOnRate() {
		AbstractValueModel lm = d_presentation.getLabelModel();
		PropertyChangeListener l = JUnitUtil.mockListener(
				lm, "value", "30/150", "40/150");
		lm.addPropertyChangeListener(l);
		d_m1.setRate(d_m1.getRate() + 10);
		verify(l);
	}
	
	@Test
	public void testFireLabelChangedOnSampleSize() {
		AbstractValueModel lm = d_presentation.getLabelModel();
		PropertyChangeListener l = JUnitUtil.mockListener(
				lm, "value", "30/150", "30/250");
		lm.addPropertyChangeListener(l);
		d_g1.setSize(d_m1.getSampleSize() + 100);
		verify(l);
	}
}
