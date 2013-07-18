/*
 * This file is part of ADDIS (Aggregate Data Drug Information System).
 * ADDIS is distributed from http://drugis.org/.
 * Copyright © 2009 Gert van Valkenhoef, Tommi Tervonen.
 * Copyright © 2010 Gert van Valkenhoef, Tommi Tervonen, Tijs Zwinkels,
 * Maarten Jacobs, Hanno Koeslag, Florin Schimbinschi, Ahmad Kamal, Daniel
 * Reid.
 * Copyright © 2011 Gert van Valkenhoef, Ahmad Kamal, Daniel Reid, Florin
 * Schimbinschi.
 * Copyright © 2012 Gert van Valkenhoef, Daniel Reid, Joël Kuiper, Wouter
 * Reckman.
 * Copyright © 2013 Gert van Valkenhoef, Joël Kuiper.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
