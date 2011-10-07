package org.drugis.addis.entities;

import javax.xml.datatype.Duration;

import org.drugis.addis.entities.WhenTaken.RelativeTo;
import org.drugis.addis.util.EntityUtil;
import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class WhenTakenTest {
	private Epoch d_epoch1;
	private Epoch d_epoch2;
	private WhenTaken d_wt;
	private Duration d_duration1;

	@Before
	public void setUp() {
		d_epoch1 = new Epoch("Je moeder", EntityUtil.createDuration("P31D"));
		d_epoch2 = new Epoch("Je vader", EntityUtil.createDuration("P33D"));
		d_duration1 = EntityUtil.createDuration("P29D");
		d_wt = new WhenTaken(d_duration1, RelativeTo.FROM_EPOCH_START, d_epoch1);
	}
	
	@Test
	public void testSetEpoch() {
		JUnitUtil.testSetter(d_wt, WhenTaken.PROPERTY_EPOCH, d_epoch1, d_epoch2);
	}
	
	@Test
	public void testSetRelativeTo() {
		JUnitUtil.testSetter(d_wt, WhenTaken.PROPERTY_RELATIVE_TO, RelativeTo.FROM_EPOCH_START, RelativeTo.BEFORE_EPOCH_END);
	}
	
	@Test
	public void testSetOffset() {
		Duration duration2 = EntityUtil.createDuration("P28D"); 
		JUnitUtil.testSetter(d_wt, WhenTaken.PROPERTY_OFFSET, d_duration1, duration2);
	}
}
