package org.drugis.addis.util;

import static org.junit.Assert.*;

import org.drugis.addis.entities.Arm;
import org.junit.Test;

public class RebuildableHashSetTest {
	
	@Test
	public void testRebuild() {
		Arm arm1 = new Arm("Arm1", 0);
		Arm arm2 = new Arm("Arm2", 0);
		
		RebuildableHashSet<Arm> set = new RebuildableHashSet<Arm>();
		set.add(arm1);
		set.add(arm2);
		
		arm1.setName("Arm8");
		assertFalse(set.contains(arm1));
		
		set.rebuild();
		assertTrue(set.contains(arm1));
	}
	
}
