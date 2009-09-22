package org.drugis.common;

import static org.junit.Assert.*;

import org.drugis.common.StudentTTable;
import org.junit.Test;

public class StudentTTableTest {
	@Test
	public void testTable() {
		// Note that the below tests are focused on the old implementation where a lookup table was
		// used. However, the new implementation satisfies them.
		
		// exact lookup values
		assertEquals(12.706, StudentTTable.getT(1), 0.001);
		assertEquals(1.998, StudentTTable.getT(63), 0.001);
		
		// out-of-range values (estimated as 1.96)
		assertEquals(1.96, StudentTTable.getT(1000), 0.01);
		assertEquals(1.96, StudentTTable.getT(2000), 0.01);
		
		// linear interpolation values
		assertEquals(StudentTTable.getT(160) / 2 + StudentTTable.getT(140) / 2,
				StudentTTable.getT(150), 0.01);
		assertEquals(3 * StudentTTable.getT(160) / 4 + StudentTTable.getT(140) / 4,
				StudentTTable.getT(155), 0.01);
	}
}
