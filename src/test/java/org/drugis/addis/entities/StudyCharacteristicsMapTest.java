package org.drugis.addis.entities;

import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.drugis.common.JUnitUtil;
import org.junit.Before;
import org.junit.Test;

public class StudyCharacteristicsMapTest {
	StudyCharacteristicsMap d_map;
	
	@Before
	public void setUp() {
		 d_map = new StudyCharacteristicsMap();
	}
	
	@Test
	public void testKeySet() {
		Set<StudyCharacteristic> keys = new HashSet<StudyCharacteristic>();
		keys.addAll(Arrays.asList(StudyCharacteristic.values()));
		assertEquals(keys, d_map.keySet());
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
			JUnitUtil.mockStrictListener(d_map, StudyCharacteristicsMap.PROPERTY_CONTENTS, null, null);
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
	
	@Test(expected=IllegalArgumentException.class)
	public void testPutIncorrectType() {
		d_map.put(StudyCharacteristic.ARMS, StudyCharacteristic.Allocation.RANDOMIZED);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testPutNonpositiveInteger() {
		d_map.put(StudyCharacteristic.ARMS, new Integer(0));
	}
}
