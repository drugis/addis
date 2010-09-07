package org.drugis.common;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

public class DateUtilTest {
	@SuppressWarnings("deprecation")
	@Test
	public void testGetCurrentDateWithoutTime() {
		Date now = new Date();
		Date woTime = DateUtil.getCurrentDateWithoutTime();
		
		assertEquals(now.getYear(), woTime.getYear());
		assertEquals(now.getMonth(), woTime.getMonth());
		assertEquals(now.getDay(), woTime.getDay());
		assertEquals(0, woTime.getHours());
		assertEquals(0, woTime.getMinutes());
		assertEquals(0, woTime.getSeconds());
	}
}
