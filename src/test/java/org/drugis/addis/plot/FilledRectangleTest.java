package org.drugis.addis.plot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.awt.Color;
import java.awt.Rectangle;

import org.junit.Test;

public class FilledRectangleTest {
	@Test
	public void testDefaultColor() {
		FilledRectangle expected = new FilledRectangle(1, 2, 3, 4, Color.BLACK);
		FilledRectangle actual = new FilledRectangle(1, 2, 3, 4);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testConstructWithRect() {
		FilledRectangle expected = new FilledRectangle(1, 2, 3, 4);
		FilledRectangle actual = new FilledRectangle(new Rectangle(1, 2, 3, 4));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testConstructWithRect2() {
		FilledRectangle expected = new FilledRectangle(1, 2, 3, 4, Color.PINK);
		FilledRectangle actual = new FilledRectangle(new Rectangle(1, 2, 3, 4), Color.PINK);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDiscriminateColor() {
		FilledRectangle unexpected = new FilledRectangle(1, 2, 3, 4, Color.BLACK);
		FilledRectangle actual = new FilledRectangle(1, 2, 3, 4, Color.PINK);
		assertNotSame(unexpected, actual);
	}
	
	@Test
	public void testDiscriminateX() {
		FilledRectangle unexpected = new FilledRectangle(1, 2, 3, 4, Color.BLACK);
		FilledRectangle actual = new FilledRectangle(100, 2, 3, 4, Color.BLACK);
		assertNotSame(unexpected, actual);
	}
	
	@Test
	public void testDiscriminateY() {
		FilledRectangle unexpected = new FilledRectangle(1, 2, 3, 4, Color.BLACK);
		FilledRectangle actual = new FilledRectangle(1, 200, 3, 4, Color.BLACK);
		assertNotSame(unexpected, actual);
	}
	
	@Test
	public void testDiscriminateW() {
		FilledRectangle unexpected = new FilledRectangle(1, 2, 3, 4, Color.BLACK);
		FilledRectangle actual = new FilledRectangle(1, 2, 300, 4, Color.BLACK);
		assertNotSame(unexpected, actual);
	}
	
	@Test
	public void testDiscriminateH() {
		FilledRectangle unexpected = new FilledRectangle(1, 2, 3, 4, Color.BLACK);
		FilledRectangle actual = new FilledRectangle(1, 2, 3, 400, Color.BLACK);
		assertNotSame(unexpected, actual);
	}
}
