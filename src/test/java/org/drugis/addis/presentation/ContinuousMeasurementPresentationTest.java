package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.AbstractValueModel;

public class ContinuousMeasurementPresentationTest {
	private BasicContinuousMeasurement d_basicContinuousMeasurement;
	private BasicPatientGroup d_pg;
	private ContinuousMeasurementPresentation d_pres;
	
	@Before
	public void setUp() {
		d_pg = new BasicPatientGroup(null, null, 1);
		d_basicContinuousMeasurement = new BasicContinuousMeasurement(0.0, 0.0, d_pg.getSize());
		d_pres = new ContinuousMeasurementPresentation(d_basicContinuousMeasurement);
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("0.0 \u00B1 0.0", d_pres.getLabelModel().getValue());
	}
	
	@Test
	public void testFireStdDevChanged() {
		getMeasurement().setMean(25.5);
		AbstractValueModel lm = d_pres.getLabelModel();
		PropertyChangeListener l = JUnitUtil.mockStrictListener(
				lm, "value", "25.5 \u00B1 0.0", "25.5 \u00B1 1.1");
		lm.addPropertyChangeListener(l);
		getMeasurement().setStdDev(1.1);
		verify(l);
	}

	@Test
	public void testFireMeanChanged() {
		getMeasurement().setMean(25.5);
		getMeasurement().setStdDev(1.1);
		AbstractValueModel lm = d_pres.getLabelModel();
		PropertyChangeListener l = JUnitUtil.mockStrictListener(
				lm, "value", "25.5 \u00B1 1.1", "27.5 \u00B1 1.1");
		lm.addPropertyChangeListener(l);
		getMeasurement().setMean(27.5);
		verify(l);
	}
	
	private BasicContinuousMeasurement getMeasurement() {
		return d_basicContinuousMeasurement;
	}
}
