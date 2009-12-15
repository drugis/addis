package org.drugis.addis.entities;

import static org.easymock.EasyMock.verify;

import java.beans.PropertyChangeListener;
import java.util.Date;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyCharacteristicsMapTest {
	CharacteristicsMap d_map;
	
	@Before
	public void setUp() {
		 d_map = new CharacteristicsMap();
	}
	
	@Test(expected=RuntimeException.class)
	public void testRemove() {
		d_map.remove(StudyCharacteristic.INDICATION);
	}
	
	@Test(expected=RuntimeException.class)
	public void testClear() {
		d_map.clear();
	}
	
	@Test
	public void testPutEmits() {
		PropertyChangeListener listener =
			JUnitUtil.mockStrictListener(d_map, CharacteristicsMap.PROPERTY_CONTENTS, null, null);
		d_map.addPropertyChangeListener(listener);
		d_map.put(StudyCharacteristic.INDICATION, new Indication(0L, "Test"));
		verify(listener);
	}
		
	@Test
	public void testPutCorrectTypes() {
		d_map.put(StudyCharacteristic.ARMS, new Integer(2));
		d_map.put(StudyCharacteristic.ALLOCATION, StudyCharacteristic.Allocation.RANDOMIZED);
		d_map.put(StudyCharacteristic.BLINDING, StudyCharacteristic.Blinding.DOUBLE_BLIND);
		d_map.put(StudyCharacteristic.OBJECTIVE, "Obj");
		d_map.put(StudyCharacteristic.STUDY_END, new Date());
		d_map.put(StudyCharacteristic.STATUS, StudyCharacteristic.Status.ONGOING);
	}
}
