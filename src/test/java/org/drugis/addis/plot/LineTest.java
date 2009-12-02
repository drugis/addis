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
		assertTrue(!unexpected.equals(actual));
	}
	
	@Test
	public void testDiscriminateP2() {
		Line unexpected = new Line(10, 20, 30, 45);
		Line2D actual = new Line2D.Double(10, 20, 30, 40);
		assertTrue(!unexpected.equals(actual));
	}
	
	@Test
	public void testToString() {
		Line l = new Line(10,20,30,40);
		assertEquals("Line(x1 = 10.0, y1 = 20.0, x2 = 30.0, y2 = 40.0)", l.toString());
	}
}
