package nl.rug.escher.entities.test;

import static org.easymock.EasyMock.*;

import java.beans.PropertyChangeListener;

import org.junit.Test;

import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.entities.Measurement;
import nl.rug.escher.entities.PatientGroup;

public class MeasurementTest {
	@Test
	public void testSetMean() {
		Helper.testSetter(new Measurement(), Measurement.PROPERTY_MEAN, null, 25.91);
	}
	
	@Test
	public void testSetStdDev() {
		Helper.testSetter(new Measurement(), Measurement.PROPERTY_STDDEV, null, 0.46);
	}
	
	@Test
	public void testSetPatientGroup() {
		Helper.testSetter(new Measurement(), Measurement.PROPERTY_PATIENTGROUP, null, new PatientGroup());
	}
	
	@Test
	public void testSetEndpoint() {
		Helper.testSetter(new Measurement(), Measurement.PROPERTY_ENDPOINT, null, new Endpoint());
	}
	
	@Test
	public void testFireLabelChanged() {
		Measurement measurement = new Measurement();
		measurement.setMean(25.5);
		PropertyChangeListener l = Helper.mockListener(
				measurement, Measurement.PROPERTY_LABEL, "INCOMPLETE", "25.5 \u00B1 1.1");
		measurement.addPropertyChangeListener(l);
		measurement.setStdDev(1.1);
		verify(l);
		
		measurement.removePropertyChangeListener(l);
		l = Helper.mockListener(
				measurement, Measurement.PROPERTY_LABEL, "25.5 \u00B1 1.1", "27.5 \u00B1 1.1");
		measurement.addPropertyChangeListener(l);
		measurement.setMean(27.5);
		verify(l);
	}
}
