package org.drugis.addis.util;

import static org.junit.Assert.*;

import org.drugis.addis.entities.Arm;
import org.junit.Test;

public class RebuildableHashMapTest {
	
	@Test
	public void testRebuild() {
		Arm arm1 = new Arm("Arm1", 0);
		Arm arm2 = new Arm("Arm2", 0);
		
		RebuildableHashMap<Arm, Object> map = new RebuildableHashMap<Arm, Object>();
		map.put(arm1, "bla");
		map.put(arm2, "Bla2");
		
		arm1.setName("Arm8");
		assertNull(map.get(arm1));
		
		map.rebuild();
		assertNotNull(map.get(arm1));
	}
}
