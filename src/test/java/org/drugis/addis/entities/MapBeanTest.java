package org.drugis.addis.entities;

import static org.easymock.EasyMock.verify;

import java.beans.PropertyChangeListener;
import java.util.Date;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class MapBeanTest {
	CharacteristicsMap d_map;
	
	@Before
	public void setUp() {
		 d_map = new CharacteristicsMap();
	}
	
	@Test(expected=RuntimeException.class)
	public void testRemove() {
		d_map.remove(BasicStudyCharacteristic.ALLOCATION);
	}
	
	@Test(expected=RuntimeException.class)
	public void testClear() {
		d_map.clear();
	}
	
	@Test
	public void testPutEmits() {
		PropertyChangeListener listener =
			JUnitUtil.mockStrictListener(d_map, MapBean.PROPERTY_CONTENTS, null, null);
		d_map.addPropertyChangeListener(listener);
		d_map.put(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.SINGLE_BLIND);
		verify(listener);
	}
		
	@Test
	public void testPutCorrectTypes() {
		d_map.put(BasicStudyCharacteristic.ALLOCATION, BasicStudyCharacteristic.Allocation.RANDOMIZED);
		d_map.put(BasicStudyCharacteristic.BLINDING, BasicStudyCharacteristic.Blinding.DOUBLE_BLIND);
		d_map.put(BasicStudyCharacteristic.OBJECTIVE, "Obj");
		d_map.put(BasicStudyCharacteristic.STUDY_END, new Date());
		d_map.put(BasicStudyCharacteristic.STATUS, BasicStudyCharacteristic.Status.ACTIVE);
	}
}
