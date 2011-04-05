package org.drugis.addis.entities;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class StudyActivityTest {
	private StudyActivity d_undefined;
	private StudyActivity d_randomization;
	private String d_rndTitle;
	private PredefinedActivity d_rndActivity;
	
	@Before
	public void setUp() {
		d_undefined = new StudyActivity(null, null);
		d_rndTitle = "Randomization";
		d_rndActivity = PredefinedActivity.RANDOMIZATION;
		d_randomization = new StudyActivity(d_rndTitle, d_rndActivity);
	}

	@Test
	public void testConstruction() {
		assertEquals(null, d_undefined.getName());
		assertEquals(null, d_undefined.getActivity());
		assertEquals(d_rndTitle, d_randomization.getName());
		assertEquals(d_rndActivity, d_randomization.getActivity());
		assertEquals(Collections.emptyList(), d_randomization.getNotes());
		assertEquals(Collections.emptyList(), d_randomization.getUsedBy());
	}
	
	@Test
	public void testSetName() {
		JUnitUtil.testSetter(d_undefined, StudyActivity.PROPERTY_NAME, null, "Screening");
	}
	
	@Test
	public void testSetActivity() {
		JUnitUtil.testSetter(d_randomization, StudyActivity.PROPERTY_ACTIVITY, d_rndActivity, PredefinedActivity.SCREENING);
	}
	
	@Test
	public void testEquals() {
		// TODO
	}
	 
	@Test
	public void testDependencies() {
		// TODO
	}
	
	@Test
	public void testSetUsedBy() {
		// TODO
	}
	
	@Test @Ignore
	public void testNotes() {
		assertEquals(Collections.emptyList(), d_randomization.getNotes());
		Note n = new Note(Source.MANUAL, "Zis is a note");
		d_randomization.getNotes().add(n);
		assertEquals(Collections.singletonList(n), d_randomization.getNotes());
	}
}