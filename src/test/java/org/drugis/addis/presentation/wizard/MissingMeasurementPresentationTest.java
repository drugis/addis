package org.drugis.addis.presentation.wizard;

import static org.junit.Assert.assertEquals;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.AdverseEvent;
import org.drugis.addis.entities.Arm;
import org.drugis.addis.entities.BasicMeasurement;
import org.drugis.addis.entities.Study;
import org.junit.Before;
import org.junit.Test;

import com.jgoodies.binding.value.ValueModel;

public class MissingMeasurementPresentationTest {

	private MissingMeasurementPresentation d_mmp;
	private Study d_s;
	private ValueModel d_missing;
	private BasicMeasurement d_defaultMeasurement;
	private Arm d_a;
	private AdverseEvent d_v;

	@Before
	public void setUp() {
		d_s = ExampleData.buildStudyBennie();
		d_v = d_s.getAdverseEvents().get(0);
		d_a = d_s.getArms().get(0);
		d_mmp = new MissingMeasurementPresentation(d_s, d_v, d_a);
		d_defaultMeasurement = d_s.buildDefaultMeasurement(d_v, d_a);
		d_missing = d_mmp.getMissingModel();
	}
	
	@Test
	public void testInitialisation() {
		assertEquals(d_defaultMeasurement, d_mmp.getMeasurement());
		assertEquals(Boolean.TRUE, d_mmp.getMissingModel().getValue());
	}
	
	@Test
	public void testChangesPropagatingToStudy() {
		assertEquals(null, d_s.getMeasurement(d_v, d_a));
		d_missing.setValue(false);
		assertEquals(Boolean.FALSE, d_mmp.getMissingModel().getValue());
		assertEquals(d_defaultMeasurement, d_s.getMeasurement(d_v, d_a));
		
		d_missing.setValue(true);
		assertEquals(null, d_s.getMeasurement(d_v, d_a));
	}
	
}
