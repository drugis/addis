package org.drugis.addis.entities;

import org.drugis.addis.ExampleData;
import org.junit.Before;
import org.junit.Test;

public class StudyArmsEntryTest {

	@Before
	public void setUp() {
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testArmNotInStudy() {
		Study usedStudy = ExampleData.buildStudyBennie();
		Study notUsedStudy = ExampleData.buildStudyChouinard();
		Arm goodArm = usedStudy.getArms().get(0);
		Arm badArm = notUsedStudy.getArms().get(0);
		new StudyArmsEntry(usedStudy,goodArm,badArm);
	}
	
}
