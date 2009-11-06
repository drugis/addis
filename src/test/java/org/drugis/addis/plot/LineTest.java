package org.drugis.addis.plot;

import static org.junit.Assert.*;

import java.awt.geom.Line2D;

import org.junit.Test;

public class LineTest {
	@Test
	public void testEqual() {
		Line expected = new Line(10, 20, 30, 40);
		Line2D actual = new Line2D.Double(10, 20, 30, 40);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDiscriminateP1() {
		Line unexpected = new Line(15, 20, 30, 40);
		Line2D actual = new Line2D.Double(10, 20, 30, 40);
		assertNotSame(unexpected, actual);
	}
	
	@Test
	public void testDiscriminateP2() {
		Line unexpected = new Line(10, 20, 30, 45);
		Line2D actual = new Line2D.Double(10, 20, 30, 40);
		assertNotSame(unexpected, actual);
	}
}
