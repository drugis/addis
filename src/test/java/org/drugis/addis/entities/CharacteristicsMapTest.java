package org.drugis.addis.entities;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

public class CharacteristicsMapTest {

	@Test
	public void testGetDependencies() {
		 CharacteristicsMap map = new CharacteristicsMap();
		 map.put(BasicStudyCharacteristic.INCLUSION, "TEST");
		 assertEquals(Collections.EMPTY_SET, map.getDependencies());
	}
}
