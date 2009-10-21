package org.drugis.addis.presentation;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;

import org.drugis.addis.entities.BasicContinuousMeasurement;
import org.drugis.addis.entities.BasicPatientGroup;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Endpoint.Type;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class ContinuousMeasurementPresentationTest {
	private Endpoint d_endpoint;
	private BasicContinuousMeasurement d_basicContinuousMeasurement;
	private BasicPatientGroup d_pg;
	private ContinuousMeasurementPresentation d_pres;
	
	@Before
	public void setUp() {
		d_endpoint = new Endpoint("X", Type.CONTINUOUS);
		d_pg = new BasicPatientGroup(null, null, null, 1);
		d_basicContinuousMeasurement = new BasicContinuousMeasurement(d_endpoint, d_pg);
		d_pres = new ContinuousMeasurementPresentation(d_basicContinuousMeasurement);
	}
	
	@Test
	public void testGetLabel() {
		assertEquals("0.0 \u00B1 0.0", d_pres.getLabel());
	}
	
	@Test
	public void testFireStdDevChanged() {
		getMeasurement().setMean(25.5);
		PropertyChangeListener l = JUnitUtil.mockStrictListener(
				d_pres, ContinuousMeasurementPresentation.PROPERTY_LABEL,
				"25.5 \u00B1 0.0", "25.5 \u00B1 1.1");
		d_pres.addPropertyChangeListener(l);
		getMeasurement().setStdDev(1.1);
		verify(l);
	}

	@Test
	public void testFireMeanChanged() {
		getMeasurement().setMean(25.5);
		getMeasurement().setStdDev(1.1);
		PropertyChangeListener l = JUnitUtil.mockStrictListener(
				d_pres, ContinuousMeasurementPresentation.PROPERTY_LABEL,
				"25.5 \u00B1 1.1", "27.5 \u00B1 1.1");
		d_pres.addPropertyChangeListener(l);
		getMeasurement().setMean(27.5);
		verify(l);
	}
	
	private BasicContinuousMeasurement getMeasurement() {
		return d_basicContinuousMeasurement;
	}
}
